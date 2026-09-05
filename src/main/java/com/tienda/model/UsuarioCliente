package com.tienda.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UsuarioCliente {
    private String id;
    private String nombres;
    private String apellidos;
    private String dni;
    private String email;
    private String telefono;
    private PerfilUsuario perfil;
    private PreferenciasMusicales preferencias;
    private List<HistorialCompra> compras = new ArrayList<>();

    public UsuarioCliente() {
        this.id = UUID.randomUUID().toString();
    }

    public UsuarioCliente(String nombres, String apellidos, String dni, String email, String telefono) {
        this();
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dni = dni;
        this.email = email;
        this.telefono = telefono;
    }

    public String getId() {
        return id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    public PreferenciasMusicales getPreferencias() {
        return preferencias;
    }

    public void setPreferencias(PreferenciasMusicales preferencias) {
        this.preferencias = preferencias;
    }

    public List<HistorialCompra> getCompras() {
        return compras;
    }

    public void agregarCompra(HistorialCompra compra) {
        this.compras.add(compra);
    }

    @Override
    public String toString() {
        return "UsuarioCliente{" +
                "id='" + id + '\'' +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", dni='" + dni + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", perfil=" + perfil +
                '}';
    }
}
