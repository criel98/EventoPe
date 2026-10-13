package pe.edu.utp.eventope.service;
import pe.edu.utp.eventope.config.*;
import pe.edu.utp.eventope.dao.*;
import pe.edu.utp.eventope.model.*;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.util.PasswordUtil;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public final class ServicioCuenta {
    private final ProveedorConexion conexiones;
    private final UsuarioClienteDAO usuarios=new UsuarioClienteDAO();
    private final PerfilUsuarioDAO perfiles=new PerfilUsuarioDAO();
    private final PreferenciasDAO preferencias=new PreferenciasDAO();
    public ServicioCuenta(){this(()->new ConexionBD(Configuracion.cargar()).abrir());}
    public ServicioCuenta(ProveedorConexion conexiones){this.conexiones=conexiones;}

    public boolean registrarClienteCompleto(UsuarioCliente u,char[] clave,PerfilUsuario p,PreferenciasMusicales pref) {
        validar(u,p,pref);
        if(clave==null||clave.length<12||clave.length>128)throw error("password","La contraseña debe tener entre 12 y 128 caracteres.");
        u.setContrasenaHash(PasswordUtil.crear(clave));u.setEstado("ACTIVO");u.setId(0);
        try(Connection c=conexiones.abrir()){
            c.setAutoCommit(false);
            try{
                usuarios.insertar(c,u);
                if(u.getId()<=0)throw new SQLException("No se obtuvo el identificador del cliente");
                p.setClienteId(u.getId());p.setEstadoVerificacion(false);perfiles.insertar(c,p);
                pref.setClienteId(u.getId());preferencias.insertar(c,pref);
                c.commit();return true;
            }catch(SQLException|RuntimeException e){c.rollback();u.setId(0);throw e;}
        }catch(SQLException e){throw traducir(e);}
    }

    public Map<String,Object> obtenerPerfilCompleto(SesionUsuario actor) {
        actor.exigirCliente();
        try(Connection c=conexiones.abrir()){
            var u=usuarios.buscarPorId(c,actor.getId()).orElseThrow(()->new SecurityException("Cuenta no disponible"));
            if(!"ACTIVO".equals(u.getEstado()))throw new SecurityException("Cuenta no disponible");
            u.setContrasenaHash(null);
            var resultado=new HashMap<String,Object>();resultado.put("cliente",u);
            resultado.put("perfil",perfiles.buscarPorClienteId(c,actor.getId()).orElseGet(PerfilUsuario::new));
            resultado.put("preferencias",preferencias.buscarPorClienteId(c,actor.getId()).orElseGet(PreferenciasMusicales::new));
            return resultado;
        }catch(SQLException e){throw traducir(e);}
    }

    public void actualizarPerfil(SesionUsuario actor,UsuarioCliente u,PerfilUsuario p,PreferenciasMusicales pref) {
        actor.exigirCliente();validar(u,p,pref);
        try(Connection c=conexiones.abrir()){
            c.setAutoCommit(false);
            try{
                try(var st=c.prepareStatement("SELECT estado FROM usuario_cliente WHERE id=? FOR UPDATE")){
                    st.setInt(1,actor.getId());try(var rs=st.executeQuery()){
                        if(!rs.next()||!"ACTIVO".equals(rs.getString(1)))throw new SecurityException("Cuenta no disponible");
                    }
                }
                u.setId(actor.getId());p.setClienteId(actor.getId());pref.setClienteId(actor.getId());
                usuarios.actualizar(c,u);perfiles.actualizar(c,p);preferencias.actualizar(c,pref);c.commit();
            }catch(SQLException|RuntimeException e){c.rollback();throw e;}
        }catch(SQLException e){throw traducir(e);}
    }

    private static RuntimeException traducir(SQLException e){
        if(e.getErrorCode()==1062)return new ValidacionCuentaException(Map.of("email","El correo o DNI ya está registrado.","dni","El correo o DNI ya está registrado."));
        return new IllegalStateException("No se pudo guardar o consultar la cuenta. Inténtalo nuevamente.",e);
    }
    public static ValidacionCuentaException error(String campo,String texto){return new ValidacionCuentaException(Map.of(campo,texto));}
    private static String texto(String s){return s==null?"":s.trim();}
    private static String opcional(String s){s=texto(s);return s.isEmpty()?null:s;}
    private static void validar(UsuarioCliente u,PerfilUsuario p,PreferenciasMusicales pref){
        var errores=new LinkedHashMap<String,String>();
        u.setNombres(texto(u.getNombres()));u.setApellidos(texto(u.getApellidos()));u.setDni(texto(u.getDni()));
        u.setEmail(texto(u.getEmail()).toLowerCase(Locale.ROOT));u.setTelefono(opcional(u.getTelefono()));
        p.setPais(opcional(p.getPais()));pref.setGeneroFavorito(opcional(pref.getGeneroFavorito()));pref.setArtistaFavorito(opcional(pref.getArtistaFavorito()));
        if(u.getNombres().isEmpty()||u.getNombres().length()>100)errores.put("nombres","Escribe los nombres (máximo 100 caracteres).");
        if(u.getApellidos().isEmpty()||u.getApellidos().length()>100)errores.put("apellidos","Escribe los apellidos (máximo 100 caracteres).");
        if(!u.getDni().matches("[0-9]{8}"))errores.put("dni","El DNI debe tener ocho dígitos.");
        if(u.getEmail().length()>100||!u.getEmail().matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))errores.put("email","Escribe un correo válido (máximo 100 caracteres).");
        if(u.getTelefono()!=null&&!u.getTelefono().matches("[+0-9 ()-]{6,20}"))errores.put("telefono","Escribe un teléfono válido (6 a 20 caracteres).");
        if(p.getPais()!=null&&p.getPais().length()>50)errores.put("pais","Máximo 50 caracteres.");
        if(p.getFechaNacimiento()!=null&&(p.getFechaNacimiento().isAfter(LocalDate.now())||p.getFechaNacimiento().isBefore(LocalDate.of(1900,1,1))))errores.put("fechaNacimiento","Revisa la fecha de nacimiento.");
        if(pref.getGeneroFavorito()!=null&&pref.getGeneroFavorito().length()>50)errores.put("generoMusical","Máximo 50 caracteres.");
        if(pref.getArtistaFavorito()!=null&&pref.getArtistaFavorito().length()>100)errores.put("artistaFavorito","Máximo 100 caracteres.");
        if(!errores.isEmpty())throw new ValidacionCuentaException(errores);
    }
}
