package com.tienda.model;

public class ReclamoSoporte {
    private int id;
    private String asunto;
    private String detalle;
    private String estado;

    public ReclamoSoporte() {
    }

    public ReclamoSoporte(int id, String asunto, String detalle, String estado) {
        this.id = id;
        this.asunto = asunto;
        this.detalle = detalle;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
