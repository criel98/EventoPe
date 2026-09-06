package com.tienda.model;

public class DispositivoValidacion {
    private int id;
    private String codigoDispositivo;
    private String estado;

    public DispositivoValidacion() {
    }

    public DispositivoValidacion(int id, String codigoDispositivo, String estado) {
        this.id = id;
        this.codigoDispositivo = codigoDispositivo;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoDispositivo() {
        return codigoDispositivo;
    }

    public void setCodigoDispositivo(String codigoDispositivo) {
        this.codigoDispositivo = codigoDispositivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
