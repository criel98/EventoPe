package com.tienda.model;

public class IncidenciaAcceso {
    private int id;
    private String descripcion;
    private String fechaHora;

    public IncidenciaAcceso() {
    }

    public IncidenciaAcceso(int id, String descripcion, String fechaHora) {
        this.id = id;
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }
}
