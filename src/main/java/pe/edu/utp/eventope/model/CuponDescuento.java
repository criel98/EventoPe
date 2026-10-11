package pe.edu.utp.eventope.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Datos de cupon_descuento. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class CuponDescuento implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String codigoCupon;
    private BigDecimal porcentajeDescuento;
    private LocalDateTime fechaExpiracion;
    private String estado;

    public CuponDescuento() {}

    public CuponDescuento(int id, String codigoCupon, BigDecimal porcentajeDescuento, LocalDateTime fechaExpiracion, String estado) {
        this.id = id;
        this.codigoCupon = codigoCupon;
        this.porcentajeDescuento = porcentajeDescuento;
        this.fechaExpiracion = fechaExpiracion;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigoCupon() { return codigoCupon; }
    public void setCodigoCupon(String codigoCupon) { this.codigoCupon = codigoCupon; }

    public BigDecimal getPorcentajeDescuento() { return porcentajeDescuento; }
    public void setPorcentajeDescuento(BigDecimal porcentajeDescuento) { this.porcentajeDescuento = porcentajeDescuento; }

    public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }


}
