package pe.edu.utp.eventope.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "control_acceso_puerta")
public class ControlAccesoPuerta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nombre_puerta")
    private String nombrePuerta;

    private String ubicacion;

    public ControlAccesoPuerta() {
    }

    public ControlAccesoPuerta(int id, String nombrePuerta, String ubicacion) {
        this.id = id;
        this.nombrePuerta = nombrePuerta;
        this.ubicacion = ubicacion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombrePuerta() {
        return nombrePuerta;
    }

    public void setNombrePuerta(String nombrePuerta) {
        this.nombrePuerta = nombrePuerta;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
