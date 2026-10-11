package pe.edu.utp.eventope.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Datos de ticket_qr_dinamico. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class TicketQRDinamico implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int entradaId;
    private String claveTotpCifrada;
    private Long ultimoIntervalo;
    private LocalDateTime fechaGeneracion;
    private LocalDateTime fechaExpiracion;

    public TicketQRDinamico() {}

    public TicketQRDinamico(int id, int entradaId, String claveTotpCifrada, Long ultimoIntervalo, LocalDateTime fechaGeneracion, LocalDateTime fechaExpiracion) {
        this.id = id;
        this.entradaId = entradaId;
        this.claveTotpCifrada = claveTotpCifrada;
        this.ultimoIntervalo = ultimoIntervalo;
        this.fechaGeneracion = fechaGeneracion;
        this.fechaExpiracion = fechaExpiracion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEntradaId() { return entradaId; }
    public void setEntradaId(int entradaId) { this.entradaId = entradaId; }

    public String getClaveTotpCifrada() { return claveTotpCifrada; }
    public void setClaveTotpCifrada(String claveTotpCifrada) { this.claveTotpCifrada = claveTotpCifrada; }

    public Long getUltimoIntervalo() { return ultimoIntervalo; }
    public void setUltimoIntervalo(Long ultimoIntervalo) { this.ultimoIntervalo = ultimoIntervalo; }

    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }

    public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }


}
