package pe.edu.utp.eventope.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Datos de control_acceso_puerta. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class ControlAccesoPuerta implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String solicitudId;
    private String solicitudHash;
    private int eventoId;
    private int dispositivoId;
    private int operadorId;
    private Integer entradaId;
    private LocalDateTime horaIngreso;
    private String estadoAcceso;
    private String motivo;
    private Integer autorizadoPorId;

    public ControlAccesoPuerta() {}

    public ControlAccesoPuerta(int id, String solicitudId, String solicitudHash, int eventoId, int dispositivoId, int operadorId, Integer entradaId, LocalDateTime horaIngreso, String estadoAcceso, String motivo, Integer autorizadoPorId) {
        this.id = id;
        this.solicitudId = solicitudId;
        this.solicitudHash = solicitudHash;
        this.eventoId = eventoId;
        this.dispositivoId = dispositivoId;
        this.operadorId = operadorId;
        this.entradaId = entradaId;
        this.horaIngreso = horaIngreso;
        this.estadoAcceso = estadoAcceso;
        this.motivo = motivo;
        this.autorizadoPorId = autorizadoPorId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSolicitudId() { return solicitudId; }
    public void setSolicitudId(String solicitudId) { this.solicitudId = solicitudId; }

    public String getSolicitudHash() { return solicitudHash; }
    public void setSolicitudHash(String solicitudHash) { this.solicitudHash = solicitudHash; }

    public int getEventoId() { return eventoId; }
    public void setEventoId(int eventoId) { this.eventoId = eventoId; }

    public int getDispositivoId() { return dispositivoId; }
    public void setDispositivoId(int dispositivoId) { this.dispositivoId = dispositivoId; }

    public int getOperadorId() { return operadorId; }
    public void setOperadorId(int operadorId) { this.operadorId = operadorId; }

    public Integer getEntradaId() { return entradaId; }
    public void setEntradaId(Integer entradaId) { this.entradaId = entradaId; }

    public LocalDateTime getHoraIngreso() { return horaIngreso; }
    public void setHoraIngreso(LocalDateTime horaIngreso) { this.horaIngreso = horaIngreso; }

    public String getEstadoAcceso() { return estadoAcceso; }
    public void setEstadoAcceso(String estadoAcceso) { this.estadoAcceso = estadoAcceso; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Integer getAutorizadoPorId() { return autorizadoPorId; }
    public void setAutorizadoPorId(Integer autorizadoPorId) { this.autorizadoPorId = autorizadoPorId; }


}
