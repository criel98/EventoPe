package pe.edu.utp.eventope.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Datos de historial_compra. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class HistorialCompra implements Serializable {
    private static final long serialVersionUID = 1L;
    private int clienteId;
    private long totalBoletosComprados;
    private LocalDateTime ultimaCompraFecha;

    public HistorialCompra() {}

    public HistorialCompra(int clienteId, long totalBoletosComprados, LocalDateTime ultimaCompraFecha) {
        this.clienteId = clienteId;
        this.totalBoletosComprados = totalBoletosComprados;
        this.ultimaCompraFecha = ultimaCompraFecha;
    }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public long getTotalBoletosComprados() { return totalBoletosComprados; }
    public void setTotalBoletosComprados(long totalBoletosComprados) { this.totalBoletosComprados = totalBoletosComprados; }

    public LocalDateTime getUltimaCompraFecha() { return ultimaCompraFecha; }
    public void setUltimaCompraFecha(LocalDateTime ultimaCompraFecha) { this.ultimaCompraFecha = ultimaCompraFecha; }


}
