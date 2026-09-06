package com.tienda.model;

public class NotificacionUsuario {
    private int id;
    private String mensaje;
    private String fechaEnvio;
    private boolean leido;

    public NotificacionUsuario() {
    }

    public NotificacionUsuario(int id, String mensaje, String fechaEnvio, boolean leido) {
        this.id = id;
        this.mensaje = mensaje;
        this.fechaEnvio = fechaEnvio;
        this.leido = leido;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(String fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public boolean isLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }
}
