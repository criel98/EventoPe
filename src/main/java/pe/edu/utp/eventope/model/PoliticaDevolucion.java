package pe.edu.utp.eventope.model;

import java.io.Serializable;
import java.math.BigDecimal;

/** Datos de politica_devolucion. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class PoliticaDevolucion implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int eventoId;
    private int diasMinimosPrevios;
    private BigDecimal penalizacionPorcentaje;
    private boolean estadoActivo;

    public PoliticaDevolucion() {}

    public PoliticaDevolucion(int id, int eventoId, int diasMinimosPrevios, BigDecimal penalizacionPorcentaje, boolean estadoActivo) {
        this.id = id;
        this.eventoId = eventoId;
        this.diasMinimosPrevios = diasMinimosPrevios;
        this.penalizacionPorcentaje = penalizacionPorcentaje;
        this.estadoActivo = estadoActivo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEventoId() { return eventoId; }
    public void setEventoId(int eventoId) { this.eventoId = eventoId; }

    public int getDiasMinimosPrevios() { return diasMinimosPrevios; }
    public void setDiasMinimosPrevios(int diasMinimosPrevios) { this.diasMinimosPrevios = diasMinimosPrevios; }

    public BigDecimal getPenalizacionPorcentaje() { return penalizacionPorcentaje; }
    public void setPenalizacionPorcentaje(BigDecimal penalizacionPorcentaje) { this.penalizacionPorcentaje = penalizacionPorcentaje; }

    public boolean isEstadoActivo() { return estadoActivo; }
    public void setEstadoActivo(boolean estadoActivo) { this.estadoActivo = estadoActivo; }


}
