package com.tienda.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class PerfilUsuario {
    private String id;
    private LocalDateTime fechaRegistro;
    private String estadoVerificacion;
    private String pais;
    private String fechaNacimiento;

    public PerfilUsuario() {
        this.id = UUID.randomUUID().toString();
        this.fechaRegistro = LocalDateTime.now();
        this.estadoVerificacion = "PENDIENTE";
    }

    public PerfilUsuario(String pais, String fechaNacimiento) {
        this();
        this.pais = pais;
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public String getEstadoVerificacion() {
        return estadoVerificacion;
    }

    public void setEstadoVerificacion(String estadoVerificacion) {
        this.estadoVerificacion = estadoVerificacion;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public boolean esVerificado() {
        return "VERIFICADO".equalsIgnoreCase(this.estadoVerificacion);
    }

    @Override
    public String toString() {
        return "PerfilUsuario{" +
                "id='" + id + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                ", estadoVerificacion='" + estadoVerificacion + '\'' +
                ", pais='" + pais + '\'' +
                ", fechaNacimiento='" + fechaNacimiento + '\'' +
                '}';
    }
}
