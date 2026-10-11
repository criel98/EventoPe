package pe.edu.utp.eventope.model;

import java.io.Serializable;
import java.math.BigDecimal;

/** Datos de categoria_asiento. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class CategoriaAsiento implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int eventoId;
    private String nombreZona;
    private BigDecimal precioBase;
    private int aforoTotal;
    private int aforoDisponible;

    public CategoriaAsiento() {}

    public CategoriaAsiento(int id, int eventoId, String nombreZona, BigDecimal precioBase, int aforoTotal, int aforoDisponible) {
        this.id = id;
        this.eventoId = eventoId;
        this.nombreZona = nombreZona;
        this.precioBase = precioBase;
        this.aforoTotal = aforoTotal;
        this.aforoDisponible = aforoDisponible;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEventoId() { return eventoId; }
    public void setEventoId(int eventoId) { this.eventoId = eventoId; }

    public String getNombreZona() { return nombreZona; }
    public void setNombreZona(String nombreZona) { this.nombreZona = nombreZona; }

    public BigDecimal getPrecioBase() { return precioBase; }
    public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }

    public int getAforoTotal() { return aforoTotal; }
    public void setAforoTotal(int aforoTotal) { this.aforoTotal = aforoTotal; }

    public int getAforoDisponible() { return aforoDisponible; }
    public void setAforoDisponible(int aforoDisponible) { this.aforoDisponible = aforoDisponible; }

    public boolean hayCupo(int cantidad) { return cantidad > 0 && cantidad <= aforoDisponible; }
}
