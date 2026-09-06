package com.tienda.model;

public class EntradaDigital {
    private int id;
    private String codigoEntrada;
    private int eventoId;
    private int compradorId;
    private String estadoUso;

    public EntradaDigital() {
    }

    public EntradaDigital(int id, String codigoEntrada, int eventoId, int compradorId, String estadoUso) {
        this.id = id;
        this.codigoEntrada = codigoEntrada;
        this.eventoId = eventoId;
        this.compradorId = compradorId;
        this.estadoUso = estadoUso;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoEntrada() {
        return codigoEntrada;
    }

    public void setCodigoEntrada(String codigoEntrada) {
        this.codigoEntrada = codigoEntrada;
    }

    public int getEventoId() {
        return eventoId;
    }

    public void setEventoId(int eventoId) {
        this.eventoId = eventoId;
    }

    public int getCompradorId() {
        return compradorId;
    }

    public void setCompradorId(int compradorId) {
        this.compradorId = compradorId;
    }

    public String getEstadoUso() {
        return estadoUso;
    }

    public void setEstadoUso(String estadoUso) {
        this.estadoUso = estadoUso;
    }
}