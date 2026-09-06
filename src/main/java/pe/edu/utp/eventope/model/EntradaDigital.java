package pe.edu.utp.eventope.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "entrada_digital")
public class EntradaDigital implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "codigo_entrada")
    private String codigoEntrada;

    @Column(name = "evento_id")
    private int eventoId;

    @Column(name = "comprador_id")
    private int compradorId;

    @Column(name = "estado_uso")
    private String estadoUso;

    public EntradaDigital() {
    }

    public EntradaDigital(int id, String codigoEntrada, int eventoId, int compradorId, String estadoUso) {
        this.id = id;
        this.codigoEntrada = codigoEntrada;
        this.eventoId = eventoId;
        this.compradorId = compradorId;
        this.estadoUso = estadoUso;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoEntrada() {
        return codigoEntrada;
    }

    public void setCodigoEntrada(String codigoEntrada) {
        this.codigoEntrada = codigoEntrada;
    }

    public int getEventoId() {
        return eventoId;
    }

    public void setEventoId(int eventoId) {
        this.eventoId = eventoId;
    }

    public int getCompradorId() {
        return compradorId;
    }

    public void setCompradorId(int compradorId) {
        this.compradorId = compradorId;
    }

    public String getEstadoUso() {
        return estadoUso;
    }

    public void setEstadoUso(String estadoUso) {
        this.estadoUso = estadoUso;
    }
}
