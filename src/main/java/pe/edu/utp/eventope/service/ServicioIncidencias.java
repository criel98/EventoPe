package pe.edu.utp.eventope.service;

import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.dao.IncidenciaDAO;
import pe.edu.utp.eventope.dto.IncidenciaDTO;
import pe.edu.utp.eventope.dto.SesionUsuario;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Consulta y resolución de incidencias de acceso. Pueden ADMIN y SOPORTE; el personal de validación (guardias) no.
 * Al resolver queda guardado quién (resuelta_por_id), cuándo (fecha_resolucion, UTC) y por qué (motivo_resolucion).
 */
public final class ServicioIncidencias {
    public static final int TAMANO_PAGINA = 20;
    public static final int MOTIVO_MIN = 10;
    public static final int MOTIVO_MAX = 200;
    private static final Set<String> ESTADOS = Set.of("ABIERTA", "RESUELTA", "DESCARTADA");
    private static final Set<String> SEVERIDADES = Set.of("ALTA", "MEDIA", "BAJA");
    private static final Set<String> DECISIONES = Set.of("RESUELTA", "DESCARTADA");

    /** Página de resultados con getters (compatible con EL de Tomcat 10.1). */
    public static final class Pagina {
        private final List<IncidenciaDTO> incidencias;
        private final int pagina;
        private final int totalPaginas;
        private final int total;

        Pagina(List<IncidenciaDTO> incidencias, int pagina, int totalPaginas, int total) {
            this.incidencias = incidencias;
            this.pagina = pagina;
            this.totalPaginas = totalPaginas;
            this.total = total;
        }

        public List<IncidenciaDTO> getIncidencias() { return incidencias; }
        public int getPagina() { return pagina; }
        public int getTotalPaginas() { return totalPaginas; }
        public int getTotal() { return total; }
    }

    private final ProveedorConexion conexiones;
    private final Clock reloj;
    private final IncidenciaDAO dao = new IncidenciaDAO();

    public ServicioIncidencias(ProveedorConexion conexiones, Clock reloj) {
        this.conexiones = conexiones;
        this.reloj = reloj;
    }

    /** @param estado ABIERTA, RESUELTA, DESCARTADA o null/otro valor para todas; igual para severidad. */
    public Pagina listar(SesionUsuario actor, String estado, String severidad, int pagina) throws SQLException {
        String e = estado != null && ESTADOS.contains(estado) ? estado : null;
        String s = severidad != null && SEVERIDADES.contains(severidad) ? severidad : null;
        try (Connection c = conexiones.abrir()) {
            AutorizacionPersonal.exigir(c, actor, SesionUsuario.Rol.ADMIN, SesionUsuario.Rol.SOPORTE);
            int total = dao.contar(c, e, s);
            int totalPaginas = Math.max(1, (total + TAMANO_PAGINA - 1) / TAMANO_PAGINA);
            int actual = Math.min(Math.max(1, pagina), totalPaginas);
            return new Pagina(dao.listar(c, e, s, TAMANO_PAGINA, (actual - 1) * TAMANO_PAGINA), actual, totalPaginas, total);
        }
    }

    public Optional<IncidenciaDTO> detalle(SesionUsuario actor, int id) throws SQLException {
        try (Connection c = conexiones.abrir()) {
            AutorizacionPersonal.exigir(c, actor, SesionUsuario.Rol.ADMIN, SesionUsuario.Rol.SOPORTE);
            return dao.obtener(c, id);
        }
    }

    /**
     * Cierra una incidencia abierta sin autorizar ingreso.
     * @param decision RESUELTA (atendida) o DESCARTADA (falsa alarma)
     * @throws IllegalArgumentException datos inválidos o incidencia inexistente
     * @throws IllegalStateException    la incidencia ya estaba cerrada (no se sobrescribe la primera resolución)
     * @throws SecurityException        el actor no es personal activo ADMIN/SOPORTE
     */
    public void resolver(SesionUsuario actor, int incidenciaId, String decision, String motivo) throws SQLException {
        String texto = validarMotivo(motivo);
        if (decision == null || !DECISIONES.contains(decision)) throw new IllegalArgumentException("Decisión no válida.");
        try (Connection c = conexiones.abrir()) {
            c.setAutoCommit(false);
            try {
                AutorizacionPersonal.exigir(c, actor, SesionUsuario.Rol.ADMIN, SesionUsuario.Rol.SOPORTE);
                String estado = dao.bloquearEstado(c, incidenciaId);
                if (estado == null) throw new IllegalArgumentException("La incidencia no existe.");
                if (!"ABIERTA".equals(estado)) throw new IllegalStateException("La incidencia ya fue cerrada; no se puede resolver de nuevo.");
                if (!dao.resolver(c, incidenciaId, decision, actor.getId(), ahoraUtc(), texto)) {
                    throw new IllegalStateException("La incidencia ya fue cerrada; no se puede resolver de nuevo.");
                }
                c.commit();
            } catch (SQLException | RuntimeException e) {
                deshacer(c, e);
                throw e;
            }
        }
    }

    static String validarMotivo(String motivo) {
        String t = motivo == null ? "" : motivo.strip().replaceAll("\\s+", " ");
        if (t.length() < MOTIVO_MIN) throw new IllegalArgumentException("Explica el motivo (mínimo " + MOTIVO_MIN + " caracteres).");
        if (t.length() > MOTIVO_MAX) throw new IllegalArgumentException("El motivo es demasiado largo (máximo " + MOTIVO_MAX + " caracteres).");
        if (t.chars().anyMatch(Character::isISOControl)) throw new IllegalArgumentException("El motivo contiene caracteres no permitidos.");
        return t;
    }

    LocalDateTime ahoraUtc() {
        return LocalDateTime.ofInstant(reloj.instant(), ZoneOffset.UTC);
    }

    static void deshacer(Connection c, Exception causa) {
        try {
            c.rollback();
        } catch (SQLException rollback) {
            causa.addSuppressed(rollback);
        }
    }
}
