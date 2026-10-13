package pe.edu.utp.eventope.dao;

import pe.edu.utp.eventope.dto.BoletoDetalleDTO;
import pe.edu.utp.eventope.dto.DispositivoOperadorDTO;
import pe.edu.utp.eventope.dto.EntradaResumenDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class MisEntradasDAO {

    public List<EntradaResumenDTO> listarPorComprador(Connection c, int compradorId) throws SQLException {
        String sql = "SELECT e.id, e.codigo_entrada, ev.nombre AS evento_nombre, ev.fecha AS evento_fecha, "
                + "l.nombre_local, l.ciudad, ca.nombre_zona, ca.precio_base, e.estado_uso, "
                + "bn.nombres_asistente, bn.apellidos_asistente, bn.dni_asistente "
                + "FROM entrada_digital e "
                + "JOIN evento_cultural ev ON ev.id = e.evento_id "
                + "JOIN local_concierto l ON l.id = ev.local_id "
                + "JOIN categoria_asiento ca ON ca.id = e.categoria_id "
                + "LEFT JOIN boleto_nominado bn ON bn.entrada_id = e.id "
                + "WHERE e.comprador_id = ? "
                + "ORDER BY ev.fecha DESC, e.id DESC";

        List<EntradaResumenDTO> lista = new ArrayList<>();
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, compradorId);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    Timestamp ts = r.getTimestamp("evento_fecha");
                    LocalDateTime fecha = ts != null ? ts.toLocalDateTime() : null;
                    String nombres = r.getString("nombres_asistente");
                    String apellidos = r.getString("apellidos_asistente");
                    String asistente = (nombres != null && apellidos != null) ? (nombres + " " + apellidos).trim() : nombres;

                    lista.add(new EntradaResumenDTO(
                            r.getInt("id"),
                            r.getString("codigo_entrada"),
                            r.getString("evento_nombre"),
                            fecha,
                            r.getString("nombre_local"),
                            r.getString("ciudad"),
                            r.getString("nombre_zona"),
                            r.getBigDecimal("precio_base"),
                            r.getString("estado_uso"),
                            asistente,
                            r.getString("dni_asistente")));
                }
            }
        }
        return lista;
    }

    public Optional<BoletoDetalleDTO> detalleBoleto(Connection c, int compradorId, int entradaId) throws SQLException {
        String sql = "SELECT e.id, e.codigo_entrada, ev.id AS evento_id, ev.nombre AS evento_nombre, ev.fecha AS evento_fecha, "
                + "l.nombre_local, l.direccion AS local_direccion, l.ciudad, ca.nombre_zona, ca.precio_base, e.estado_uso, "
                + "bn.nombres_asistente, bn.apellidos_asistente, bn.dni_asistente, "
                + "cp.serie_correlativo AS comprobante "
                + "FROM entrada_digital e "
                + "JOIN evento_cultural ev ON ev.id = e.evento_id "
                + "JOIN local_concierto l ON l.id = ev.local_id "
                + "JOIN categoria_asiento ca ON ca.id = e.categoria_id "
                + "LEFT JOIN boleto_nominado bn ON bn.entrada_id = e.id "
                + "LEFT JOIN detalle_transaccion dt ON dt.entrada_id = e.id "
                + "LEFT JOIN comprobante_pago cp ON cp.transaccion_id = dt.transaccion_id "
                + "WHERE e.id = ? AND e.comprador_id = ?";

        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, entradaId);
            p.setInt(2, compradorId);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    Timestamp ts = r.getTimestamp("evento_fecha");
                    LocalDateTime fecha = ts != null ? ts.toLocalDateTime() : null;

                    return Optional.of(new BoletoDetalleDTO(
                            r.getInt("id"),
                            r.getString("codigo_entrada"),
                            r.getInt("evento_id"),
                            r.getString("evento_nombre"),
                            fecha,
                            r.getString("nombre_local"),
                            r.getString("local_direccion"),
                            r.getString("ciudad"),
                            r.getString("nombre_zona"),
                            r.getBigDecimal("precio_base"),
                            r.getString("estado_uso"),
                            r.getString("nombres_asistente"),
                            r.getString("apellidos_asistente"),
                            r.getString("dni_asistente"),
                            r.getString("comprobante")));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<DispositivoOperadorDTO> dispositivoActivoPorOperador(Connection c, int operadorId) throws SQLException {
        String sql = "SELECT d.id AS dispositivo_id, d.operador_id, d.evento_id, ev.nombre AS evento_nombre, "
                + "d.nombre_puerta, d.modelo_celular "
                + "FROM dispositivo_validacion d "
                + "JOIN evento_cultural ev ON ev.id = d.evento_id "
                + "WHERE d.operador_id = ? AND d.estado_dispositivo = 'ACTIVO' "
                + "LIMIT 1";

        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, operadorId);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    return Optional.of(new DispositivoOperadorDTO(
                            r.getInt("dispositivo_id"),
                            r.getInt("operador_id"),
                            r.getInt("evento_id"),
                            r.getString("evento_nombre"),
                            r.getString("nombre_puerta"),
                            r.getString("modelo_celular")));
                }
            }
        }
        return Optional.empty();
    }
}
