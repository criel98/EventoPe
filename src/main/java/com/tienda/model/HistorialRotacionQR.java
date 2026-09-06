package com.tienda.model;

import java.time.LocalDateTime;

public class HistorialRotacionQR {
    private int id;
    private int qrId;
    private LocalDateTime horaGeneracion;
    private String hashAnterior;
    private String hashNuevo;

    public HistorialRotacionQR() {
    }

    public HistorialRotacionQR(int id, int qrId, LocalDateTime horaGeneracion,
            String hashAnterior, String hashNuevo) {
        this.id = id;
        this.qrId = qrId;
        this.horaGeneracion = horaGeneracion;
        this.hashAnterior = hashAnterior;
        this.hashNuevo = hashNuevo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQrId() {
        return qrId;
    }

    public void setQrId(int qrId) {
        this.qrId = qrId;
    }

    public LocalDateTime getHoraGeneracion() {
        return horaGeneracion;
    }

    public void setHoraGeneracion(LocalDateTime horaGeneracion) {
        this.horaGeneracion = horaGeneracion;
    }

    public String getHashAnterior() {
        return hashAnterior;
    }

    public void setHashAnterior(String hashAnterior) {
        this.hashAnterior = hashAnterior;
    }

    public String getHashNuevo() {
        return hashNuevo;
    }

    public void setHashNuevo(String hashNuevo) {
        this.hashNuevo = hashNuevo;
    }
}