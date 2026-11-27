package pe.edu.utp.eventope.dao;
import java.sql.*;
import java.time.*;
/** Usa la conexión de la transacción del servicio. No hace commit ni cierra la conexión. */
public final class QrDAO {
    public record Entrada(int id,String codigo,int comprador,int evento,String estado,boolean nominada,String dni){}
    public record Credencial(int id,String cifrado,Long ultimoIntervalo){}
    public Entrada bloquearEntrada(Connection c,int id)throws SQLException{return entrada(c,"e.id",id);}
    public Entrada bloquearEntrada(Connection c,String codigo)throws SQLException{return entrada(c,"e.codigo_entrada",codigo);}
    private Entrada entrada(Connection c,String columna,Object valor)throws SQLException{
        // Columna es una constante del programa, nunca un parámetro HTTP.
        try(var p=c.prepareStatement("SELECT e.id,e.codigo_entrada,e.comprador_id,e.evento_id,e.estado_uso,b.dni_asistente FROM entrada_digital e LEFT JOIN boleto_nominado b ON b.entrada_id=e.id WHERE "+columna+"=? FOR UPDATE")){
            p.setObject(1,valor);try(var r=p.executeQuery()){if(!r.next())return null;String dni=r.getString(6);return new Entrada(r.getInt(1),r.getString(2),r.getInt(3),r.getInt(4),r.getString(5),dni!=null,dni);}
        }
    }
    public Credencial credencial(Connection c,int entrada)throws SQLException{
        try(var p=c.prepareStatement("SELECT id,clave_totp_cifrada,ultimo_intervalo FROM ticket_qr_dinamico WHERE entrada_id=? FOR UPDATE")){
            p.setInt(1,entrada);try(var r=p.executeQuery()){if(!r.next())return null;long n=r.getLong(3);Long ultimo=r.wasNull()?null:n;return new Credencial(r.getInt(1),r.getString(2),ultimo);}
        }
    }
    public Credencial insertar(Connection c,int entrada,String cifrado)throws SQLException{
        try(var p=c.prepareStatement("INSERT INTO ticket_qr_dinamico(entrada_id,clave_totp_cifrada) VALUES (?,?)",Statement.RETURN_GENERATED_KEYS)){
            p.setInt(1,entrada);p.setString(2,cifrado);p.executeUpdate();try(var r=p.getGeneratedKeys()){if(!r.next())throw new SQLException("No se generó id QR");return new Credencial(r.getInt(1),cifrado,null);}
        }
    }
    public void auditar(Connection c,int qr,long intervalo,Instant ahora,Instant expira,String hash)throws SQLException{
        String anterior=null;
        try(var p=c.prepareStatement("SELECT hash_nuevo FROM historial_rotacion_qr WHERE qr_id=? ORDER BY intervalo DESC LIMIT 1")){
            p.setInt(1,qr);try(var r=p.executeQuery()){if(r.next())anterior=r.getString(1);}
        }
        try(var p=c.prepareStatement("INSERT INTO historial_rotacion_qr(qr_id,intervalo,hora_generacion,hash_anterior,hash_nuevo) VALUES (?,?,?,?,?)")){
            p.setInt(1,qr);p.setLong(2,intervalo);p.setObject(3,LocalDateTime.ofInstant(ahora,ZoneOffset.UTC));p.setString(4,anterior);p.setString(5,hash);p.executeUpdate();
        }
        try(var p=c.prepareStatement("UPDATE ticket_qr_dinamico SET ultimo_intervalo=?,fecha_generacion=?,fecha_expiracion=? WHERE id=?")){
            p.setLong(1,intervalo);p.setObject(2,LocalDateTime.ofInstant(ahora,ZoneOffset.UTC));p.setObject(3,LocalDateTime.ofInstant(expira,ZoneOffset.UTC));p.setInt(4,qr);p.executeUpdate();
        }
    }
}
