package pe.edu.utp.eventope;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import pe.edu.utp.eventope.config.*;
import pe.edu.utp.eventope.dto.*;
import pe.edu.utp.eventope.service.*;
import pe.edu.utp.eventope.util.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
/** Borra fixtures SOLO en el esquema llamado exactamente eventope_base_test. */
@EnabledIfEnvironmentVariable(named="EVENTOPE_TEST_DB_URL",matches=".+")
class BaseMySqlTest {
 static final String CODIGO="00000000-0000-4000-8000-000000000001";
 static final String DISPOSITIVO="dispositivo-solo-para-pruebas-123456";
 static final Instant AHORA=Instant.parse("2026-10-07T12:00:01Z");
 static final SesionUsuario CLIENTE=new SesionUsuario(1,SesionUsuario.Tipo.CLIENTE,SesionUsuario.Rol.CLIENTE,"Cliente A");
 static final SesionUsuario GUARDIA=new SesionUsuario(1,SesionUsuario.Tipo.PERSONAL,SesionUsuario.Rol.VALIDACION,"Guardia A");
 static final SesionUsuario GUARDIA2=new SesionUsuario(2,SesionUsuario.Tipo.PERSONAL,SesionUsuario.Rol.VALIDACION,"Guardia B");
 ProveedorConexion conexiones;CifradoSecretos cifrado;ServicioQr qr;ServicioAcceso acceso;
 @BeforeEach void preparar()throws Exception{
  conexiones=()->DriverManager.getConnection(System.getenv("EVENTOPE_TEST_DB_URL"),System.getenv().getOrDefault("EVENTOPE_TEST_DB_USER","root"),System.getenv().getOrDefault("EVENTOPE_TEST_DB_PASSWORD",""));
  cifrado=new CifradoSecretos(Base64.getEncoder().encodeToString(Totp.nuevaClave()));qr=new ServicioQr(conexiones,cifrado,Clock.fixed(AHORA,ZoneOffset.UTC));acceso=new ServicioAcceso(conexiones,cifrado,Clock.fixed(AHORA,ZoneOffset.UTC));
  try(Connection c=conexiones.abrir();Statement s=c.createStatement()){
   if(!"eventope_base_test".equals(c.getCatalog()))throw new IllegalStateException("Se rechaza borrar un esquema distinto de eventope_base_test");
   String[] tablas={"notificacion_usuario","reclamo_soporte","incidencia_acceso","control_acceso_puerta","dispositivo_validacion","historial_rotacion_qr","ticket_qr_dinamico","boleto_nominado","comprobante_pago","metodo_pago","detalle_transaccion","entrada_digital","transaccion_compra","politica_devolucion","categoria_asiento","preferencias_musicales","perfil_usuario","evento_cultural","cupon_descuento","administrador_plataforma","usuario_cliente","artista","local_concierto","organizador_evento"};
   for(String tabla:tablas)s.executeUpdate("DELETE FROM "+tabla);
   s.executeUpdate("INSERT INTO usuario_cliente(id,nombres,apellidos,dni,email,contrasena_hash) VALUES (1,'A','Prueba','10000001','a@example.invalid','hash-no-login'),(2,'B','Prueba','10000002','b@example.invalid','hash-no-login')");
   s.executeUpdate("INSERT INTO administrador_plataforma(id,username,contrasena_hash,nivel_acceso) VALUES(1,'guardia1','hash-no-login','VALIDACION'),(2,'guardia2','hash-no-login','VALIDACION')");
   s.executeUpdate("INSERT INTO organizador_evento(id,ruc,razon_social,email_contacto) VALUES(1,'20000000001','Demo','demo@example.invalid')");
   s.executeUpdate("INSERT INTO local_concierto(id,nombre_local,direccion,capacidad_max,ciudad) VALUES(1,'Local','Demo',100,'Lima')");
   s.executeUpdate("INSERT INTO evento_cultural(id,nombre,tipo_evento,fecha,aforo,local_id,organizador_id,estado) VALUES(1,'Evento','CONCIERTO','2026-11-01 20:00:00',100,1,1,'PUBLICADO'),(2,'Otro','CONCIERTO','2026-11-01 20:00:00',100,1,1,'PUBLICADO')");
   s.executeUpdate("INSERT INTO categoria_asiento(id,evento_id,nombre_zona,precio_base,aforo_total,aforo_disponible) VALUES(1,1,'General',100,100,99)");
   s.executeUpdate("INSERT INTO entrada_digital(id,codigo_entrada,evento_id,categoria_id,comprador_id) VALUES(1,'"+CODIGO+"',1,1,1)");
   s.executeUpdate("INSERT INTO boleto_nominado(entrada_id,dni_asistente,nombres_asistente,apellidos_asistente) VALUES(1,'10000001','Asistente','Demo')");
   try(var p=c.prepareStatement("INSERT INTO dispositivo_validacion(id,operador_id,evento_id,nombre_puerta,token_acceso_hash) VALUES(1,1,1,'Norte',?),(2,2,1,'Sur',?)")){p.setString(1,HashUtil.sha256(DISPOSITIVO));p.setString(2,HashUtil.sha256(DISPOSITIVO+"2"));p.executeUpdate();}
  }
 }
 int count(String tabla)throws Exception{try(var c=conexiones.abrir();var s=c.createStatement();var r=s.executeQuery("SELECT COUNT(*) FROM "+tabla)){r.next();return r.getInt(1);}}
 void sql(String texto)throws Exception{try(var c=conexiones.abrir();var s=c.createStatement()){s.executeUpdate(texto);}}
 ResultadoAccesoDTO validar(String token,String dni)throws Exception{return acceso.validar(GUARDIA,1,DISPOSITIVO,UUID.randomUUID().toString(),token,dni);}
 @Test void schema24TablasUnaVista()throws Exception{try(var c=conexiones.abrir();var s=c.createStatement();var r=s.executeQuery("SELECT table_type,COUNT(*) FROM information_schema.tables WHERE table_schema=DATABASE() GROUP BY table_type")){Map<String,Integer> n=new HashMap<>();while(r.next())n.put(r.getString(1),r.getInt(2));assertEquals(24,n.get("BASE TABLE"));assertEquals(1,n.get("VIEW"));}}
 @Test void disponibilidadYFK()throws Exception{assertThrows(SQLException.class,()->sql("UPDATE categoria_asiento SET aforo_disponible=-1 WHERE id=1"));assertThrows(SQLException.class,()->sql("UPDATE entrada_digital SET categoria_id=999 WHERE id=1"));assertThrows(SQLException.class,()->sql("INSERT INTO boleto_nominado(entrada_id,dni_asistente,nombres_asistente,apellidos_asistente) VALUES(1,'10000001','Duplicado','Demo')"));}
 @Test void historialSinCompra()throws Exception{try(var c=conexiones.abrir();var s=c.createStatement();var r=s.executeQuery("SELECT total_boletos_comprados,ultima_compra_fecha FROM historial_compra WHERE cliente_id=1")){assertTrue(r.next());assertEquals(0,r.getLong(1));assertNull(r.getObject(2));}}
 @Test void propietarioYEstado()throws Exception{var otro=new SesionUsuario(2,SesionUsuario.Tipo.CLIENTE,SesionUsuario.Rol.CLIENTE,"B");assertThrows(SecurityException.class,()->qr.obtener(otro,1));sql("UPDATE entrada_digital SET estado_uso='ANULADA' WHERE id=1");assertThrows(IllegalStateException.class,()->qr.obtener(CLIENTE,1));assertEquals(0,count("ticket_qr_dinamico"));}
 @Test void mismaVentanaUnaAuditoria()throws Exception{var uno=qr.obtener(CLIENTE,1);var dos=qr.obtener(CLIENTE,1);assertEquals(uno.getContenido(),dos.getContenido());assertEquals(1,count("historial_rotacion_qr"));var futuro=new ServicioQr(conexiones,cifrado,Clock.fixed(AHORA.plusSeconds(30),ZoneOffset.UTC));assertNotEquals(uno.getContenido(),futuro.obtener(CLIENTE,1).getContenido());assertEquals(2,count("historial_rotacion_qr"));}
 @Test void accesoYReintento()throws Exception{String token=qr.obtener(CLIENTE,1).getContenido(),id=UUID.randomUUID().toString();var a=acceso.validar(GUARDIA,1,DISPOSITIVO,id,token,"10000001");assertTrue(a.isPermitido());var b=acceso.validar(GUARDIA,1,DISPOSITIVO,id,token,"10000001");assertTrue(b.isRecuperado());assertEquals(a.getControlId(),b.getControlId());assertEquals(1,count("control_acceso_puerta"));assertFalse(validar(token,"10000001").isPermitido());assertEquals(1,count("incidencia_acceso"));assertThrows(IllegalStateException.class,()->qr.obtener(CLIENTE,1));}
 @Test void identidadEventoYAlteracion()throws Exception{String token=qr.obtener(CLIENTE,1).getContenido();assertEquals("IDENTIDAD_NO_COINCIDE",validar(token,"99999999").getMotivo());sql("UPDATE dispositivo_validacion SET evento_id=2 WHERE id=1");assertEquals("EVENTO_INCORRECTO",validar(token,"10000001").getMotivo());sql("UPDATE dispositivo_validacion SET evento_id=1 WHERE id=1");String alterado=token.substring(0,token.length()-1)+(token.endsWith("0")?"1":"0");assertEquals("QR_ALTERADO",validar(alterado,"10000001").getMotivo());}
 @Test void expiradoNoConsume()throws Exception{String token=qr.obtener(CLIENTE,1).getContenido();var futuro=new ServicioAcceso(conexiones,cifrado,Clock.fixed(AHORA.plusSeconds(30),ZoneOffset.UTC));assertFalse(futuro.validar(GUARDIA,1,DISPOSITIVO,UUID.randomUUID().toString(),token,"10000001").isPermitido());assertTrue(validar(token,"10000001").isPermitido());}
 @Test void desconocidoSinEntrada()throws Exception{String desconocido=new QrPayload(UUID.randomUUID().toString(),Totp.intervalo(AHORA),"12345678").codificar();assertFalse(validar(desconocido,"10000001").isPermitido());try(var c=conexiones.abrir();var s=c.createStatement();var r=s.executeQuery("SELECT entrada_id FROM control_acceso_puerta")){r.next();assertNull(r.getObject(1));}}
 @Test void revocadoYRol()throws Exception{String token=qr.obtener(CLIENTE,1).getContenido();assertThrows(SecurityException.class,()->acceso.validar(CLIENTE,1,DISPOSITIVO,UUID.randomUUID().toString(),token,"10000001"));sql("UPDATE dispositivo_validacion SET estado_dispositivo='REVOCADO' WHERE id=1");assertThrows(SecurityException.class,()->validar(token,"10000001"));}
 @RepeatedTest(10) void concurrenciaDosDispositivos()throws Exception{String token=qr.obtener(CLIENTE,1).getContenido();ExecutorService pool=Executors.newFixedThreadPool(2);CountDownLatch inicio=new CountDownLatch(1);try{Future<ResultadoAccesoDTO> a=pool.submit(()->{inicio.await();return validar(token,"10000001");});Future<ResultadoAccesoDTO> b=pool.submit(()->{inicio.await();return acceso.validar(GUARDIA2,2,DISPOSITIVO+"2",UUID.randomUUID().toString(),token,"10000001");});inicio.countDown();int n=(a.get(10,TimeUnit.SECONDS).isPermitido()?1:0)+(b.get(10,TimeUnit.SECONDS).isPermitido()?1:0);assertEquals(1,n);assertEquals(2,count("control_acceso_puerta"));}finally{pool.shutdownNow();}}
 @Test void rollbackSiRegistroFalla()throws Exception{String token=qr.obtener(CLIENTE,1).getContenido();try(var c=conexiones.abrir();var s=c.createStatement()){s.execute("CREATE TRIGGER falla_control BEFORE INSERT ON control_acceso_puerta FOR EACH ROW SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Fallo inducido de prueba'");}try{assertThrows(SQLException.class,()->validar(token,"10000001"));assertEquals(0,count("control_acceso_puerta"));}finally{try(var c=conexiones.abrir();var s=c.createStatement()){s.execute("DROP TRIGGER falla_control");}}assertTrue(validar(token,"10000001").isPermitido());}
 @Test void reintentoCambiadoSeRechaza()throws Exception{String token=qr.obtener(CLIENTE,1).getContenido(),id=UUID.randomUUID().toString();acceso.validar(GUARDIA,1,DISPOSITIVO,id,token,"10000001");assertThrows(SecurityException.class,()->acceso.validar(GUARDIA,1,DISPOSITIVO,id,token,"10000002"));}
}
