package pe.edu.utp.eventope.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Datos de entrada_digital. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class EntradaDigital implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String codigoEntrada;
    private int eventoId;
    private int categoriaId;
    private int compradorId;
    private String estadoUso;
    private LocalDateTime fechaEmision;

    public EntradaDigital() {}

    public EntradaDigital(int id, String codigoEntrada, int eventoId, int categoriaId, int compradorId, String estadoUso, LocalDateTime fechaEmision) {
        this.id = id;
        this.codigoEntrada = codigoEntrada;
        this.eventoId = eventoId;
        this.categoriaId = categoriaId;
        this.compradorId = compradorId;
        this.estadoUso = estadoUso;
        this.fechaEmision = fechaEmision;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigoEntrada() { return codigoEntrada; }
    public void setCodigoEntrada(String codigoEntrada) { this.codigoEntrada = codigoEntrada; }

    public int getEventoId() { return eventoId; }
    public void setEventoId(int eventoId) { this.eventoId = eventoId; }

    public int getCategoriaId() { return categoriaId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }

    public int getCompradorId() { return compradorId; }
    public void setCompradorId(int compradorId) { this.compradorId = compradorId; }

    public String getEstadoUso() { return estadoUso; }
    public void setEstadoUso(String estadoUso) { this.estadoUso = estadoUso; }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }

    public boolean estaDisponible() { return "DISPONIBLE".equals(estadoUso); }
    public void marcarUtilizada() { if (!estaDisponible()) throw new IllegalStateException("Entrada no disponible"); estadoUso = "UTILIZADA"; }
    public void anular() { if (!estaDisponible()) throw new IllegalStateException("Entrada no disponible"); estadoUso = "ANULADA"; }
}
