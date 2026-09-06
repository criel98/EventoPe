package com.tienda.model;

public class ControlAccesoPuerta {
    private int id;
    private String nombrePuerta;
    private String ubicacion;

    public ControlAccesoPuerta() {
    }

    public ControlAccesoPuerta(int id, String nombrePuerta, String ubicacion) {
        this.id = id;
        this.nombrePuerta = nombrePuerta;
        this.ubicacion = ubicacion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombrePuerta() {
        return nombrePuerta;
    }

    public void setNombrePuerta(String nombrePuerta) {
        this.nombrePuerta = nombrePuerta;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
