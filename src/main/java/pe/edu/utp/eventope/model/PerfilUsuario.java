package pe.edu.utp.eventope.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalDate;

/** Datos de perfil_usuario. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class PerfilUsuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int clienteId;
    private LocalDateTime fechaRegistro;
    private boolean estadoVerificacion;
    private String pais;
    private LocalDate fechaNacimiento;

    public PerfilUsuario() {}

    public PerfilUsuario(int id, int clienteId, LocalDateTime fechaRegistro, boolean estadoVerificacion, String pais, LocalDate fechaNacimiento) {
        this.id = id;
        this.clienteId = clienteId;
        this.fechaRegistro = fechaRegistro;
        this.estadoVerificacion = estadoVerificacion;
        this.pais = pais;
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public boolean isEstadoVerificacion() { return estadoVerificacion; }
    public void setEstadoVerificacion(boolean estadoVerificacion) { this.estadoVerificacion = estadoVerificacion; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }


}
