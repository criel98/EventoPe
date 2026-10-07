package pe.edu.utp.eventope.model;

import java.io.Serializable;

/** Datos de dispositivo_validacion. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class DispositivoValidacion implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int operadorId;
    private int eventoId;
    private String nombrePuerta;
    private String modeloCelular;
    private String tokenAccesoHash;
    private String estadoDispositivo;

    public DispositivoValidacion() {}

    public DispositivoValidacion(int id, int operadorId, int eventoId, String nombrePuerta, String modeloCelular, String tokenAccesoHash, String estadoDispositivo) {
        this.id = id;
        this.operadorId = operadorId;
        this.eventoId = eventoId;
        this.nombrePuerta = nombrePuerta;
        this.modeloCelular = modeloCelular;
        this.tokenAccesoHash = tokenAccesoHash;
        this.estadoDispositivo = estadoDispositivo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOperadorId() { return operadorId; }
    public void setOperadorId(int operadorId) { this.operadorId = operadorId; }

    public int getEventoId() { return eventoId; }
    public void setEventoId(int eventoId) { this.eventoId = eventoId; }

    public String getNombrePuerta() { return nombrePuerta; }
    public void setNombrePuerta(String nombrePuerta) { this.nombrePuerta = nombrePuerta; }

    public String getModeloCelular() { return modeloCelular; }
    public void setModeloCelular(String modeloCelular) { this.modeloCelular = modeloCelular; }

    public String getTokenAccesoHash() { return tokenAccesoHash; }
    public void setTokenAccesoHash(String tokenAccesoHash) { this.tokenAccesoHash = tokenAccesoHash; }

    public String getEstadoDispositivo() { return estadoDispositivo; }
    public void setEstadoDispositivo(String estadoDispositivo) { this.estadoDispositivo = estadoDispositivo; }


}
