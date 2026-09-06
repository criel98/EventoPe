package com.tienda.model;

public class PoliticaDevolucion {
    private int id;
    private int eventoId;
    private int diasMaximosPrevios;
    private double penalizacionPorcentaje;
    private boolean estadoActivo;

    public PoliticaDevolucion() {
    }

    public PoliticaDevolucion(int id, int eventoId, int diasMaximosPrevios,
            double penalizacionPorcentaje, boolean estadoActivo) {
        this.id = id;
        this.eventoId = eventoId;
        this.diasMaximosPrevios = diasMaximosPrevios;
        this.penalizacionPorcentaje = penalizacionPorcentaje;
        this.estadoActivo = estadoActivo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventoId() {
        return eventoId;
    }

    public void setEventoId(int eventoId) {
        this.eventoId = eventoId;
    }

    public int getDiasMaximosPrevios() {
        return diasMaximosPrevios;
    }

    public void setDiasMaximosPrevios(int diasMaximosPrevios) {
        this.diasMaximosPrevios = diasMaximosPrevios;
    }

    public double getPenalizacionPorcentaje() {
        return penalizacionPorcentaje;
    }

    public void setPenalizacionPorcentaje(double penalizacionPorcentaje) {
        this.penalizacionPorcentaje = penalizacionPorcentaje;
    }

    public boolean isEstadoActivo() {
        return estadoActivo;
    }

    public void setEstadoActivo(boolean estadoActivo) {
        this.estadoActivo = estadoActivo;
    }
}