package pe.edu.utp.eventope.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Datos de incidencia_acceso. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class IncidenciaAcceso implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int controlId;
    private String tipoIncidencia;
    private String descripcion;
    private String severidad;
    private String estadoResolucion;
    private Integer resueltaPorId;
    private LocalDateTime fechaResolucion;
    private String motivoResolucion;

    public IncidenciaAcceso() {}

    public IncidenciaAcceso(int id, int controlId, String tipoIncidencia, String descripcion, String severidad, String estadoResolucion, Integer resueltaPorId, LocalDateTime fechaResolucion, String motivoResolucion) {
        this.id = id;
        this.controlId = controlId;
        this.tipoIncidencia = tipoIncidencia;
        this.descripcion = descripcion;
        this.severidad = severidad;
        this.estadoResolucion = estadoResolucion;
        this.resueltaPorId = resueltaPorId;
        this.fechaResolucion = fechaResolucion;
        this.motivoResolucion = motivoResolucion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getControlId() { return controlId; }
    public void setControlId(int controlId) { this.controlId = controlId; }

    public String getTipoIncidencia() { return tipoIncidencia; }
    public void setTipoIncidencia(String tipoIncidencia) { this.tipoIncidencia = tipoIncidencia; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getSeveridad() { return severidad; }
    public void setSeveridad(String severidad) { this.severidad = severidad; }

    public String getEstadoResolucion() { return estadoResolucion; }
    public void setEstadoResolucion(String estadoResolucion) { this.estadoResolucion = estadoResolucion; }

    public Integer getResueltaPorId() { return resueltaPorId; }
    public void setResueltaPorId(Integer resueltaPorId) { this.resueltaPorId = resueltaPorId; }

    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }

    public String getMotivoResolucion() { return motivoResolucion; }
    public void setMotivoResolucion(String motivoResolucion) { this.motivoResolucion = motivoResolucion; }


}
