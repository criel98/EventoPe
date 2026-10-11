package pe.edu.utp.eventope.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Datos de comprobante_pago. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class ComprobantePago implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int transaccionId;
    private String serieCorrelativo;
    private String tipoComprobante;
    private LocalDateTime fechaEmision;

    public ComprobantePago() {}

    public ComprobantePago(int id, int transaccionId, String serieCorrelativo, String tipoComprobante, LocalDateTime fechaEmision) {
        this.id = id;
        this.transaccionId = transaccionId;
        this.serieCorrelativo = serieCorrelativo;
        this.tipoComprobante = tipoComprobante;
        this.fechaEmision = fechaEmision;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTransaccionId() { return transaccionId; }
    public void setTransaccionId(int transaccionId) { this.transaccionId = transaccionId; }

    public String getSerieCorrelativo() { return serieCorrelativo; }
    public void setSerieCorrelativo(String serieCorrelativo) { this.serieCorrelativo = serieCorrelativo; }

    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }


}
