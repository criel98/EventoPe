package pe.edu.utp.eventope.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_rotacion_qr")
public class HistorialRotacionQR implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "qr_id")
    private int qrId;

    @Column(name = "hora_generacion")
    private LocalDateTime horaGeneracion;

    @Column(name = "hash_anterior")
    private String hashAnterior;

    @Column(name = "hash_nuevo")
    private String hashNuevo;

    public HistorialRotacionQR() {
    }

    public HistorialRotacionQR(int id, int qrId, LocalDateTime horaGeneracion, String hashAnterior, String hashNuevo) {
        this.id = id;
        this.qrId = qrId;
        this.horaGeneracion = horaGeneracion;
        this.hashAnterior = hashAnterior;
        this.hashNuevo = hashNuevo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQrId() {
        return qrId;
    }

    public void setQrId(int qrId) {
        this.qrId = qrId;
    }

    public LocalDateTime getHoraGeneracion() {
        return horaGeneracion;
    }

    public void setHoraGeneracion(LocalDateTime horaGeneracion) {
        this.horaGeneracion = horaGeneracion;
    }

    public String getHashAnterior() {
        return hashAnterior;
    }

    public void setHashAnterior(String hashAnterior) {
        this.hashAnterior = hashAnterior;
    }

    public String getHashNuevo() {
        return hashNuevo;
    }

    public void setHashNuevo(String hashNuevo) {
        this.hashNuevo = hashNuevo;
    }
}
