package com.tienda.model;

import java.util.UUID;

public class AdministradorPlataforma {
    private String id;
    private String username;
    private String nivelAcceso;
    private String areaSoporte;

    public AdministradorPlataforma() {
        this.id = UUID.randomUUID().toString();
    }

    public AdministradorPlataforma(String username, String nivelAcceso, String areaSoporte) {
        this();
        this.username = username;
        this.nivelAcceso = nivelAcceso;
        this.areaSoporte = areaSoporte;
    }

    public String getId() {
        return id;
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

    @Override
    public String toString() {
        return "AdministradorPlataforma{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", nivelAcceso='" + nivelAcceso + '\'' +
                ", areaSoporte='" + areaSoporte + '\'' +
                '}';
    }
}
