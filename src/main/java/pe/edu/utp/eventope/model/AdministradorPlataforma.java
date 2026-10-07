package pe.edu.utp.eventope.model;

import java.io.Serializable;

/** Datos de administrador_plataforma. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class AdministradorPlataforma implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String username;
    private String contrasenaHash;
    private String nivelAcceso;
    private String areaSoporte;
    private String estado;

    public AdministradorPlataforma() {}

    public AdministradorPlataforma(int id, String username, String contrasenaHash, String nivelAcceso, String areaSoporte, String estado) {
        this.id = id;
        this.username = username;
        this.contrasenaHash = contrasenaHash;
        this.nivelAcceso = nivelAcceso;
        this.areaSoporte = areaSoporte;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getContrasenaHash() { return contrasenaHash; }
    public void setContrasenaHash(String contrasenaHash) { this.contrasenaHash = contrasenaHash; }

    public String getNivelAcceso() { return nivelAcceso; }
    public void setNivelAcceso(String nivelAcceso) { this.nivelAcceso = nivelAcceso; }

    public String getAreaSoporte() { return areaSoporte; }
    public void setAreaSoporte(String areaSoporte) { this.areaSoporte = areaSoporte; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }


}
