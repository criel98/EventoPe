package pe.edu.utp.eventope.model;

import java.io.Serializable;

/** Datos de local_concierto. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class LocalConcierto implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String nombreLocal;
    private String direccion;
    private int capacidadMax;
    private String ciudad;

    public LocalConcierto() {}

    public LocalConcierto(int id, String nombreLocal, String direccion, int capacidadMax, String ciudad) {
        this.id = id;
        this.nombreLocal = nombreLocal;
        this.direccion = direccion;
        this.capacidadMax = capacidadMax;
        this.ciudad = ciudad;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombreLocal() { return nombreLocal; }
    public void setNombreLocal(String nombreLocal) { this.nombreLocal = nombreLocal; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public int getCapacidadMax() { return capacidadMax; }
    public void setCapacidadMax(int capacidadMax) { this.capacidadMax = capacidadMax; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }


}
