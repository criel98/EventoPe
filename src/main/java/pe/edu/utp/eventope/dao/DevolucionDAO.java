package pe.edu.utp.eventope.dao;

import pe.edu.utp.eventope.dto.ResultadoDevolucion;
import pe.edu.utp.eventope.service.ReglasDevolucion;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public final class DevolucionDAO {
    private final Clock reloj;

    public DevolucionDAO() { this(Clock.systemUTC()); }
    public DevolucionDAO(Clock reloj) { this.reloj = reloj; }

    public ResultadoDevolucion devolver(Connection c, int clienteId, int transaccionId) throws SQLException {
        if (clienteId <= 0 || transaccionId <= 0) {
            throw new IllegalArgumentException("La compra seleccionada no es válida.");
        }
        int eventoId;
        String codigo;
        BigDecimal total;
        String estado;
        BigDecimal reembolso;
        Timestamp fechaReembolso;
        String sqlCompra = "SELECT evento_id,codigo_unico,total,estado,importe_reembolso,fecha_reembolso "
                + "FROM transaccion_compra WHERE id=? AND cliente_id=? FOR UPDATE";
        try (PreparedStatement p=c.prepareStatement(sqlCompra)) {
            p.setInt(1, transaccionId); p.setInt(2, clienteId);
            try (ResultSet r=p.executeQuery()) {
                if (!r.next()) throw new SQLException("La compra no existe o no pertenece a esta cuenta.");
                eventoId=r.getInt(1); codigo=r.getString(2); total=r.getBigDecimal(3);
                estado=r.getString(4); reembolso=r.getBigDecimal(5); fechaReembolso=r.getTimestamp(6);
            }
        }
        if ("DEVUELTA".equals(estado)) {
            if (reembolso == null || fechaReembolso == null) {
                throw new SQLException("La compra figura devuelta sin datos de reembolso.");
            }
            return new ResultadoDevolucion(transaccionId,codigo,reembolso,fechaReembolso.toLocalDateTime(),true);
        }
        if (!"CONFIRMADA".equals(estado)) throw new SQLException("La compra no está confirmada.");

        LocalDateTime fechaEvento;
        try (PreparedStatement p=c.prepareStatement("SELECT fecha FROM evento_cultural WHERE id=? FOR UPDATE")) {
            p.setInt(1, eventoId);
            try (ResultSet r=p.executeQuery()) {
                if (!r.next()) throw new SQLException("No se encontró el evento.");
                fechaEvento=r.getTimestamp(1).toLocalDateTime();
            }
        }
        int dias; BigDecimal penalizacion; boolean activa;
        try (PreparedStatement p=c.prepareStatement(
                "SELECT dias_minimos_previos,penalizacion_porcentaje,estado_activo "
                        + "FROM politica_devolucion WHERE evento_id=? FOR UPDATE")) {
            p.setInt(1, eventoId);
            try (ResultSet r=p.executeQuery()) {
                if (!r.next()) throw new SQLException("El evento no tiene política de devolución configurada.");
                dias=r.getInt(1); penalizacion=r.getBigDecimal(2); activa=r.getBoolean(3);
            }
        }
        try {
            ReglasDevolucion.validarPlazo(LocalDateTime.now(reloj),fechaEvento,dias,activa);
        } catch (IllegalArgumentException e) {
            throw new SQLException(e.getMessage(),e);
        }

        Map<Integer,Integer> cantidades=new LinkedHashMap<>();
        int entradas;
        try (PreparedStatement p=c.prepareStatement(
                "SELECT e.id,e.categoria_id,e.estado_uso,e.evento_id "
                + "FROM entrada_digital e JOIN detalle_transaccion d ON d.entrada_id=e.id "
                + "WHERE d.transaccion_id=? ORDER BY e.id FOR UPDATE")) {
            p.setInt(1, transaccionId);
            try (ResultSet r=p.executeQuery()) {
                entradas=0;
                while(r.next()) {
                    entradas++;
                    if (!"DISPONIBLE".equals(r.getString(3))) {
                        throw new SQLException("Una entrada utilizada o anulada no puede devolverse.");
                    }
                    if (r.getInt(4)!=eventoId) throw new SQLException("La entrada no pertenece al evento.");
                    cantidades.merge(r.getInt(2),1,Integer::sum);
                }
            }
        }
        if (entradas==0) throw new SQLException("La compra no contiene entradas.");

        for (Map.Entry<Integer,Integer> item:cantidades.entrySet()) {
            try (PreparedStatement p=c.prepareStatement(
                    "UPDATE categoria_asiento SET aforo_disponible=aforo_disponible+? "
                    + "WHERE id=? AND evento_id=? AND aforo_disponible+?<=aforo_total")) {
                p.setInt(1,item.getValue()); p.setInt(2,item.getKey()); p.setInt(3,eventoId); p.setInt(4,item.getValue());
                if(p.executeUpdate()!=1) throw new SQLException("No se pudo restaurar el aforo.");
            }
        }

        try (PreparedStatement p=c.prepareStatement(
                "UPDATE entrada_digital e JOIN detalle_transaccion d ON d.entrada_id=e.id "
                        + "SET e.estado_uso='ANULADA' "
                        + "WHERE d.transaccion_id=? AND e.estado_uso='DISPONIBLE'")) {
            p.setInt(1, transaccionId);
            if(p.executeUpdate()!=entradas) throw new SQLException("No se pudo anular todas las entradas.");
        }

        BigDecimal importe=ReglasDevolucion.calcularImporte(total,penalizacion);
        LocalDateTime ahora=LocalDateTime.now(reloj);
        try (PreparedStatement p=c.prepareStatement(
                "UPDATE transaccion_compra SET estado='DEVUELTA',importe_reembolso=?,fecha_reembolso=? "
                        + "WHERE id=? AND cliente_id=? AND estado='CONFIRMADA'")) {
            p.setBigDecimal(1,importe); p.setTimestamp(2,Timestamp.valueOf(ahora));
            p.setInt(3,transaccionId); p.setInt(4,clienteId);
            if(p.executeUpdate()!=1) throw new SQLException("La compra cambió antes de devolverse.");
        }
        return new ResultadoDevolucion(transaccionId,codigo,importe,ahora,false);
    }
}
