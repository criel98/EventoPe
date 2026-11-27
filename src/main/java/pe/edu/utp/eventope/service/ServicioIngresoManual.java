package pe.edu.utp.eventope.service;

import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.dao.IncidenciaDAO;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.util.HashUtil;
import pe.edu.utp.eventope.util.MotivosAcceso;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Ingreso manual justificado. EXCLUSIVO del administrador (ADMIN activo, verificado en la base de datos).
 *
 * Parte siempre de una incidencia ABIERTA creada por una denegación real y, en UNA transacción:
 *  1. bloquea la incidencia y la entrada (FOR UPDATE), así dos administradores simultáneos no pueden autorizar dos veces;
 *  2. exige que la entrada esté DISPONIBLE (una ANULADA o UTILIZADA nunca se recupera) y sea del mismo evento;
 *  3. exige que el motivo de la denegación sea recuperable (no fraude ni datos inexistentes);
 *  4. marca la entrada UTILIZADA, registra un nuevo control PERMITIDO con autorizado_por_id = administrador,
 *     cierra la incidencia con la justificación y avisa al dueño de la entrada.
 * Cualquier fallo revierte todo. La solicitud del nuevo control se deriva de la incidencia: aun sin los bloqueos,
 * la restricción UNIQUE de solicitud_id impediría un segundo ingreso manual para la misma incidencia.
 */
public final class ServicioIngresoManual {
    public static final int JUSTIFICACION_MIN = 15;
    public static final int JUSTIFICACION_MAX = 170;
    private static final String PREFIJO_RESOLUCION = "Ingreso manual autorizado: ";

    /** Resultado de una autorización correcta. */
    public static final class Resultado {
        private final int controlId;
        private final int entradaId;

        Resultado(int controlId, int entradaId) {
            this.controlId = controlId;
            this.entradaId = entradaId;
        }

        public int getControlId() { return controlId; }
        public int getEntradaId() { return entradaId; }
    }

    private final ProveedorConexion conexiones;
    private final Clock reloj;
    private final IncidenciaDAO incidencias = new IncidenciaDAO();

    public ServicioIngresoManual(ProveedorConexion conexiones, Clock reloj) {
        this.conexiones = conexiones;
        this.reloj = reloj;
    }

    /**
     * @throws SecurityException        el actor no es un administrador activo
     * @throws IllegalArgumentException justificación inválida o incidencia inexistente
     * @throws IllegalStateException    la incidencia o la entrada no admiten un ingreso manual
     */
    public Resultado autorizar(SesionUsuario actor, int incidenciaId, String justificacion) throws SQLException {
        String texto = validarJustificacion(justificacion);
        for (int intento = 0; ; intento++) {
            try {
                return intentar(actor, incidenciaId, texto);
            } catch (SQLException e) {
                // 1213 = deadlock, 1205 = espera de bloqueo agotada: reintento corto, igual que ServicioAcceso.
                if (intento >= 2 || (e.getErrorCode() != 1213 && e.getErrorCode() != 1205)) throw e;
            }
        }
    }

    private Resultado intentar(SesionUsuario actor, int incidenciaId, String justificacion) throws SQLException {
        try (Connection c = conexiones.abrir()) {
            c.setAutoCommit(false);
            try {
                AutorizacionPersonal.exigir(c, actor, SesionUsuario.Rol.ADMIN);

                String estado = incidencias.bloquearEstado(c, incidenciaId);
                if (estado == null) throw new IllegalArgumentException("La incidencia no existe.");
                if (!"ABIERTA".equals(estado)) {
                    throw new IllegalStateException("La incidencia ya fue cerrada; no se puede autorizar otro ingreso.");
                }

                Control control = control(c, incidenciaId);
                if (!"DENEGADO".equals(control.estado) || control.autorizadoPor != null) {
                    throw new IllegalStateException("Solo una denegación sin autorización previa admite ingreso manual.");
                }
                if (control.entradaId == null) {
                    throw new IllegalStateException("El intento no está asociado a una entrada: no hay nada que autorizar.");
                }
                if (!MotivosAcceso.admiteIngresoManual(control.motivo)) {
                    throw new IllegalStateException("Este motivo (" + MotivosAcceso.descripcion(control.motivo) + ") no admite ingreso manual.");
                }

                Entrada entrada = bloquearEntrada(c, control.entradaId);
                if ("ANULADA".equals(entrada.estado)) throw new IllegalStateException("La entrada está anulada: no se puede recuperar.");
                if (!"DISPONIBLE".equals(entrada.estado)) throw new IllegalStateException("La entrada ya fue utilizada: no se permite un segundo ingreso.");
                if (entrada.eventoId != control.eventoId) throw new IllegalStateException("La entrada pertenece a otro evento.");
                if (haIngresado(c, entrada.id)) throw new IllegalStateException("La entrada ya tiene un ingreso registrado.");

                Instant ahora = reloj.instant();
                LocalDateTime ahoraUtc = LocalDateTime.ofInstant(ahora, ZoneOffset.UTC);
                if (!consumir(c, entrada.id)) throw new IllegalStateException("La entrada ya fue utilizada: no se permite un segundo ingreso.");
                int controlNuevo = registrarControl(c, incidenciaId, control, actor.getId(), ahoraUtc);
                if (!incidencias.resolver(c, incidenciaId, "RESUELTA", actor.getId(), ahoraUtc, PREFIJO_RESOLUCION + justificacion)) {
                    throw new IllegalStateException("La incidencia ya fue cerrada; no se puede autorizar otro ingreso.");
                }
                ServicioNotificaciones.notificarIngresoManual(c, entrada.compradorId, control.evento, ahora);
                c.commit();
                return new Resultado(controlNuevo, entrada.id);
            } catch (SQLException | RuntimeException e) {
                ServicioIncidencias.deshacer(c, e);
                throw e;
            }
        }
    }

    // ------------------------------------------------------------------ acceso a datos propio de esta operación

    private record Control(int id, int eventoId, int dispositivoId, int operadorId, Integer entradaId,
                           String estado, String motivo, Integer autorizadoPor, String evento) {}

    private record Entrada(int id, int eventoId, int compradorId, String estado) {}

    private static Control control(Connection c, int incidenciaId) throws SQLException {
        try (PreparedStatement p = c.prepareStatement(
                "SELECT k.id, k.evento_id, k.dispositivo_id, k.operador_id, k.entrada_id, k.estado_acceso, k.motivo, k.autorizado_por_id, ev.nombre "
              + "FROM incidencia_acceso i JOIN control_acceso_puerta k ON k.id = i.control_id JOIN evento_cultural ev ON ev.id = k.evento_id "
              + "WHERE i.id = ?")) {
            p.setInt(1, incidenciaId);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) throw new IllegalArgumentException("La incidencia no existe.");
                int e = r.getInt(5);
                Integer entradaId = r.wasNull() ? null : e;
                int a = r.getInt(8);
                Integer autorizado = r.wasNull() ? null : a;
                return new Control(r.getInt(1), r.getInt(2), r.getInt(3), r.getInt(4), entradaId, r.getString(6), r.getString(7), autorizado, r.getString(9));
            }
        }
    }

    private static Entrada bloquearEntrada(Connection c, int id) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("SELECT id, evento_id, comprador_id, estado_uso FROM entrada_digital WHERE id = ? FOR UPDATE")) {
            p.setInt(1, id);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) throw new IllegalStateException("La entrada ya no existe.");
                return new Entrada(r.getInt(1), r.getInt(2), r.getInt(3), r.getString(4));
            }
        }
    }

    private static boolean haIngresado(Connection c, int entradaId) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("SELECT COUNT(*) FROM control_acceso_puerta WHERE entrada_id = ? AND estado_acceso = 'PERMITIDO'")) {
            p.setInt(1, entradaId);
            try (ResultSet r = p.executeQuery()) {
                r.next();
                return r.getInt(1) > 0;
            }
        }
    }

    private static boolean consumir(Connection c, int entradaId) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("UPDATE entrada_digital SET estado_uso = 'UTILIZADA' WHERE id = ? AND estado_uso = 'DISPONIBLE'")) {
            p.setInt(1, entradaId);
            return p.executeUpdate() == 1;
        }
    }

    private static int registrarControl(Connection c, int incidenciaId, Control origen, int adminId, LocalDateTime ahoraUtc) throws SQLException {
        String solicitud = UUID.nameUUIDFromBytes(("ingreso-manual-incidencia-" + incidenciaId).getBytes(StandardCharsets.UTF_8)).toString();
        String hash = HashUtil.sha256("MANUAL|" + incidenciaId + "|" + adminId);
        try (PreparedStatement p = c.prepareStatement(
                "INSERT INTO control_acceso_puerta(solicitud_id, solicitud_hash, evento_id, dispositivo_id, operador_id, entrada_id, hora_ingreso, estado_acceso, motivo, autorizado_por_id) "
              + "VALUES (?,?,?,?,?,?,?,'PERMITIDO',?,?)", Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, solicitud);
            p.setString(2, hash);
            p.setInt(3, origen.eventoId);
            p.setInt(4, origen.dispositivoId);
            p.setInt(5, origen.operadorId);
            p.setInt(6, origen.entradaId);
            p.setObject(7, ahoraUtc);
            p.setString(8, "INGRESO_MANUAL_INC_" + incidenciaId);
            p.setInt(9, adminId);
            p.executeUpdate();
            try (ResultSet r = p.getGeneratedKeys()) {
                if (!r.next()) throw new SQLException("Control manual sin id");
                return r.getInt(1);
            }
        }
    }

    static String validarJustificacion(String justificacion) {
        String t = justificacion == null ? "" : justificacion.strip().replaceAll("\\s+", " ");
        if (t.length() < JUSTIFICACION_MIN) throw new IllegalArgumentException("La justificación es obligatoria (mínimo " + JUSTIFICACION_MIN + " caracteres).");
        if (t.length() > JUSTIFICACION_MAX) throw new IllegalArgumentException("La justificación es demasiado larga (máximo " + JUSTIFICACION_MAX + " caracteres).");
        if (t.chars().anyMatch(Character::isISOControl)) throw new IllegalArgumentException("La justificación contiene caracteres no permitidos.");
        return t;
    }
}
