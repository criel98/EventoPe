package pe.edu.utp.eventope.dao;

import pe.edu.utp.eventope.model.CategoriaAsiento;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class CategoriaAsientoDAO {
    public List<CategoriaAsiento> listarPorEvento(Connection c, int eventoId) throws SQLException {
        String sql = "SELECT id, evento_id, nombre_zona, precio_base, aforo_total, aforo_disponible " +
                     "FROM categoria_asiento WHERE evento_id = ? ORDER BY precio_base ASC, nombre_zona ASC";
        List<CategoriaAsiento> result = new ArrayList<>();
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, eventoId);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    result.add(new CategoriaAsiento(
                            r.getInt("id"),
                            r.getInt("evento_id"),
                            r.getString("nombre_zona"),
                            r.getBigDecimal("precio_base"),
                            r.getInt("aforo_total"),
                            r.getInt("aforo_disponible")
                    ));
                }
            }
        }
        return result;
    }
}
