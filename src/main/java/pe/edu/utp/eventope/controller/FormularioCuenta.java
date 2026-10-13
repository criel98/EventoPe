package pe.edu.utp.eventope.controller;
import jakarta.servlet.http.HttpServletRequest;
import pe.edu.utp.eventope.model.*;
import pe.edu.utp.eventope.service.ServicioCuenta;
import java.time.LocalDate;
import java.util.*;
/** Adaptador de formularios: nunca lee identificadores, roles o estados como identidad. */
final class FormularioCuenta {
    static final String[] CAMPOS={"nombres","apellidos","dni","email","telefono","pais","fechaNacimiento","generoMusical","artistaFavorito"};
    static Map<String,String> valores(HttpServletRequest r){
        var m=new LinkedHashMap<String,String>();for(String campo:CAMPOS){String s=r.getParameter(campo);m.put(campo,s==null?"":s);}return m;
    }
    static UsuarioCliente cliente(Map<String,String> m){
        var u=new UsuarioCliente();u.setNombres(m.get("nombres"));u.setApellidos(m.get("apellidos"));u.setDni(m.get("dni"));u.setEmail(m.get("email"));u.setTelefono(m.get("telefono"));return u;
    }
    static PerfilUsuario perfil(Map<String,String> m){
        var p=new PerfilUsuario();p.setPais(m.get("pais"));String fecha=m.get("fechaNacimiento");
        if(fecha!=null&&!fecha.isBlank())try{p.setFechaNacimiento(LocalDate.parse(fecha));}catch(java.time.format.DateTimeParseException e){throw ServicioCuenta.error("fechaNacimiento","Escribe una fecha válida.");}
        return p;
    }
    static PreferenciasMusicales preferencias(Map<String,String> m){var p=new PreferenciasMusicales();p.setGeneroFavorito(m.get("generoMusical"));p.setArtistaFavorito(m.get("artistaFavorito"));return p;}
    static Map<String,String> desdePerfil(Map<String,Object> datos){
        var u=(UsuarioCliente)datos.get("cliente");var p=(PerfilUsuario)datos.get("perfil");var pref=(PreferenciasMusicales)datos.get("preferencias");
        var m=new LinkedHashMap<String,String>();m.put("nombres",u.getNombres());m.put("apellidos",u.getApellidos());m.put("dni",u.getDni());m.put("email",u.getEmail());m.put("telefono",u.getTelefono());m.put("pais",p.getPais());m.put("fechaNacimiento",p.getFechaNacimiento()==null?"":p.getFechaNacimiento().toString());m.put("generoMusical",pref.getGeneroFavorito());m.put("artistaFavorito",pref.getArtistaFavorito());return m;
    }
}
