package pe.edu.utp.eventope.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidencia_acceso")
public class IncidenciaAcceso implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    private String descripcion;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    public IncidenciaAcceso() {
    }

    public IncidenciaAcceso(int id, String descripcion, LocalDateTime fechaHora) {
        this.id = id;
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}
