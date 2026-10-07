package pe.edu.utp.eventope.service;
import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.dao.*;
import pe.edu.utp.eventope.dto.*;
import pe.edu.utp.eventope.util.*;
import java.sql.*;
import java.time.*;
import java.util.*;
/** Una única operación transaccional; la interfaz de guardia NO decide si la entrada es válida. */
public final class ServicioAcceso {
    private final ProveedorConexion conexiones;private final CifradoSecretos cifrado;private final Clock reloj;
    private final QrDAO qrDAO=new QrDAO();private final AccesoDAO dao=new AccesoDAO();
    public ServicioAcceso(ProveedorConexion c,CifradoSecretos s,Clock r){conexiones=c;cifrado=s;reloj=r;}
    public ResultadoAccesoDTO validar(SesionUsuario actor,int dispositivoId,String tokenDispositivo,String solicitudId,String contenido,String dniPresentado)throws SQLException{
        for(int intento=0;;intento++){
            try{return intentar(actor,dispositivoId,tokenDispositivo,solicitudId,contenido,dniPresentado);}
            catch(SQLException e){if(intento>=2||(e.getErrorCode()!=1213&&e.getErrorCode()!=1205))throw e;}
        }
    }
    private ResultadoAccesoDTO intentar(SesionUsuario actor,int dispositivoId,String tokenDispositivo,String solicitudId,String contenido,String dniPresentado)throws SQLException{
        actor.exigirValidacion();
        if(!UUID.fromString(solicitudId).toString().equals(solicitudId))throw new IllegalArgumentException("Solicitud inválida");
        if(contenido==null||contenido.length()>100||dniPresentado==null||!dniPresentado.matches("[0-9]{8}")||tokenDispositivo==null||tokenDispositivo.length()>200)throw new IllegalArgumentException("Datos de validación inválidos");
        String hash=HashUtil.sha256(contenido+"|"+dniPresentado);
        try(Connection c=conexiones.abrir()){
            c.setAutoCommit(false);
            try{
                var d=dao.bloquearDispositivo(c,dispositivoId);
                if(d==null||d.operador()!=actor.getId()||!d.estado().equals("ACTIVO")||!d.estadoOperador().equals("ACTIVO")||!d.rol().equals("VALIDACION")||!HashUtil.iguales(d.hash(),HashUtil.sha256(tokenDispositivo)))throw new SecurityException("Operador o dispositivo no habilitado");
                var previo=dao.recuperar(c,solicitudId,hash,actor.getId(),dispositivoId);if(previo!=null){c.commit();return previo;}
                Instant ahora=reloj.instant();
                if(dao.intentosRecientes(c,dispositivoId,ahora.minusSeconds(60))>=30)throw new SecurityException("Demasiados intentos; espere un minuto");
                QrPayload payload=null;try{payload=QrPayload.leer(contenido);}catch(IllegalArgumentException ignored){/* Registrar QR malformado sin atribuirlo a una entrada. */}
                QrDAO.Entrada e=payload==null?null:qrDAO.bloquearEntrada(c,payload.codigoEntrada());
                String motivo;
                if(payload==null)motivo="QR_MALFORMADO";
                else if(e==null)motivo="QR_DESCONOCIDO";
                else if(e.evento()!=d.evento())motivo="EVENTO_INCORRECTO";
                else if(!e.estado().equals("DISPONIBLE"))motivo=e.estado().equals("UTILIZADA")?"YA_UTILIZADA":"ANULADA";
                else if(!e.nominada())motivo="SIN_NOMINACION";
                else if(!e.dni().equals(dniPresentado))motivo="IDENTIDAD_NO_COINCIDE";
                else if(payload.intervalo()!=Totp.intervalo(ahora))motivo="QR_VENCIDO_O_FUERA_DE_INTERVALO";
                else{
                    var qr=qrDAO.credencial(c,e.id());
                    if(qr==null)motivo="QR_NO_EMITIDO";
                    else{byte[] clave=cifrado.descifrar(qr.cifrado(),e.codigo());try{motivo=payload.verificar(clave,ahora)?"VALIDO":"QR_ALTERADO";}finally{Arrays.fill(clave,(byte)0);}}
                }
                boolean permitido=motivo.equals("VALIDO");
                if(permitido&&!dao.consumir(c,e.id())){permitido=false;motivo="YA_UTILIZADA";}
                int id=dao.registrar(c,solicitudId,hash,d,e==null?null:e.id(),ahora,permitido,motivo);
                if(!permitido)dao.incidencia(c,id,motivo);
                c.commit();return new ResultadoAccesoDTO(id,permitido,motivo,false);
            }catch(SQLException|RuntimeException ex){try{c.rollback();}catch(SQLException rollback){ex.addSuppressed(rollback);}throw ex;}
        }
    }
}
