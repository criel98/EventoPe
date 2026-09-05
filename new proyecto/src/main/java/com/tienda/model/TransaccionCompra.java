package com.tienda.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransaccionCompra {
    private String id;
    private List<DetalleTransaccion> detalles = new ArrayList<>();
    private CuponDescuento cupon;
    private ComprobantePago comprobante;

    public TransaccionCompra() {
        this.id = UUID.randomUUID().toString();
    }

    public TransaccionCompra(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<DetalleTransaccion> getDetalles() {
        return detalles;
    }

    public void agregarDetalle(DetalleTransaccion detalle) {
        detalles.add(detalle);
    }

    public double calcularTotalAntesDescuento() {
        return detalles.stream().mapToDouble(DetalleTransaccion::getSubtotal).sum();
    }

    public double calcularTotal() {
        double total = calcularTotalAntesDescuento();
        if (cupon != null && cupon.isValido()) {
            total = cupon.aplicarSobre(total);
        }
        return total;
    }

    public void aplicarCupon(CuponDescuento cupon) {
        if (cupon != null && cupon.isValido()) this.cupon = cupon;
    }

    public CuponDescuento getCupon() {
        return cupon;
    }

    public ComprobantePago getComprobante() {
        return comprobante;
    }

    public void generarComprobante(MetodoPago metodo) {
        double total = calcularTotal();
        this.comprobante = new ComprobantePago(UUID.randomUUID().toString(), java.time.LocalDateTime.now(), total, metodo);
    }

    @Override
    public String toString() {
        return "TransaccionCompra{" +
                "id='" + id + '\'' +
                ", detalles=" + detalles +
                ", cupon=" + cupon +
                ", comprobante=" + comprobante +
                '}';
    }
}
