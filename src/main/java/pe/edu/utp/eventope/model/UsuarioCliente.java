package pe.edu.utp.eventope.model;

import java.io.Serializable;

/** Datos de usuario_cliente. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class UsuarioCliente implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String nombres;
    private String apellidos;
    private String dni;
    private String email;
    private String contrasenaHash;
    private String telefono;
    private String estado;

    public UsuarioCliente() {}

    public UsuarioCliente(int id, String nombres, String apellidos, String dni, String email, String contrasenaHash, String telefono, String estado) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dni = dni;
        this.email = email;
        this.contrasenaHash = contrasenaHash;
        this.telefono = telefono;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContrasenaHash() { return contrasenaHash; }
    public void setContrasenaHash(String contrasenaHash) { this.contrasenaHash = contrasenaHash; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }


}
