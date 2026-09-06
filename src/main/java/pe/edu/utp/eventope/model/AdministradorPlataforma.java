package pe.edu.utp.eventope.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "administrador_plataforma")
public class AdministradorPlataforma implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    private String username;

    @Column(name = "nivel_acceso")
    private String nivelAcceso;

    @Column(name = "area_soporte")
    private String areaSoporte;

    public AdministradorPlataforma() {
    }

    public AdministradorPlataforma(int id, String username, String nivelAcceso, String areaSoporte) {
        this.id = id;
        this.username = username;
        this.nivelAcceso = nivelAcceso;
        this.areaSoporte = areaSoporte;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNivelAcceso() {
        return nivelAcceso;
    }

    public void setNivelAcceso(String nivelAcceso) {
        this.nivelAcceso = nivelAcceso;
    }

    public String getAreaSoporte() {
        return areaSoporte;
    }

    public void setAreaSoporte(String areaSoporte) {
        this.areaSoporte = areaSoporte;
    }
}
