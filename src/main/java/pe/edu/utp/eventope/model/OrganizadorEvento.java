package pe.edu.utp.eventope.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "organizador_evento")
public class OrganizadorEvento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nombre_empresa")
    private String nombreEmpresa;

    private String contacto;

    public OrganizadorEvento() {
    }

    public OrganizadorEvento(int id, String nombreEmpresa, String contacto) {
        this.id = id;
        this.nombreEmpresa = nombreEmpresa;
        this.contacto = contacto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }
}
