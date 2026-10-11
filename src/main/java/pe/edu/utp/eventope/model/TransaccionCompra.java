package pe.edu.utp.eventope.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Datos de transaccion_compra. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class TransaccionCompra implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String codigoUnico;
    private String solicitudHash;
    private int clienteId;
    private int eventoId;
    private Integer cuponId;
    private LocalDateTime fechaHora;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal igv;
    private BigDecimal total;
    private String estado;
    private BigDecimal importeReembolso;
    private LocalDateTime fechaReembolso;

    public TransaccionCompra() {}

    public TransaccionCompra(int id, String codigoUnico, String solicitudHash, int clienteId, int eventoId, Integer cuponId, LocalDateTime fechaHora, BigDecimal subtotal, BigDecimal descuento, BigDecimal igv, BigDecimal total, String estado, BigDecimal importeReembolso, LocalDateTime fechaReembolso) {
        this.id = id;
        this.codigoUnico = codigoUnico;
        this.solicitudHash = solicitudHash;
        this.clienteId = clienteId;
        this.eventoId = eventoId;
        this.cuponId = cuponId;
        this.fechaHora = fechaHora;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.igv = igv;
        this.total = total;
        this.estado = estado;
        this.importeReembolso = importeReembolso;
        this.fechaReembolso = fechaReembolso;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigoUnico() { return codigoUnico; }
    public void setCodigoUnico(String codigoUnico) { this.codigoUnico = codigoUnico; }

    public String getSolicitudHash() { return solicitudHash; }
    public void setSolicitudHash(String solicitudHash) { this.solicitudHash = solicitudHash; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public int getEventoId() { return eventoId; }
    public void setEventoId(int eventoId) { this.eventoId = eventoId; }

    public Integer getCuponId() { return cuponId; }
    public void setCuponId(Integer cuponId) { this.cuponId = cuponId; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }

    public BigDecimal getIgv() { return igv; }
    public void setIgv(BigDecimal igv) { this.igv = igv; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getImporteReembolso() { return importeReembolso; }
    public void setImporteReembolso(BigDecimal importeReembolso) { this.importeReembolso = importeReembolso; }

    public LocalDateTime getFechaReembolso() { return fechaReembolso; }
    public void setFechaReembolso(LocalDateTime fechaReembolso) { this.fechaReembolso = fechaReembolso; }


}
