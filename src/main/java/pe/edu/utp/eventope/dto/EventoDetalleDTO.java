package pe.edu.utp.eventope.dto;

import pe.edu.utp.eventope.model.CategoriaAsiento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class EventoDetalleDTO {
    private int id;
    private String nombre;
    private String descripcion;
    private String tipoEvento;
    private LocalDateTime fecha;
    private int aforo;
    private String localNombre;
    private String ciudad;
    private String artistaNombre;
    private final List<CategoriaAsiento> categorias = new ArrayList<>();

    public EventoDetalleDTO() {
    }

    public EventoDetalleDTO(int id, String nombre, String descripcion, String tipoEvento, LocalDateTime fecha,
                           int aforo, String localNombre, String ciudad, String artistaNombre) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipoEvento = tipoEvento;
        this.fecha = fecha;
        this.aforo = aforo;
        this.localNombre = localNombre;
        this.ciudad = ciudad;
        this.artistaNombre = artistaNombre;
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

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getArtistaNombre() { return artistaNombre; }
    public void setArtistaNombre(String artistaNombre) { this.artistaNombre = artistaNombre; }

    public List<CategoriaAsiento> getCategorias() { return categorias; }
    public void setCategorias(List<CategoriaAsiento> categorias) { this.categorias.clear(); if (categorias != null) this.categorias.addAll(categorias); }
}
