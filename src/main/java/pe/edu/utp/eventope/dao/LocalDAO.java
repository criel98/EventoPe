package pe.edu.utp.eventope.dao;

import pe.edu.utp.eventope.model.LocalConcierto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class LocalDAO {
    public List<LocalConcierto> listar(Connection c)throws SQLException{
        List<LocalConcierto> result=new ArrayList<>();
        try(PreparedStatement p=c.prepareStatement("SELECT id,nombre_local,direccion,capacidad_max,ciudad FROM local_concierto ORDER BY nombre_local");ResultSet r=p.executeQuery()){while(r.next())result.add(mapear(r));}
        return result;
    }
    public Optional<LocalConcierto> buscar(Connection c,int id)throws SQLException{
        try(PreparedStatement p=c.prepareStatement("SELECT id,nombre_local,direccion,capacidad_max,ciudad FROM local_concierto WHERE id=?")){p.setInt(1,id);try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(mapear(r)):Optional.empty();}}
    }
    public Optional<LocalConcierto> buscar(Connection c,int id,boolean bloquear)throws SQLException{
        if(!bloquear)return buscar(c,id);
        try(PreparedStatement p=c.prepareStatement("SELECT id,nombre_local,direccion,capacidad_max,ciudad FROM local_concierto WHERE id=? FOR UPDATE")){p.setInt(1,id);try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(mapear(r)):Optional.empty();}}
    }
    public void insertar(Connection c,LocalConcierto l)throws SQLException{
        try(PreparedStatement p=c.prepareStatement("INSERT INTO local_concierto(nombre_local,direccion,capacidad_max,ciudad) VALUES(?,?,?,?)")){p.setString(1,l.getNombreLocal());p.setString(2,l.getDireccion());p.setInt(3,l.getCapacidadMax());p.setString(4,l.getCiudad());p.executeUpdate();}
    }
    public void actualizar(Connection c,LocalConcierto l)throws SQLException{
        try(PreparedStatement p=c.prepareStatement("UPDATE local_concierto SET nombre_local=?,direccion=?,capacidad_max=?,ciudad=? WHERE id=?")){p.setString(1,l.getNombreLocal());p.setString(2,l.getDireccion());p.setInt(3,l.getCapacidadMax());p.setString(4,l.getCiudad());p.setInt(5,l.getId());if(p.executeUpdate()!=1)throw new SQLException("Local no encontrado.");}
    }
    public void eliminar(Connection c,int id)throws SQLException{
        try(PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM evento_cultural WHERE local_id=?")){p.setInt(1,id);try(ResultSet r=p.executeQuery()){r.next();if(r.getLong(1)>0)throw new SQLException("El local está asociado a eventos y no se puede borrar.");}}
        try(PreparedStatement p=c.prepareStatement("DELETE FROM local_concierto WHERE id=?")){p.setInt(1,id);if(p.executeUpdate()!=1)throw new SQLException("Local no encontrado.");}
    }
    private static LocalConcierto mapear(ResultSet r)throws SQLException{return new LocalConcierto(r.getInt("id"),r.getString("nombre_local"),r.getString("direccion"),r.getInt("capacidad_max"),r.getString("ciudad"));}
}
