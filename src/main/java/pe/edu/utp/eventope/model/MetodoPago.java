package pe.edu.utp.eventope.model;

import java.io.Serializable;

/** Datos de metodo_pago. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class MetodoPago implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int transaccionId;
    private String nombreMetodo;
    private String nroOperacion;
    private String estadoTransaccion;

    public MetodoPago() {}

    public MetodoPago(int id, int transaccionId, String nombreMetodo, String nroOperacion, String estadoTransaccion) {
        this.id = id;
        this.transaccionId = transaccionId;
        this.nombreMetodo = nombreMetodo;
        this.nroOperacion = nroOperacion;
        this.estadoTransaccion = estadoTransaccion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTransaccionId() { return transaccionId; }
    public void setTransaccionId(int transaccionId) { this.transaccionId = transaccionId; }

    public String getNombreMetodo() { return nombreMetodo; }
    public void setNombreMetodo(String nombreMetodo) { this.nombreMetodo = nombreMetodo; }

    public String getNroOperacion() { return nroOperacion; }
    public void setNroOperacion(String nroOperacion) { this.nroOperacion = nroOperacion; }

    public String getEstadoTransaccion() { return estadoTransaccion; }
    public void setEstadoTransaccion(String estadoTransaccion) { this.estadoTransaccion = estadoTransaccion; }


}
