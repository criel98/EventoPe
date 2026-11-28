package pe.edu.utp.eventope.dao;

import pe.edu.utp.eventope.model.Artista;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ArtistaDAO {
    public List<Artista> listar(Connection c) throws SQLException {
        List<Artista> result = new ArrayList<>();
        try (PreparedStatement p = c.prepareStatement("SELECT id,nombre_artistico,genero_musical,pais_origen,red_social FROM artista ORDER BY nombre_artistico");
             ResultSet r = p.executeQuery()) { while (r.next()) result.add(mapear(r)); }
        return result;
    }
    public Optional<Artista> buscar(Connection c, int id) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("SELECT id,nombre_artistico,genero_musical,pais_origen,red_social FROM artista WHERE id=?")) {
            p.setInt(1,id); try (ResultSet r=p.executeQuery()) { return r.next()?Optional.of(mapear(r)):Optional.empty(); }
        }
    }
    public void insertar(Connection c, Artista a) throws SQLException {
        try (PreparedStatement p=c.prepareStatement("INSERT INTO artista(nombre_artistico,genero_musical,pais_origen,red_social) VALUES(?,?,?,?)")) {
            p.setString(1,a.getNombreArtistico());p.setString(2,a.getGeneroMusical());p.setString(3,a.getPaisOrigen());p.setString(4,a.getRedSocial());p.executeUpdate();
        }
    }
    public void actualizar(Connection c, Artista a) throws SQLException {
        try (PreparedStatement p=c.prepareStatement("UPDATE artista SET nombre_artistico=?,genero_musical=?,pais_origen=?,red_social=? WHERE id=?")) {
            p.setString(1,a.getNombreArtistico());p.setString(2,a.getGeneroMusical());p.setString(3,a.getPaisOrigen());p.setString(4,a.getRedSocial());p.setInt(5,a.getId());
            if(p.executeUpdate()!=1)throw new SQLException("Artista no encontrado.");
        }
    }
    public void eliminar(Connection c,int id)throws SQLException{
        try(PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM evento_cultural WHERE artista_id=?")){p.setInt(1,id);try(ResultSet r=p.executeQuery()){r.next();if(r.getLong(1)>0)throw new SQLException("El artista está asociado a eventos y no se puede borrar.");}}
        try(PreparedStatement p=c.prepareStatement("DELETE FROM artista WHERE id=?")){p.setInt(1,id);if(p.executeUpdate()!=1)throw new SQLException("Artista no encontrado.");}
    }
    private static Artista mapear(ResultSet r)throws SQLException{return new Artista(r.getInt("id"),r.getString("nombre_artistico"),r.getString("genero_musical"),r.getString("pais_origen"),r.getString("red_social"));}
}
