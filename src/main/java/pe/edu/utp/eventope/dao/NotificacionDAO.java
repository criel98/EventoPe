package pe.edu.utp.eventope.dao;

import pe.edu.utp.eventope.model.NotificacionUsuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Persistencia de notificacion_usuario. Usa la conexión del llamador: no confirma ni cierra. */
public final class NotificacionDAO {
    public record ContextoAcceso(String evento, String puerta) {}

    /** La fecha se guarda siempre en UTC (UTC_TIMESTAMP), sin depender de la zona horaria de la sesión de la base de datos. */
    public void insertar(Connection c, int clienteId, String titulo, String contenido) throws SQLException {
        try (PreparedStatement p = c.prepareStatement(
                "INSERT INTO notificacion_usuario(cliente_id, titulo_mensaje, contenido, fecha_envio) VALUES (?,?,?,UTC_TIMESTAMP())")) {
            p.setInt(1, clienteId);
            p.setString(2, titulo);
            p.setString(3, contenido);
            p.executeUpdate();
        }
    }

    /** Inserta solo si el mismo cliente no tiene ya un aviso idéntico (evita duplicados si se repite una confirmación). */
    public boolean insertarSiNoExiste(Connection c, int clienteId, String titulo, String contenido) throws SQLException {
        try (PreparedStatement p = c.prepareStatement(
                "SELECT COUNT(*) FROM notificacion_usuario WHERE cliente_id=? AND titulo_mensaje=? AND contenido=?")) {
            p.setInt(1, clienteId);
            p.setString(2, titulo);
            p.setString(3, contenido);
            try (ResultSet r = p.executeQuery()) {
                r.next();
                if (r.getInt(1) > 0) return false;
            }
        }
        insertar(c, clienteId, titulo, contenido);
        return true;
    }

    /** Siempre filtra por cliente: un cliente nunca recibe filas de otro. */
    public List<NotificacionUsuario> listar(Connection c, int clienteId, boolean soloNoLeidas, int limite) throws SQLException {
        String sql = "SELECT id, cliente_id, titulo_mensaje, contenido, fecha_envio, leido FROM notificacion_usuario "
                + "WHERE cliente_id=?" + (soloNoLeidas ? " AND leido=FALSE" : "") + " ORDER BY fecha_envio DESC, id DESC LIMIT ?";
        List<NotificacionUsuario> lista = new ArrayList<>();
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, clienteId);
            p.setInt(2, limite);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    lista.add(new NotificacionUsuario(r.getInt(1), r.getInt(2), r.getString(3), r.getString(4),
                            r.getObject(5, java.time.LocalDateTime.class), r.getBoolean(6)));
                }
            }
        }
        return lista;
    }

    public int contarNoLeidas(Connection c, int clienteId) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("SELECT COUNT(*) FROM notificacion_usuario WHERE cliente_id=? AND leido=FALSE")) {
            p.setInt(1, clienteId);
            try (ResultSet r = p.executeQuery()) {
                r.next();
                return r.getInt(1);
            }
        }
    }

    /** @return false si la notificación no existe o pertenece a otro cliente (no se distingue a propósito). */
    public boolean marcarLeida(Connection c, int clienteId, int id) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("SELECT id FROM notificacion_usuario WHERE id=? AND cliente_id=?")) {
            p.setInt(1, id);
            p.setInt(2, clienteId);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) return false;
            }
        }
        try (PreparedStatement p = c.prepareStatement("UPDATE notificacion_usuario SET leido=TRUE WHERE id=? AND cliente_id=?")) {
            p.setInt(1, id);
            p.setInt(2, clienteId);
            p.executeUpdate();
        }
        return true;
    }

    public int marcarTodasLeidas(Connection c, int clienteId) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("UPDATE notificacion_usuario SET leido=TRUE WHERE cliente_id=? AND leido=FALSE")) {
            p.setInt(1, clienteId);
            return p.executeUpdate();
        }
    }

    public Optional<ContextoAcceso> contextoAcceso(Connection c, int eventoId, int dispositivoId) throws SQLException {
        try (PreparedStatement p = c.prepareStatement(
                "SELECT e.nombre, d.nombre_puerta FROM evento_cultural e, dispositivo_validacion d WHERE e.id=? AND d.id=?")) {
            p.setInt(1, eventoId);
            p.setInt(2, dispositivoId);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? Optional.of(new ContextoAcceso(r.getString(1), r.getString(2))) : Optional.empty();
            }
        }
    }
}
