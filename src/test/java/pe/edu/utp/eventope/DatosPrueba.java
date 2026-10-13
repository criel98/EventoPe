package pe.edu.utp.eventope;
import pe.edu.utp.eventope.config.ProveedorConexion;
import java.sql.*;
final class DatosPrueba {
 static ProveedorConexion conexiones(){return ()->DriverManager.getConnection(System.getenv("EVENTOPE_TEST_DB_URL"),System.getenv().getOrDefault("EVENTOPE_TEST_DB_USER","root"),System.getenv().getOrDefault("EVENTOPE_TEST_DB_PASSWORD",""));}
 static void limpiar()throws Exception{
  try(var c=conexiones().abrir();var s=c.createStatement()){
   if(!"eventope_base_test".equals(c.getCatalog()))throw new IllegalStateException("Solo se admite eventope_base_test");
   String[] tablas={"notificacion_usuario","reclamo_soporte","incidencia_acceso","control_acceso_puerta","dispositivo_validacion","historial_rotacion_qr","ticket_qr_dinamico","boleto_nominado","comprobante_pago","metodo_pago","detalle_transaccion","entrada_digital","transaccion_compra","politica_devolucion","categoria_asiento","preferencias_musicales","perfil_usuario","evento_cultural","cupon_descuento","administrador_plataforma","usuario_cliente","artista","local_concierto","organizador_evento"};
   for(String tabla:tablas)s.executeUpdate("DELETE FROM "+tabla);
  }
 }
 static void sql(String sql)throws Exception{try(var c=conexiones().abrir();var s=c.createStatement()){s.executeUpdate(sql);}}
 static int count(String tabla)throws Exception{try(var c=conexiones().abrir();var s=c.createStatement();var r=s.executeQuery("SELECT COUNT(*) FROM "+tabla)){r.next();return r.getInt(1);}}
}
