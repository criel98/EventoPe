package com.tienda.model;

public class DetalleTransaccion {
    private String productoId;
    private String nombre;
    private int cantidad;
    private double precioUnitario;

    public DetalleTransaccion(String productoId, String nombre, int cantidad, double precioUnitario) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public String getProductoId() {
        return productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public double getSubtotal() {
        return cantidad * precioUnitario;
    }

    @Override
    public String toString() {
        return "DetalleTransaccion{" +
                "productoId='" + productoId + '\'' +
                ", nombre='" + nombre + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                '}';
    }
}
