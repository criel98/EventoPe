package pe.edu.utp.eventope.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "boleto_nominado")
public class BoletoNominado implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "entrada_id")
    private int entradaId;

    @Column(name = "dni_asistente")
    private String dniAsistente;

    @Column(name = "nombres_asistente")
    private String nombresAsistente;

    @Column(name = "apellidos_asistente")
    private String apellidosAsistente;

    public BoletoNominado() {
    }

    public BoletoNominado(int id, int entradaId, String dniAsistente, String nombresAsistente, String apellidosAsistente) {
        this.id = id;
        this.entradaId = entradaId;
        this.dniAsistente = dniAsistente;
        this.nombresAsistente = nombresAsistente;
        this.apellidosAsistente = apellidosAsistente;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEntradaId() {
        return entradaId;
    }

    public void setEntradaId(int entradaId) {
        this.entradaId = entradaId;
    }

    public String getDniAsistente() {
        return dniAsistente;
    }

    public void setDniAsistente(String dniAsistente) {
        this.dniAsistente = dniAsistente;
    }

    public String getNombresAsistente() {
        return nombresAsistente;
    }

    public void setNombresAsistente(String nombresAsistente) {
        this.nombresAsistente = nombresAsistente;
    }

    public String getApellidosAsistente() {
        return apellidosAsistente;
    }

    public void setApellidosAsistente(String apellidosAsistente) {
        this.apellidosAsistente = apellidosAsistente;
    }
}
