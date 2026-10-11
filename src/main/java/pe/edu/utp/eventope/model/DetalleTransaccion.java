package pe.edu.utp.eventope.model;

import java.io.Serializable;
import java.math.BigDecimal;

/** Datos de detalle_transaccion. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class DetalleTransaccion implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int transaccionId;
    private int entradaId;
    private BigDecimal precioAplicado;
    private BigDecimal descuentoAplicado;

    public DetalleTransaccion() {}

    public DetalleTransaccion(int id, int transaccionId, int entradaId, BigDecimal precioAplicado, BigDecimal descuentoAplicado) {
        this.id = id;
        this.transaccionId = transaccionId;
        this.entradaId = entradaId;
        this.precioAplicado = precioAplicado;
        this.descuentoAplicado = descuentoAplicado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTransaccionId() { return transaccionId; }
    public void setTransaccionId(int transaccionId) { this.transaccionId = transaccionId; }

    public int getEntradaId() { return entradaId; }
    public void setEntradaId(int entradaId) { this.entradaId = entradaId; }

    public BigDecimal getPrecioAplicado() { return precioAplicado; }
    public void setPrecioAplicado(BigDecimal precioAplicado) { this.precioAplicado = precioAplicado; }

    public BigDecimal getDescuentoAplicado() { return descuentoAplicado; }
    public void setDescuentoAplicado(BigDecimal descuentoAplicado) { this.descuentoAplicado = descuentoAplicado; }

    public BigDecimal obtenerImporteNeto() {
        if (precioAplicado == null || descuentoAplicado == null) throw new IllegalStateException("Importes incompletos");
        if (precioAplicado.signum() < 0 || descuentoAplicado.signum() < 0 || descuentoAplicado.compareTo(precioAplicado) > 0) throw new IllegalStateException("Importes invalidos");
        return precioAplicado.subtract(descuentoAplicado);
    }
}
