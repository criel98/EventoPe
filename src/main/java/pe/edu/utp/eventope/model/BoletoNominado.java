package pe.edu.utp.eventope.model;

import java.io.Serializable;

/** Datos de boleto_nominado. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class BoletoNominado implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int entradaId;
    private String dniAsistente;
    private String nombresAsistente;
    private String apellidosAsistente;

    public BoletoNominado() {}

    public BoletoNominado(int id, int entradaId, String dniAsistente, String nombresAsistente, String apellidosAsistente) {
        this.id = id;
        this.entradaId = entradaId;
        this.dniAsistente = dniAsistente;
        this.nombresAsistente = nombresAsistente;
        this.apellidosAsistente = apellidosAsistente;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEntradaId() { return entradaId; }
    public void setEntradaId(int entradaId) { this.entradaId = entradaId; }

    public String getDniAsistente() { return dniAsistente; }
    public void setDniAsistente(String dniAsistente) { this.dniAsistente = dniAsistente; }

    public String getNombresAsistente() { return nombresAsistente; }
    public void setNombresAsistente(String nombresAsistente) { this.nombresAsistente = nombresAsistente; }

    public String getApellidosAsistente() { return apellidosAsistente; }
    public void setApellidosAsistente(String apellidosAsistente) { this.apellidosAsistente = apellidosAsistente; }


}
