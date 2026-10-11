package pe.edu.utp.eventope.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Datos de historial_rotacion_qr. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class HistorialRotacionQR implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int qrId;
    private long intervalo;
    private LocalDateTime horaGeneracion;
    private String hashAnterior;
    private String hashNuevo;

    public HistorialRotacionQR() {}

    public HistorialRotacionQR(int id, int qrId, long intervalo, LocalDateTime horaGeneracion, String hashAnterior, String hashNuevo) {
        this.id = id;
        this.qrId = qrId;
        this.intervalo = intervalo;
        this.horaGeneracion = horaGeneracion;
        this.hashAnterior = hashAnterior;
        this.hashNuevo = hashNuevo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQrId() { return qrId; }
    public void setQrId(int qrId) { this.qrId = qrId; }

    public long getIntervalo() { return intervalo; }
    public void setIntervalo(long intervalo) { this.intervalo = intervalo; }

    public LocalDateTime getHoraGeneracion() { return horaGeneracion; }
    public void setHoraGeneracion(LocalDateTime horaGeneracion) { this.horaGeneracion = horaGeneracion; }

    public String getHashAnterior() { return hashAnterior; }
    public void setHashAnterior(String hashAnterior) { this.hashAnterior = hashAnterior; }

    public String getHashNuevo() { return hashNuevo; }
    public void setHashNuevo(String hashNuevo) { this.hashNuevo = hashNuevo; }


}
