package pe.edu.utp.eventope.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Datos de notificacion_usuario. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class NotificacionUsuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int clienteId;
    private String tituloMensaje;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private boolean leido;

    public NotificacionUsuario() {}

    public NotificacionUsuario(int id, int clienteId, String tituloMensaje, String contenido, LocalDateTime fechaEnvio, boolean leido) {
        this.id = id;
        this.clienteId = clienteId;
        this.tituloMensaje = tituloMensaje;
        this.contenido = contenido;
        this.fechaEnvio = fechaEnvio;
        this.leido = leido;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public String getTituloMensaje() { return tituloMensaje; }
    public void setTituloMensaje(String tituloMensaje) { this.tituloMensaje = tituloMensaje; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }


}
