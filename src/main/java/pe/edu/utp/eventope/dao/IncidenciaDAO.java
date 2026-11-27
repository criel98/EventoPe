package pe.edu.utp.eventope.dao;

import pe.edu.utp.eventope.dto.IncidenciaDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Consultas y cambios de incidencia_acceso. Usa la conexión de la transacción del servicio. */
public final class IncidenciaDAO {
    private static final String SELECT =
            "SELECT i.id, i.control_id, i.tipo_incidencia, i.descripcion, i.severidad, i.estado_resolucion, "
          + "i.fecha_resolucion, i.motivo_resolucion, ra.username, "
          + "c.hora_ingreso, c.estado_acceso, c.motivo, c.entrada_id, c.evento_id, c.autorizado_por_id, "
          + "ev.nombre, dv.nombre_puerta, op.username, "
          + "ed.estado_uso, ed.evento_id, bn.nombres_asistente, bn.apellidos_asistente, bn.dni_asistente "
          + "FROM incidencia_acceso i "
          + "JOIN control_acceso_puerta c ON c.id = i.control_id "
          + "JOIN evento_cultural ev ON ev.id = c.evento_id "
          + "JOIN dispositivo_validacion dv ON dv.id = c.dispositivo_id "
          + "JOIN administrador_plataforma op ON op.id = c.operador_id "
          + "LEFT JOIN administrador_plataforma ra ON ra.id = i.resuelta_por_id "
          + "LEFT JOIN entrada_digital ed ON ed.id = c.entrada_id "
          + "LEFT JOIN boleto_nominado bn ON bn.entrada_id = ed.id ";

    /** estado y severidad ya validados contra una lista fija; null significa "todos". */
    public List<IncidenciaDTO> listar(Connection c, String estado, String severidad, int limite, int desplazamiento) throws SQLException {
        String sql = SELECT + "WHERE (? IS NULL OR i.estado_resolucion = ?) AND (? IS NULL OR i.severidad = ?) "
                + "ORDER BY (i.estado_resolucion = 'ABIERTA') DESC, FIELD(i.severidad,'ALTA','MEDIA','BAJA'), c.hora_ingreso DESC, i.id DESC LIMIT ? OFFSET ?";
        List<IncidenciaDTO> lista = new ArrayList<>();
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, estado);
            p.setString(2, estado);
            p.setString(3, severidad);
            p.setString(4, severidad);
            p.setInt(5, limite);
            p.setInt(6, desplazamiento);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) lista.add(mapear(r));
            }
        }
        return lista;
    }

    public int contar(Connection c, String estado, String severidad) throws SQLException {
        try (PreparedStatement p = c.prepareStatement(
                "SELECT COUNT(*) FROM incidencia_acceso WHERE (? IS NULL OR estado_resolucion = ?) AND (? IS NULL OR severidad = ?)")) {
            p.setString(1, estado);
            p.setString(2, estado);
            p.setString(3, severidad);
            p.setString(4, severidad);
            try (ResultSet r = p.executeQuery()) {
                r.next();
                return r.getInt(1);
            }
        }
    }

    public Optional<IncidenciaDTO> obtener(Connection c, int id) throws SQLException {
        try (PreparedStatement p = c.prepareStatement(SELECT + "WHERE i.id = ?")) {
            p.setInt(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? Optional.of(mapear(r)) : Optional.empty();
            }
        }
    }

    /** Bloquea la fila de la incidencia hasta el fin de la transacción. @return estado actual o null si no existe. */
    public String bloquearEstado(Connection c, int id) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("SELECT estado_resolucion FROM incidencia_acceso WHERE id = ? FOR UPDATE")) {
            p.setInt(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? r.getString(1) : null;
            }
        }
    }

    /** Solo cambia una incidencia ABIERTA: una segunda resolución no sobrescribe quién/cuándo/por qué de la primera. */
    public boolean resolver(Connection c, int id, String estadoFinal, int resueltaPorId, LocalDateTime cuandoUtc, String motivo) throws SQLException {
        try (PreparedStatement p = c.prepareStatement(
                "UPDATE incidencia_acceso SET estado_resolucion = ?, resuelta_por_id = ?, fecha_resolucion = ?, motivo_resolucion = ? "
              + "WHERE id = ? AND estado_resolucion = 'ABIERTA'")) {
            p.setString(1, estadoFinal);
            p.setInt(2, resueltaPorId);
            p.setObject(3, cuandoUtc);
            p.setString(4, motivo);
            p.setInt(5, id);
            return p.executeUpdate() == 1;
        }
    }

    private static IncidenciaDTO mapear(ResultSet r) throws SQLException {
        int entrada = r.getInt(13);
        Integer entradaId = r.wasNull() ? null : entrada;
        int autorizado = r.getInt(15);
        Integer autorizadoPor = r.wasNull() ? null : autorizado;
        int evEntrada = r.getInt(20);
        Integer entradaEvento = r.wasNull() ? null : evEntrada;
        return new IncidenciaDTO(new IncidenciaDTO.Datos(
                r.getInt(1), r.getInt(2), r.getString(3), r.getString(4), r.getString(5), r.getString(6),
                r.getObject(7, LocalDateTime.class), r.getString(8), r.getString(9),
                r.getObject(10, LocalDateTime.class), r.getString(11), r.getString(12), entradaId,
                r.getInt(14), autorizadoPor, r.getString(16), r.getString(17), r.getString(18),
                r.getString(19), entradaEvento, r.getString(21), r.getString(22), r.getString(23)));
    }
}
