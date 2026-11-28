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

    public void validar() {
        if (eventoId <= 0 || nombreZona == null || nombreZona.isBlank()
                || precioBase == null || precioBase.signum() < 0 || aforoTotal <= 0
                || aforoDisponible < 0 || aforoDisponible > aforoTotal) {
            throw new IllegalArgumentException("La zona requiere nombre, precio no negativo y aforo válido.");
        }
    }

    public void validarReduccionCapacidad(int nuevoAforo) {
        validarReduccionCapacidad(nuevoAforo, 0);
    }

    public void validarReduccionCapacidad(int nuevoAforo, int entradasComprometidas) {
        int comprometidas = Math.max(aforoTotal - aforoDisponible, entradasComprometidas);
        if (nuevoAforo <= 0 || entradasComprometidas < 0 || nuevoAforo < comprometidas) {
            throw new IllegalArgumentException("La capacidad no puede ser menor que las entradas ya comprometidas.");
        }
    }
}
