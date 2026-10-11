package pe.edu.utp.eventope.dao;
import java.sql.*;
import java.time.*;
import pe.edu.utp.eventope.dto.ResultadoAccesoDTO;
public final class AccesoDAO {
    public record Dispositivo(int id,int operador,int evento,String hash,String estado,String estadoOperador,String rol){}
    public Dispositivo bloquearDispositivo(Connection c,int id)throws SQLException{
        try(var p=c.prepareStatement("SELECT d.id,d.operador_id,d.evento_id,d.token_acceso_hash,d.estado_dispositivo,a.estado,a.nivel_acceso FROM dispositivo_validacion d JOIN administrador_plataforma a ON a.id=d.operador_id WHERE d.id=? FOR UPDATE")){
            p.setInt(1,id);try(var r=p.executeQuery()){return r.next()?new Dispositivo(r.getInt(1),r.getInt(2),r.getInt(3),r.getString(4),r.getString(5),r.getString(6),r.getString(7)):null;}
        }
    }
    public ResultadoAccesoDTO recuperar(Connection c,String solicitud,String hash,int operador,int dispositivo)throws SQLException{
        try(var p=c.prepareStatement("SELECT id,estado_acceso,motivo,solicitud_hash,operador_id,dispositivo_id FROM control_acceso_puerta WHERE solicitud_id=?")){
            p.setString(1,solicitud);try(var r=p.executeQuery()){
                if(!r.next())return null;
                if(!pe.edu.utp.eventope.util.HashUtil.iguales(hash,r.getString(4))||operador!=r.getInt(5)||dispositivo!=r.getInt(6))throw new SecurityException("Solicitud reutilizada con otros datos");
                return new ResultadoAccesoDTO(r.getInt(1),"PERMITIDO".equals(r.getString(2)),r.getString(3),true);
            }
        }
    }
    public int intentosRecientes(Connection c,int dispositivo,Instant desde)throws SQLException{
        try(var p=c.prepareStatement("SELECT COUNT(*) FROM control_acceso_puerta WHERE dispositivo_id=? AND hora_ingreso>=?")){
            p.setInt(1,dispositivo);p.setObject(2,LocalDateTime.ofInstant(desde,ZoneOffset.UTC));try(var r=p.executeQuery()){r.next();return r.getInt(1);}
        }
    }
    public boolean consumir(Connection c,int entrada)throws SQLException{
        try(var p=c.prepareStatement("UPDATE entrada_digital SET estado_uso='UTILIZADA' WHERE id=? AND estado_uso='DISPONIBLE'")){p.setInt(1,entrada);return p.executeUpdate()==1;}
    }
    public int registrar(Connection c,String solicitud,String hash,Dispositivo d,Integer entrada,Instant ahora,boolean permitido,String motivo)throws SQLException{
        try(var p=c.prepareStatement("INSERT INTO control_acceso_puerta(solicitud_id,solicitud_hash,evento_id,dispositivo_id,operador_id,entrada_id,hora_ingreso,estado_acceso,motivo) VALUES (?,?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS)){
            p.setString(1,solicitud);p.setString(2,hash);p.setInt(3,d.evento());p.setInt(4,d.id());p.setInt(5,d.operador());if(entrada==null)p.setNull(6,Types.INTEGER);else p.setInt(6,entrada);p.setObject(7,LocalDateTime.ofInstant(ahora,ZoneOffset.UTC));p.setString(8,permitido?"PERMITIDO":"DENEGADO");p.setString(9,motivo);p.executeUpdate();
            try(var r=p.getGeneratedKeys()){if(!r.next())throw new SQLException("Control sin id");return r.getInt(1);}
        }
    }
    public void incidencia(Connection c,int control,String motivo)throws SQLException{
        try(var p=c.prepareStatement("INSERT INTO incidencia_acceso(control_id,tipo_incidencia,descripcion,severidad,estado_resolucion) VALUES (?,?,?,'MEDIA','ABIERTA')")){p.setInt(1,control);p.setString(2,motivo);p.setString(3,"Intento denegado en demostración de acceso");p.executeUpdate();}
    }
}
