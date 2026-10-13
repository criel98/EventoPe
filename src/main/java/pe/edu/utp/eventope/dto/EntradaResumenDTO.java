package pe.edu.utp.eventope.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class EntradaResumenDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String codigoEntrada;
    private final String eventoNombre;
    private final LocalDateTime eventoFecha;
    private final String localNombre;
    private final String ciudad;
    private final String zona;
    private final BigDecimal precio;
    private final String estadoUso;
    private final String asistenteNombre;
    private final String asistenteDniEnmascarado;

    public EntradaResumenDTO(int id, String codigoEntrada, String eventoNombre, LocalDateTime eventoFecha,
                             String localNombre, String ciudad, String zona, BigDecimal precio,
                             String estadoUso, String asistenteNombre, String asistenteDni) {
        this.id = id;
        this.codigoEntrada = codigoEntrada;
        this.eventoNombre = eventoNombre;
        this.eventoFecha = eventoFecha;
        this.localNombre = localNombre;
        this.ciudad = ciudad;
        this.zona = zona;
        this.precio = precio;
        this.estadoUso = estadoUso;
        this.asistenteNombre = asistenteNombre == null ? "Sin asignar" : asistenteNombre;
        this.asistenteDniEnmascarado = enmascararDni(asistenteDni);
    }

    private static String enmascararDni(String dni) {
        if (dni == null || dni.length() < 4) return "****";
        return "****" + dni.substring(dni.length() - 4);
    }

    public int getId() { return id; }
    public String getCodigoEntrada() { return codigoEntrada; }
    public String getEventoNombre() { return eventoNombre; }
    public LocalDateTime getEventoFecha() { return eventoFecha; }
    public String getLocalNombre() { return localNombre; }
    public String getCiudad() { return ciudad; }
    public String getZona() { return zona; }
    public BigDecimal getPrecio() { return precio; }
    public String getEstadoUso() { return estadoUso; }
    public String getAsistenteNombre() { return asistenteNombre; }
    public String getAsistenteDniEnmascarado() { return asistenteDniEnmascarado; }
}
