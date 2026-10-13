package pe.edu.utp.eventope.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class BoletoDetalleDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String codigoEntrada;
    private final int eventoId;
    private final String eventoNombre;
    private final LocalDateTime eventoFecha;
    private final String localNombre;
    private final String localDireccion;
    private final String ciudad;
    private final String zona;
    private final BigDecimal precio;
    private final String estadoUso;
    private final String asistenteNombre;
    private final String asistenteApellidos;
    private final String asistenteDniEnmascarado;
    private final String comprobante;

    public BoletoDetalleDTO(int id, String codigoEntrada, int eventoId, String eventoNombre, LocalDateTime eventoFecha,
                            String localNombre, String localDireccion, String ciudad, String zona, BigDecimal precio,
                            String estadoUso, String asistenteNombre, String asistenteApellidos, String asistenteDni,
                            String comprobante) {
        this.id = id;
        this.codigoEntrada = codigoEntrada;
        this.eventoId = eventoId;
        this.eventoNombre = eventoNombre;
        this.eventoFecha = eventoFecha;
        this.localNombre = localNombre;
        this.localDireccion = localDireccion;
        this.ciudad = ciudad;
        this.zona = zona;
        this.precio = precio;
        this.estadoUso = estadoUso;
        this.asistenteNombre = asistenteNombre == null ? "" : asistenteNombre;
        this.asistenteApellidos = asistenteApellidos == null ? "" : asistenteApellidos;
        this.asistenteDniEnmascarado = enmascararDni(asistenteDni);
        this.comprobante = comprobante == null ? "DEMO-000000" : comprobante;
    }

    private static String enmascararDni(String dni) {
        if (dni == null || dni.length() < 4) return "****";
        return "****" + dni.substring(dni.length() - 4);
    }

    public int getId() { return id; }
    public String getCodigoEntrada() { return codigoEntrada; }
    public int getEventoId() { return eventoId; }
    public String getEventoNombre() { return eventoNombre; }
    public LocalDateTime getEventoFecha() { return eventoFecha; }
    public String getLocalNombre() { return localNombre; }
    public String getLocalDireccion() { return localDireccion; }
    public String getCiudad() { return ciudad; }
    public String getZona() { return zona; }
    public BigDecimal getPrecio() { return precio; }
    public String getEstadoUso() { return estadoUso; }
    public String getAsistenteNombre() { return asistenteNombre; }
    public String getAsistenteApellidos() { return asistenteApellidos; }
    public String getAsistenteNombreCompleto() { return (asistenteNombre + " " + asistenteApellidos).trim(); }
    public String getAsistenteDniEnmascarado() { return asistenteDniEnmascarado; }
    public String getComprobante() { return comprobante; }
}
