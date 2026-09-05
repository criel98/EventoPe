package com.tienda.model;

public class OrganizadorEvento {
    private int id;
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
