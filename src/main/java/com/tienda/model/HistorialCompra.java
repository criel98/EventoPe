package com.tienda.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class HistorialCompra {
    private String id;
    private String clienteId;
    private int totalBoletosComprados;
    private LocalDateTime ultimaCompraFecha;

    public HistorialCompra() {
        this.id = UUID.randomUUID().toString();
        this.ultimaCompraFecha = LocalDateTime.now();
    }

    public HistorialCompra(String clienteId, int totalBoletosComprados) {
        this();
        this.clienteId = clienteId;
        this.totalBoletosComprados = totalBoletosComprados;
    }

    public String getId() {
        return id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public int getTotalBoletosComprados() {
        return totalBoletosComprados;
    }

    public void setTotalBoletosComprados(int totalBoletosComprados) {
        this.totalBoletosComprados = totalBoletosComprados;
    }

    public LocalDateTime getUltimaCompraFecha() {
        return ultimaCompraFecha;
    }

    public void actualizarFechaCompra() {
        this.ultimaCompraFecha = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "HistorialCompra{" +
                "id='" + id + '\'' +
                ", clienteId='" + clienteId + '\'' +
                ", totalBoletosComprados=" + totalBoletosComprados +
                ", ultimaCompraFecha=" + ultimaCompraFecha +
                '}';
    }
}
