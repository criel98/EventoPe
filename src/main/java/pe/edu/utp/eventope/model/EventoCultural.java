package pe.edu.utp.eventope.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Datos de evento_cultural. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class EventoCultural implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String nombre;
    private String descripcion;
    private String tipoEvento;
    private LocalDateTime fecha;
    private int aforo;
    private int localId;
    private int organizadorId;
    private Integer artistaId;
    private String estado;

    public EventoCultural() {}

    public EventoCultural(int id, String nombre, String descripcion, String tipoEvento, LocalDateTime fecha, int aforo, int localId, int organizadorId, Integer artistaId, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipoEvento = tipoEvento;
        this.fecha = fecha;
        this.aforo = aforo;
        this.localId = localId;
        this.organizadorId = organizadorId;
        this.artistaId = artistaId;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public int getAforo() { return aforo; }
    public void setAforo(int aforo) { this.aforo = aforo; }

    public int getLocalId() { return localId; }
    public void setLocalId(int localId) { this.localId = localId; }

    public int getOrganizadorId() { return organizadorId; }
    public void setOrganizadorId(int organizadorId) { this.organizadorId = organizadorId; }

    public Integer getArtistaId() { return artistaId; }
    public void setArtistaId(Integer artistaId) { this.artistaId = artistaId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }


}
