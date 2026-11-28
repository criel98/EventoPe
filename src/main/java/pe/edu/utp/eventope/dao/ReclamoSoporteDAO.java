package pe.edu.utp.eventope.dao;

import pe.edu.utp.eventope.model.ReclamoSoporte;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ReclamoSoporteDAO {

    private static final String SQL_INSERTAR =
            "INSERT INTO reclamo_soporte " +
            "(cliente_id, admin_id, incidencia_id, asunto, descripcion, estado_reclamo, fecha_creacion, respuesta) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_BUSCAR_ID =
            "SELECT id, cliente_id, admin_id, incidencia_id, asunto, descripcion, estado_reclamo, fecha_creacion, respuesta " +
            "FROM reclamo_soporte WHERE id = ?";

    private static final String SQL_LISTAR_CLIENTE =
            "SELECT id, cliente_id, admin_id, incidencia_id, asunto, descripcion, estado_reclamo, fecha_creacion, respuesta " +
            "FROM reclamo_soporte WHERE cliente_id = ? ORDER BY fecha_creacion DESC, id DESC";

    private static final String SQL_LISTAR_TODOS =
            "SELECT id, cliente_id, admin_id, incidencia_id, asunto, descripcion, estado_reclamo, fecha_creacion, respuesta " +
            "FROM reclamo_soporte ORDER BY fecha_creacion DESC, id DESC";

    private static final String SQL_RESPONDER =
            "UPDATE reclamo_soporte " +
            "SET admin_id = ?, respuesta = ?, estado_reclamo = ? " +
            "WHERE id = ?";

    public int insertar(Connection c, ReclamoSoporte reclamo) throws SQLException {
        LocalDateTime fecha = reclamo.getFechaCreacion() != null ? reclamo.getFechaCreacion() : LocalDateTime.now();
        try (PreparedStatement ps = c.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reclamo.getClienteId());
            if (reclamo.getAdminId() != null) {
                ps.setInt(2, reclamo.getAdminId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            if (reclamo.getIncidenciaId() != null) {
                ps.setInt(3, reclamo.getIncidenciaId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setString(4, reclamo.getAsunto());
            ps.setString(5, reclamo.getDescripcion());
            ps.setString(6, reclamo.getEstadoReclamo());
            ps.setTimestamp(7, Timestamp.valueOf(fecha));
            if (reclamo.getRespuesta() != null) {
                ps.setString(8, reclamo.getRespuesta());
            } else {
                ps.setNull(8, Types.VARCHAR);
            }

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se pudo registrar el reclamo de soporte.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int idGenerado = keys.getInt(1);
                    reclamo.setId(idGenerado);
                    reclamo.setFechaCreacion(fecha);
                    return idGenerado;
                }
            }
            reclamo.setFechaCreacion(fecha);
            return 0;
        }
    }

    public Optional<ReclamoSoporte> buscarPorId(Connection c, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SQL_BUSCAR_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapear(rs));
            }
        }
    }

    public List<ReclamoSoporte> listarPorCliente(Connection c, int clienteId) throws SQLException {
        List<ReclamoSoporte> lista = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(SQL_LISTAR_CLIENTE)) {
            ps.setInt(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public List<ReclamoSoporte> listarTodos(Connection c) throws SQLException {
        List<ReclamoSoporte> lista = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public boolean responderYActualizarEstado(Connection c, int reclamoId, int adminId, String respuesta, String nuevoEstado)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SQL_RESPONDER)) {
            ps.setInt(1, adminId);
            ps.setString(2, respuesta);
            ps.setString(3, nuevoEstado);
            ps.setInt(4, reclamoId);
            return ps.executeUpdate() > 0;
        }
    }

    private ReclamoSoporte mapear(ResultSet rs) throws SQLException {
        int adminVal = rs.getInt("admin_id");
        Integer adminId = rs.wasNull() ? null : adminVal;

        int incVal = rs.getInt("incidencia_id");
        Integer incidenciaId = rs.wasNull() ? null : incVal;

        Timestamp ts = rs.getTimestamp("fecha_creacion");
        LocalDateTime fechaCreacion = ts != null ? ts.toLocalDateTime() : null;

        return new ReclamoSoporte(
                rs.getInt("id"),
                rs.getInt("cliente_id"),
                adminId,
                incidenciaId,
                rs.getString("asunto"),
                rs.getString("descripcion"),
                rs.getString("estado_reclamo"),
                fechaCreacion,
                rs.getString("respuesta")
        );
    }
}
