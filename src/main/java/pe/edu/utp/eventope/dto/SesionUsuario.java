package pe.edu.utp.eventope.dto;
import java.io.Serializable;
/** Crear solamente después de verificar credenciales en servidor. No recibir el rol del formulario. */
public final class SesionUsuario implements Serializable {
    private static final long serialVersionUID = 1L;
    public enum Tipo { CLIENTE, PERSONAL }
    public enum Rol { CLIENTE, ADMIN, VALIDACION, SOPORTE }
    private final int id; private final Tipo tipo; private final Rol rol; private final String nombre;
    public SesionUsuario(int id, Tipo tipo, Rol rol, String nombre) {
        if(id <= 0 || tipo == null || rol == null || nombre == null) throw new IllegalArgumentException("Sesión inválida");
        if ((tipo == Tipo.CLIENTE) != (rol == Rol.CLIENTE)) throw new IllegalArgumentException("Tipo y rol incompatibles");
        this.id=id;this.tipo=tipo;this.rol=rol;this.nombre=nombre;
    }
    public int getId(){return id;} public Tipo getTipo(){return tipo;} public Rol getRol(){return rol;} public String getNombre(){return nombre;}
    public void exigirCliente(){if(tipo!=Tipo.CLIENTE)throw new SecurityException("Se requiere cuenta de cliente");}
    public void exigirValidacion(){if(tipo!=Tipo.PERSONAL||rol!=Rol.VALIDACION)throw new SecurityException("Se requiere personal de validación");}
}
