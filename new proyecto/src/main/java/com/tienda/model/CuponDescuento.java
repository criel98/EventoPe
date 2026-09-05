package com.tienda.model;

import java.time.LocalDate;

public class CuponDescuento {
    private String codigo;
    private double porcentaje; // e.g., 10.0 means 10%
    private LocalDate fechaExpiracion;
    private boolean activo;

    public CuponDescuento(String codigo, double porcentaje, LocalDate fechaExpiracion, boolean activo) {
        this.codigo = codigo;
        this.porcentaje = porcentaje;
        this.fechaExpiracion = fechaExpiracion;
        this.activo = activo;
    }

    public String getCodigo() {
        return codigo;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public LocalDate getFechaExpiracion() {
        return fechaExpiracion;
    }

    public boolean isActivo() {
        return activo;
    }

    public boolean isValido() {
        return activo && (fechaExpiracion == null || !LocalDate.now().isAfter(fechaExpiracion));
    }

    public double aplicarSobre(double monto) {
        if (!isValido()) return monto;
        return monto * (1 - porcentaje / 100.0);
    }

    @Override
    public String toString() {
        return "CuponDescuento{" +
                "codigo='" + codigo + '\'' +
                ", porcentaje=" + porcentaje +
                ", fechaExpiracion=" + fechaExpiracion +
                ", activo=" + activo +
                '}';
    }
}
