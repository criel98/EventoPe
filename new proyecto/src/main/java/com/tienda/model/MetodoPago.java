package com.tienda.model;

public class MetodoPago {
    private String tipo; // e.g., Tarjeta, Efectivo, PayPal
    private String referencia; // e.g., último 4 dígitos, id de transacción
    private boolean confirmado;

    public MetodoPago(String tipo, String referencia) {
        this.tipo = tipo;
        this.referencia = referencia;
        this.confirmado = false;
    }

    public String getTipo() {
        return tipo;
    }

    public String getReferencia() {
        return referencia;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
    }

    @Override
    public String toString() {
        return "MetodoPago{" +
                "tipo='" + tipo + '\'' +
                ", referencia='" + referencia + '\'' +
                ", confirmado=" + confirmado +
                '}';
    }
}
