package pe.edu.utp.eventope.dao;

import pe.edu.utp.eventope.model.CategoriaAsiento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CategoriaAsientoDAO {
    public Optional<CategoriaAsiento> buscar(Connection c, int id, boolean bloquear) throws SQLException {
        String sql="SELECT id,evento_id,nombre_zona,precio_base,aforo_total,aforo_disponible FROM categoria_asiento WHERE id=?"+(bloquear?" FOR UPDATE":"");
        try(PreparedStatement p=c.prepareStatement(sql)){p.setInt(1,id);try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(mapear(r)):Optional.empty();}}
    }

    public void insertar(Connection c, CategoriaAsiento categoria) throws SQLException {
        try(PreparedStatement p=c.prepareStatement("INSERT INTO categoria_asiento(evento_id,nombre_zona,precio_base,aforo_total,aforo_disponible) VALUES(?,?,?,?,?)")){
            p.setInt(1,categoria.getEventoId());p.setString(2,categoria.getNombreZona());p.setBigDecimal(3,categoria.getPrecioBase());p.setInt(4,categoria.getAforoTotal());p.setInt(5,categoria.getAforoDisponible());p.executeUpdate();
        }
    }

    public void actualizar(Connection c, CategoriaAsiento categoria) throws SQLException {
        try(PreparedStatement p=c.prepareStatement("UPDATE categoria_asiento SET nombre_zona=?,precio_base=?,aforo_total=?,aforo_disponible=? WHERE id=? AND evento_id=?")){
            p.setString(1,categoria.getNombreZona());p.setBigDecimal(2,categoria.getPrecioBase());p.setInt(3,categoria.getAforoTotal());p.setInt(4,categoria.getAforoDisponible());p.setInt(5,categoria.getId());p.setInt(6,categoria.getEventoId());
            if(p.executeUpdate()!=1)throw new SQLException("Zona no encontrada para el evento indicado.");
        }
    }

    public void eliminar(Connection c,int id)throws SQLException{
        try(PreparedStatement p=c.prepareStatement("SELECT (SELECT COUNT(*) FROM entrada_digital WHERE categoria_id=?)+(SELECT COUNT(*) FROM transaccion_compra t JOIN categoria_asiento z ON z.evento_id=t.evento_id WHERE z.id=?)")){
            p.setInt(1,id);p.setInt(2,id);try(ResultSet r=p.executeQuery()){r.next();if(r.getLong(1)>0)throw new SQLException("La zona tiene compras o entradas asociadas y no se puede borrar.");}
        }
        try(PreparedStatement p=c.prepareStatement("DELETE FROM categoria_asiento WHERE id=?")){p.setInt(1,id);if(p.executeUpdate()!=1)throw new SQLException("Zona no encontrada.");}
    }

    public int contarEntradasNoAnuladas(Connection c,int categoriaId)throws SQLException{
        try(PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM entrada_digital WHERE categoria_id=? AND estado_uso<>'ANULADA'")){
            p.setInt(1,categoriaId);
            try(ResultSet r=p.executeQuery()){r.next();return r.getInt(1);}
        }
    }

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

    private static CategoriaAsiento mapear(ResultSet r)throws SQLException{
        return new CategoriaAsiento(r.getInt("id"),r.getInt("evento_id"),r.getString("nombre_zona"),r.getBigDecimal("precio_base"),r.getInt("aforo_total"),r.getInt("aforo_disponible"));
    }
}
