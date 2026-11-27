package pe.edu.utp.eventope.service;
import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.dao.QrDAO;
import pe.edu.utp.eventope.dto.*;
import pe.edu.utp.eventope.util.*;
import java.sql.*;
import java.time.*;
import java.util.Arrays;
/** Núcleo compartido listo para conectar a la pantalla de Glenn. */
public final class ServicioQr {
    private final ProveedorConexion conexiones;private final CifradoSecretos cifrado;private final Clock reloj;private final QrDAO dao=new QrDAO();
    public ServicioQr(ProveedorConexion c,CifradoSecretos s,Clock r){conexiones=c;cifrado=s;reloj=r;}
    public QrRespuestaDTO obtener(SesionUsuario actor,int entradaId)throws SQLException{
        actor.exigirCliente();
        try(Connection c=conexiones.abrir()){
            c.setAutoCommit(false);
            try{
                try(var p=c.prepareStatement("SELECT estado FROM usuario_cliente WHERE id=?")){
                    p.setInt(1,actor.getId());try(var r=p.executeQuery()){if(!r.next()||!"ACTIVO".equals(r.getString(1)))throw new SecurityException("Cuenta no disponible");}
                }
                var entrada=dao.bloquearEntrada(c,entradaId);
                if(entrada==null||entrada.comprador()!=actor.getId())throw new SecurityException("Entrada no disponible para esta cuenta");
                if(!entrada.estado().equals("DISPONIBLE")||!entrada.nominada())throw new IllegalStateException("Entrada no habilitada para QR");
                var qr=dao.credencial(c,entradaId);
                if(qr==null){byte[] nueva=Totp.nuevaClave();try{qr=dao.insertar(c,entradaId,cifrado.cifrar(nueva,entrada.codigo()));}finally{Arrays.fill(nueva,(byte)0);}}
                Instant ahora=reloj.instant();long intervalo=Totp.intervalo(ahora);
                if(qr.ultimoIntervalo()!=null&&intervalo<qr.ultimoIntervalo())throw new IllegalStateException("Reloj del servidor retrocedió");
                byte[] clave=cifrado.descifrar(qr.cifrado(),entrada.codigo());String payload;
                try{payload=new QrPayload(entrada.codigo(),intervalo,Totp.generar(clave,ahora)).codificar();}finally{Arrays.fill(clave,(byte)0);}
                // Reabrir o refrescar durante el mismo intervalo produce el mismo TOTP, no una rotación falsa.
                if(qr.ultimoIntervalo()==null||qr.ultimoIntervalo()!=intervalo)dao.auditar(c,qr.id(),intervalo,ahora,Totp.expiracion(ahora),HashUtil.sha256(payload));
                c.commit();return new QrRespuestaDTO(payload,ahora,Totp.expiracion(ahora));
            }catch(SQLException|RuntimeException e){try{c.rollback();}catch(SQLException rollback){e.addSuppressed(rollback);}throw e;}
        }
    }
}
