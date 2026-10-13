package pe.edu.utp.eventope.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class EventoResumenDTO {
    private int id;
    private String nombre;
    private String descripcion;
    private String tipoEvento;
    private LocalDateTime fecha;
    private int aforo;
    private String localNombre;
    private String artistaNombre;
    private BigDecimal precioDesde;

    public EventoResumenDTO() {
    }

    public EventoResumenDTO(int id, String nombre, String descripcion, String tipoEvento, LocalDateTime fecha, int aforo,
                           String localNombre, String artistaNombre, BigDecimal precioDesde) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipoEvento = tipoEvento;
        this.fecha = fecha;
        this.aforo = aforo;
        this.localNombre = localNombre;
        this.artistaNombre = artistaNombre;
        this.precioDesde = precioDesde;
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

    public String getLocalNombre() { return localNombre; }
    public void setLocalNombre(String localNombre) { this.localNombre = localNombre; }

    public String getArtistaNombre() { return artistaNombre; }
    public void setArtistaNombre(String artistaNombre) { this.artistaNombre = artistaNombre; }

    public BigDecimal getPrecioDesde() { return precioDesde; }
    public void setPrecioDesde(BigDecimal precioDesde) { this.precioDesde = precioDesde; }
}
