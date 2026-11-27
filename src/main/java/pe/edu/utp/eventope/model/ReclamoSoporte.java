package pe.edu.utp.eventope.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Datos de reclamo_soporte. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class ReclamoSoporte implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int clienteId;
    private Integer adminId;
    private Integer incidenciaId;
    private String asunto;
    private String descripcion;
    private String estadoReclamo;
    private LocalDateTime fechaCreacion;
    private String respuesta;

    public ReclamoSoporte() {}

    public ReclamoSoporte(int id, int clienteId, Integer adminId, Integer incidenciaId, String asunto, String descripcion, String estadoReclamo, LocalDateTime fechaCreacion, String respuesta) {
        this.id = id;
        this.clienteId = clienteId;
        this.adminId = adminId;
        this.incidenciaId = incidenciaId;
        this.asunto = asunto;
        this.descripcion = descripcion;
        this.estadoReclamo = estadoReclamo;
        this.fechaCreacion = fechaCreacion;
        this.respuesta = respuesta;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public Integer getAdminId() { return adminId; }
    public void setAdminId(Integer adminId) { this.adminId = adminId; }

    public Integer getIncidenciaId() { return incidenciaId; }
    public void setIncidenciaId(Integer incidenciaId) { this.incidenciaId = incidenciaId; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstadoReclamo() { return estadoReclamo; }
    public void setEstadoReclamo(String estadoReclamo) { this.estadoReclamo = estadoReclamo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }


}
