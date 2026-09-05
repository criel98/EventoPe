package com.tienda.model;

import java.time.LocalDateTime;

public class ComprobantePago {
    private String id;
    private LocalDateTime fecha;
    private double monto;
    private MetodoPago metodo;
    private String estado; // e.g., PENDIENTE, PAGADO

    public ComprobantePago(String id, LocalDateTime fecha, double monto, MetodoPago metodo) {
        this.id = id;
        this.fecha = fecha;
        this.monto = monto;
        this.metodo = metodo;
        this.estado = "PENDIENTE";
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public double getMonto() {
        return monto;
    }

    public MetodoPago getMetodo() {
        return metodo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "ComprobantePago{" +
                "id='" + id + '\'' +
                ", fecha=" + fecha +
                ", monto=" + monto +
                ", metodo=" + metodo +
                ", estado='" + estado + '\'' +
                '}';
    }
}
