package pe.edu.utp.eventope.dto;

import pe.edu.utp.eventope.model.NotificacionUsuario;
import pe.edu.utp.eventope.service.TipoNotificacion;
import pe.edu.utp.eventope.util.FormatoFecha;

import java.io.Serializable;

/** Vista de una notificación para la bandeja del cliente. Solo contiene lo que se muestra. */
public final class NotificacionDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String tipoEtiqueta;
    private final String tipoClase;
    private final String titulo;
    private final String contenido;
    private final String fecha;
    private final boolean leido;

    public NotificacionDTO(NotificacionUsuario n) {
        var tipo = TipoNotificacion.deTitulo(n.getTituloMensaje());
        this.id = n.getId();
        this.tipoEtiqueta = tipo.map(TipoNotificacion::getEtiqueta).orElse("Aviso");
        this.tipoClase = tipo.map(TipoNotificacion::getClase).orElse("general");
        this.titulo = TipoNotificacion.sinPrefijo(n.getTituloMensaje());
        this.contenido = n.getContenido();
        this.fecha = FormatoFecha.lima(n.getFechaEnvio());
        this.leido = n.isLeido();
    }

    public int getId() { return id; }
    public String getTipoEtiqueta() { return tipoEtiqueta; }
    public String getTipoClase() { return tipoClase; }
    public String getTitulo() { return titulo; }
    public String getContenido() { return contenido; }
    public String getFecha() { return fecha; }
    public boolean isLeido() { return leido; }
}
