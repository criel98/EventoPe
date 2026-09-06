package pe.edu.utp.eventope.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_qr_dinamico")
public class TicketQR_Dinamico implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "entrada_id")
    private int entradaId;

    @Column(name = "token_encriptado")
    private String tokenEncriptado;

    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;

    @Column(name = "iteracion_clave")
    private int iteracionClave;

    public TicketQR_Dinamico() {
    }

    public TicketQR_Dinamico(int id, int entradaId, String tokenEncriptado, LocalDateTime fechaExpiracion, int iteracionClave) {
        this.id = id;
        this.entradaId = entradaId;
        this.tokenEncriptado = tokenEncriptado;
        this.fechaExpiracion = fechaExpiracion;
        this.iteracionClave = iteracionClave;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEntradaId() {
        return entradaId;
    }

    public void setEntradaId(int entradaId) {
        this.entradaId = entradaId;
    }

    public String getTokenEncriptado() {
        return tokenEncriptado;
    }

    public void setTokenEncriptado(String tokenEncriptado) {
        this.tokenEncriptado = tokenEncriptado;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public int getIteracionClave() {
        return iteracionClave;
    }

    public void setIteracionClave(int iteracionClave) {
        this.iteracionClave = iteracionClave;
    }
}
