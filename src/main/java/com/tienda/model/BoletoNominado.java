package com.tienda.model;

public class BoletoNominado {
    private int id;
    private int entradaId;
    private String dniAsistente;
    private String nombresAsistente;
    private String apellidosAsistente;

    public BoletoNominado() {
    }

    public BoletoNominado(int id, int entradaId, String dniAsistente,
            String nombresAsistente, String apellidosAsistente) {
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