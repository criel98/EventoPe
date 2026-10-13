package pe.edu.utp.eventope.service;
import pe.edu.utp.eventope.config.*;
import pe.edu.utp.eventope.dao.*;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.util.PasswordUtil;
import java.sql.*;
import java.util.*;
public final class ServicioAutenticacion {
    private final ProveedorConexion conexiones;
    public ServicioAutenticacion(){this(()->new ConexionBD(Configuracion.cargar()).abrir());}
    public ServicioAutenticacion(ProveedorConexion conexiones){this.conexiones=conexiones;}
    public Optional<SesionUsuario> autenticarCliente(String email,char[] clave){
        if(email==null||email.length()>100||clave==null||clave.length>128)return Optional.empty();
        try(Connection c=conexiones.abrir()){
            var encontrado=new UsuarioClienteDAO().buscarPorEmail(c,email.trim().toLowerCase(Locale.ROOT));
            if(encontrado.isEmpty())return Optional.empty();
            var u=encontrado.get();
            if(!"ACTIVO".equals(u.getEstado())||!PasswordUtil.verificar(clave,u.getContrasenaHash()))return Optional.empty();
            return Optional.of(new SesionUsuario(u.getId(),SesionUsuario.Tipo.CLIENTE,SesionUsuario.Rol.CLIENTE,u.getNombres()+" "+u.getApellidos()));
        }catch(SQLException e){throw new IllegalStateException("No se pudo iniciar sesión. Inténtalo nuevamente.",e);}
    }
    public Optional<SesionUsuario> autenticarPersonal(String nombre,char[] clave){
        if(nombre==null||nombre.length()>50||clave==null||clave.length>128)return Optional.empty();
        try(Connection c=conexiones.abrir()){
            var encontrado=new PersonalDAO().buscarPorUsername(c,nombre.trim());
            if(encontrado.isEmpty())return Optional.empty();
            var u=encontrado.get();
            if(!"ACTIVO".equals(u.getEstado())||!"VALIDACION".equals(u.getNivelAcceso())||!PasswordUtil.verificar(clave,u.getContrasenaHash()))return Optional.empty();
            return Optional.of(new SesionUsuario(u.getId(),SesionUsuario.Tipo.PERSONAL,SesionUsuario.Rol.VALIDACION,u.getUsername()));
        }catch(SQLException e){throw new IllegalStateException("No se pudo iniciar sesión. Inténtalo nuevamente.",e);}
    }
}
