package pe.edu.utp.eventope.service;

import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.dao.NotificacionDAO;
import pe.edu.utp.eventope.dto.NotificacionDTO;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.util.FormatoFecha;
import pe.edu.utp.eventope.util.MotivosAcceso;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Notificaciones internas.
 * <ul>
 *   <li>Bandeja del cliente (instancia): solo ve y modifica las suyas; el id sale de la sesión, nunca del formulario.</li>
 *   <li>Emisión (métodos estáticos): se ejecutan dentro de la transacción del llamador, protegidos por un savepoint.
 *       Si el aviso falla, la operación principal (compra, ingreso) NO se pierde y el error queda en el registro.</li>
 * </ul>
 * Los textos nunca incluyen el contenido del QR, el DNI completo ni la clave del boleto.
 */
public final class ServicioNotificaciones {
    private static final Logger LOG = Logger.getLogger(ServicioNotificaciones.class.getName());
    private static final NotificacionDAO DAO = new NotificacionDAO();
    private static final int LIMITE_BANDEJA = 100;

    private final ProveedorConexion conexiones;

    public ServicioNotificaciones(ProveedorConexion conexiones) {
        this.conexiones = conexiones;
    }

    // ---------------------------------------------------------------- bandeja del cliente

    public List<NotificacionDTO> listar(SesionUsuario actor, boolean soloNoLeidas) throws SQLException {
        actor.exigirCliente();
        try (Connection c = conexiones.abrir()) {
            return DAO.listar(c, actor.getId(), soloNoLeidas, LIMITE_BANDEJA).stream().map(NotificacionDTO::new).toList();
        }
    }

    public int contarNoLeidas(SesionUsuario actor) throws SQLException {
        actor.exigirCliente();
        try (Connection c = conexiones.abrir()) {
            return DAO.contarNoLeidas(c, actor.getId());
        }
    }

    /** @return false si la notificación no existe o es de otro cliente. */
    public boolean marcarLeida(SesionUsuario actor, int notificacionId) throws SQLException {
        actor.exigirCliente();
        try (Connection c = conexiones.abrir()) {
            return DAO.marcarLeida(c, actor.getId(), notificacionId);
        }
    }

    public int marcarTodasLeidas(SesionUsuario actor) throws SQLException {
        actor.exigirCliente();
        try (Connection c = conexiones.abrir()) {
            return DAO.marcarTodasLeidas(c, actor.getId());
        }
    }

    // ---------------------------------------------------------------- emisión dentro de una transacción

    /**
     * Aviso de compra. Idempotente: si la misma confirmación se repite, el aviso idéntico (mismo código de compra)
     * no se duplica. Pensado para que Ethan lo invoque justo antes del commit de la compra.
     */
    public static void notificarCompra(Connection c, int clienteId, String evento, String codigoCompra, int cantidad, BigDecimal total) {
        String texto = String.format("Tu compra %s de %d %s para «%s» por S/ %s fue confirmada. Encontrarás tus boletos en «Mis entradas».",
                codigoCompra, cantidad, cantidad == 1 ? "entrada" : "entradas", evento, total == null ? "0.00" : total.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
        emitir(c, clienteId, TipoNotificacion.COMPRA.titulo("Compra confirmada"), texto, true);
    }

    /** Aviso por el resultado de un intento de acceso con una entrada identificada del cliente. */
    public static void notificarAcceso(Connection c, int clienteId, int eventoId, int dispositivoId, boolean permitido, String motivo, Instant cuando) {
        try {
            var ctx = DAO.contextoAcceso(c, eventoId, dispositivoId);
            String evento = ctx.map(NotificacionDAO.ContextoAcceso::evento).orElse("tu evento");
            String puerta = ctx.map(NotificacionDAO.ContextoAcceso::puerta).orElse("la puerta");
            String hora = FormatoFecha.lima(LocalDateTime.ofInstant(cuando, ZoneOffset.UTC));
            if (permitido) {
                emitir(c, clienteId, TipoNotificacion.VALIDACION.titulo("Entrada validada"),
                        String.format("Tu entrada para «%s» fue validada en %s el %s (hora de Lima).", evento, puerta, hora), false);
            } else {
                emitir(c, clienteId, TipoNotificacion.SEGURIDAD.titulo("Intento de ingreso rechazado"),
                        String.format("El %s (hora de Lima) se rechazó un intento de ingreso con una de tus entradas para «%s» en %s: %s. "
                                + "Si no reconoces este intento, no compartas capturas de tu QR y contacta a soporte.",
                                hora, evento, puerta, MotivosAcceso.descripcion(motivo)), false);
            }
        } catch (SQLException | RuntimeException e) {
            LOG.log(Level.WARNING, "No se pudo preparar la notificación de acceso; la operación principal continúa", e);
        }
    }

    /** Aviso cuando un administrador autoriza un ingreso manual con la entrada del cliente. */
    public static void notificarIngresoManual(Connection c, int clienteId, String evento, Instant cuando) {
        String hora = FormatoFecha.lima(LocalDateTime.ofInstant(cuando, ZoneOffset.UTC));
        emitir(c, clienteId, TipoNotificacion.VALIDACION.titulo("Ingreso autorizado por un administrador"),
                String.format("Un administrador autorizó manualmente el ingreso con tu entrada para «%s» el %s (hora de Lima). "
                        + "La entrada quedó marcada como utilizada.", evento, hora), false);
    }

    private static void emitir(Connection c, int clienteId, String titulo, String contenido, boolean unica) {
        Savepoint sp = null;
        try {
            if (!c.getAutoCommit()) sp = c.setSavepoint();
            if (unica) DAO.insertarSiNoExiste(c, clienteId, titulo, contenido);
            else DAO.insertar(c, clienteId, titulo, contenido);
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "No se pudo guardar la notificación; la operación principal continúa", e);
            if (sp != null) {
                try {
                    c.rollback(sp);
                } catch (SQLException ignorada) {
                    LOG.log(Level.WARNING, "No se pudo revertir al savepoint de la notificación", ignorada);
                }
            }
        }
    }
}
