package pe.edu.utp.eventope;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import pe.edu.utp.eventope.config.*;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.model.*;
import pe.edu.utp.eventope.service.*;
import pe.edu.utp.eventope.util.PasswordUtil;
import java.sql.*;
import java.lang.reflect.*;
import java.time.LocalDate;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named="EVENTOPE_TEST_DB_URL",matches=".+")
class CuentaMySqlTest {
 ServicioCuenta cuenta; ServicioAutenticacion auth;
 static final char[] CLAVE="ClientePrueba2026!".toCharArray();
 @BeforeEach void preparar()throws Exception{DatosPrueba.limpiar();cuenta=new ServicioCuenta(DatosPrueba.conexiones());auth=new ServicioAutenticacion(DatosPrueba.conexiones());}
 UsuarioCliente usuario(String dni,String email){var u=new UsuarioCliente();u.setNombres("María");u.setApellidos("Muñoz");u.setDni(dni);u.setEmail(email);return u;}
 UsuarioCliente registrar(String dni,String email){var u=usuario(dni,email);assertTrue(cuenta.registrarClienteCompleto(u,CLAVE,new PerfilUsuario(),new PreferenciasMusicales()));return u;}
 SesionUsuario actor(UsuarioCliente u){return new SesionUsuario(u.getId(),SesionUsuario.Tipo.CLIENTE,SesionUsuario.Rol.CLIENTE,u.getNombres());}
 @Test void registroCompletoYOpcionalesVacios()throws Exception{
  var u=registrar("12345678","TEST@EXAMPLE.INVALID");assertEquals(1,DatosPrueba.count("usuario_cliente"));assertEquals(1,DatosPrueba.count("perfil_usuario"));assertEquals(1,DatosPrueba.count("preferencias_musicales"));
  assertTrue(auth.autenticarCliente(" test@example.invalid ",CLAVE).isPresent());
  var datos=cuenta.obtenerPerfilCompleto(actor(u));assertNull(((UsuarioCliente)datos.get("cliente")).getContrasenaHash());assertNull(((PerfilUsuario)datos.get("perfil")).getFechaNacimiento());assertNull(((PreferenciasMusicales)datos.get("preferencias")).getGeneroFavorito());
 }
 @Test void contrasenaIncorrectaCuentaInactivaYDesconocida()throws Exception{
  registrar("12345678","a@example.invalid");assertTrue(auth.autenticarCliente("a@example.invalid","incorrecta".toCharArray()).isEmpty());assertTrue(auth.autenticarCliente("nadie@example.invalid",CLAVE).isEmpty());
  DatosPrueba.sql("UPDATE usuario_cliente SET estado='INACTIVO'");assertTrue(auth.autenticarCliente("a@example.invalid",CLAVE).isEmpty());
 }
 @Test void correoDuplicadoNoDejaFilasParciales()throws Exception{
  registrar("12345678","a@example.invalid");assertThrows(ValidacionCuentaException.class,()->registrar("87654321","a@example.invalid"));assertEquals(1,DatosPrueba.count("usuario_cliente"));assertEquals(1,DatosPrueba.count("perfil_usuario"));
 }
 @Test void dniDuplicadoSeRechaza()throws Exception{registrar("12345678","a@example.invalid");assertThrows(ValidacionCuentaException.class,()->registrar("12345678","b@example.invalid"));assertEquals(1,DatosPrueba.count("preferencias_musicales"));}
 @Test void falloIntermedioRevierteRegistro()throws Exception{
  ProveedorConexion fallo=()->{
   Connection real=DatosPrueba.conexiones().abrir();
   return (Connection)Proxy.newProxyInstance(Connection.class.getClassLoader(),new Class[]{Connection.class},(p,m,a)->{
    if(m.getName().equals("prepareStatement")&&((String)a[0]).startsWith("INSERT INTO perfil_usuario"))throw new SQLException("Fallo provocado");
    try{return m.invoke(real,a);}catch(InvocationTargetException e){throw e.getCause();}
   });
  };
  assertThrows(IllegalStateException.class,()->new ServicioCuenta(fallo).registrarClienteCompleto(usuario("12345678","a@example.invalid"),CLAVE,new PerfilUsuario(),new PreferenciasMusicales()));
  assertEquals(0,DatosPrueba.count("usuario_cliente"));assertEquals(0,DatosPrueba.count("perfil_usuario"));
 }
 @Test void registrosSimultaneosNoDuplican()throws Exception{
  var ejecutor=Executors.newFixedThreadPool(2);var inicio=new CountDownLatch(1);
  Callable<Boolean> accion=()->{inicio.await();try{registrar("12345678","a@example.invalid");return true;}catch(ValidacionCuentaException e){return false;}};
  try{var a=ejecutor.submit(accion);var b=ejecutor.submit(accion);inicio.countDown();assertEquals(1,(a.get(15,TimeUnit.SECONDS)?1:0)+(b.get(15,TimeUnit.SECONDS)?1:0));assertEquals(1,DatosPrueba.count("usuario_cliente"));}finally{ejecutor.shutdownNow();}
 }
 @Test void editarSoloCuentaDeSesionIgnoraIdDelFormulario()throws Exception{
  var a=registrar("12345678","a@example.invalid");var b=registrar("87654321","b@example.invalid");var cambio=usuario("12345678","a@example.invalid");cambio.setId(b.getId());cambio.setNombres("Ángela");var pref=new PreferenciasMusicales();pref.setGeneroFavorito("Jazz");
  cuenta.actualizarPerfil(actor(a),cambio,new PerfilUsuario(),pref);
  assertEquals("Ángela",((UsuarioCliente)cuenta.obtenerPerfilCompleto(actor(a)).get("cliente")).getNombres());assertEquals("María",((UsuarioCliente)cuenta.obtenerPerfilCompleto(actor(b)).get("cliente")).getNombres());assertTrue(auth.autenticarCliente("a@example.invalid",CLAVE).isPresent());
 }
 @Test void actualizarConDuplicadoRevierteTodo()throws Exception{
  var a=registrar("12345678","a@example.invalid");registrar("87654321","b@example.invalid");var cambio=usuario("12345678","b@example.invalid");cambio.setNombres("Cambiado");
  assertThrows(ValidacionCuentaException.class,()->cuenta.actualizarPerfil(actor(a),cambio,new PerfilUsuario(),new PreferenciasMusicales()));assertEquals("María",((UsuarioCliente)cuenta.obtenerPerfilCompleto(actor(a)).get("cliente")).getNombres());
 }
 @Test void personalNoPuedeEditarClienteConMismoId()throws Exception{
  var a=registrar("12345678","a@example.invalid");var guardia=new SesionUsuario(a.getId(),SesionUsuario.Tipo.PERSONAL,SesionUsuario.Rol.VALIDACION,"Guardia");
  assertThrows(SecurityException.class,()->cuenta.obtenerPerfilCompleto(guardia));assertThrows(SecurityException.class,()->cuenta.actualizarPerfil(guardia,a,new PerfilUsuario(),new PreferenciasMusicales()));
 }
 @Test void validacionesRechazanDatosInvalidos()throws Exception{
  assertThrows(ValidacionCuentaException.class,()->registrar("123","correo"));
  var p=new PerfilUsuario();p.setFechaNacimiento(LocalDate.now().plusDays(1));assertThrows(ValidacionCuentaException.class,()->cuenta.registrarClienteCompleto(usuario("12345678","a@example.invalid"),CLAVE,p,new PreferenciasMusicales()));assertEquals(0,DatosPrueba.count("usuario_cliente"));
 }
 @Test void personalSoloValidacionActiva()throws Exception{
  try(var c=DatosPrueba.conexiones().abrir();var p=c.prepareStatement("INSERT INTO administrador_plataforma(username,contrasena_hash,nivel_acceso,estado) VALUES(?,?,'VALIDACION','ACTIVO')")){p.setString(1,"guardia");p.setString(2,PasswordUtil.crear(CLAVE));p.executeUpdate();}
  assertTrue(auth.autenticarPersonal("guardia",CLAVE).isPresent());assertTrue(auth.autenticarPersonal("guardia","mala".toCharArray()).isEmpty());DatosPrueba.sql("UPDATE administrador_plataforma SET nivel_acceso='ADMIN'");assertTrue(auth.autenticarPersonal("guardia",CLAVE).isEmpty());DatosPrueba.sql("UPDATE administrador_plataforma SET nivel_acceso='VALIDACION',estado='INACTIVO'");assertTrue(auth.autenticarPersonal("guardia",CLAVE).isEmpty());
 }
}
