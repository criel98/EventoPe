package com.tienda.model;

import java.time.LocalDateTime;

public class TicketQR_Dinamico {
    private int id;
    private int entradaId;
    private String tokenEncriptado;
    private LocalDateTime fechaExpiracion;
    private int iteracionClave;

    public TicketQR_Dinamico() {
    }

    public TicketQR_Dinamico(int id, int entradaId, String tokenEncriptado,
            LocalDateTime fechaExpiracion, int iteracionClave) {
        this.id = id;
        this.entradaId = entradaId;
        this.tokenEncriptado = tokenEncriptado;
        this.fechaExpiracion = fechaExpiracion;
        this.iteracionClave = iteracionClave;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEntradaId() {
        return entradaId;
    }

    public void setEntradaId(int entradaId) {
        this.entradaId = entradaId;
    }

    public String getTokenEncriptado() {
        return tokenEncriptado;
    }

    public void setTokenEncriptado(String tokenEncriptado) {
        this.tokenEncriptado = tokenEncriptado;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public int getIteracionClave() {
        return iteracionClave;
    }

    public void setIteracionClave(int iteracionClave) {
        this.iteracionClave = iteracionClave;
    }
}