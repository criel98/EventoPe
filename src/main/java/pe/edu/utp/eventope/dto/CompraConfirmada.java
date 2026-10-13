package pe.edu.utp.eventope.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public final class CompraConfirmada implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String codigo;
    private final String comprobante;
    private final String evento;
    private final BigDecimal total;
    private final int cantidad;

    public CompraConfirmada(int id, String codigo, String comprobante, String evento, BigDecimal total, int cantidad) {
        this.id = id;
        this.codigo = codigo;
        this.comprobante = comprobante;
        this.evento = evento;
        this.total = total;
        this.cantidad = cantidad;
    }

    public int getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getComprobante() { return comprobante; }
    public String getEvento() { return evento; }
    public BigDecimal getTotal() { return total; }
    public int getCantidad() { return cantidad; }
}
