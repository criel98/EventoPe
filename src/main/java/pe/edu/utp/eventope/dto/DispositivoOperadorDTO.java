package pe.edu.utp.eventope.dto;

import java.io.Serializable;

public final class DispositivoOperadorDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int dispositivoId;
    private final int operadorId;
    private final int eventoId;
    private final String eventoNombre;
    private final String nombrePuerta;
    private final String modeloCelular;

    public DispositivoOperadorDTO(int dispositivoId, int operadorId, int eventoId,
                                  String eventoNombre, String nombrePuerta, String modeloCelular) {
        this.dispositivoId = dispositivoId;
        this.operadorId = operadorId;
        this.eventoId = eventoId;
        this.eventoNombre = eventoNombre;
        this.nombrePuerta = nombrePuerta;
        this.modeloCelular = modeloCelular;
    }

    public int getDispositivoId() { return dispositivoId; }
    public int getOperadorId() { return operadorId; }
    public int getEventoId() { return eventoId; }
    public String getEventoNombre() { return eventoNombre; }
    public String getNombrePuerta() { return nombrePuerta; }
    public String getModeloCelular() { return modeloCelular; }
}
