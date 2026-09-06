package pe.edu.utp.eventope.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "politica_devolucion")
public class PoliticaDevolucion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "evento_id")
    private int eventoId;

    @Column(name = "dias_maximos_previos")
    private int diasMaximosPrevios;

    @Column(name = "penalizacion_porcentaje")
    private BigDecimal penalizacionPorcentaje;

    @Column(name = "estado_activo")
    private boolean estadoActivo;

    public PoliticaDevolucion() {
    }

    public PoliticaDevolucion(int id, int eventoId, int diasMaximosPrevios, BigDecimal penalizacionPorcentaje, boolean estadoActivo) {
        this.id = id;
        this.eventoId = eventoId;
        this.diasMaximosPrevios = diasMaximosPrevios;
        this.penalizacionPorcentaje = penalizacionPorcentaje;
        this.estadoActivo = estadoActivo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventoId() {
        return eventoId;
    }

    public void setEventoId(int eventoId) {
        this.eventoId = eventoId;
    }

    public int getDiasMaximosPrevios() {
        return diasMaximosPrevios;
    }

    public void setDiasMaximosPrevios(int diasMaximosPrevios) {
        this.diasMaximosPrevios = diasMaximosPrevios;
    }

    public BigDecimal getPenalizacionPorcentaje() {
        return penalizacionPorcentaje;
    }

    public void setPenalizacionPorcentaje(BigDecimal penalizacionPorcentaje) {
        this.penalizacionPorcentaje = penalizacionPorcentaje;
    }

    public boolean isEstadoActivo() {
        return estadoActivo;
    }

    public void setEstadoActivo(boolean estadoActivo) {
        this.estadoActivo = estadoActivo;
    }
}
