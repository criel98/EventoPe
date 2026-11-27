package pe.edu.utp.eventope.service;

import java.util.Optional;

/**
 * Tipo de una notificación interna. El esquema no tiene columna de tipo y no se modificó para no obligar a todo el
 * equipo a migrar su base: el tipo viaja como prefijo fijo del título ("Compra: ...", "Seguridad: ...").
 */
public enum TipoNotificacion {
    COMPRA("Compra", "compra"),
    VALIDACION("Validación", "validacion"),
    SEGURIDAD("Seguridad", "seguridad");

    private static final String SEPARADOR = ": ";
    private final String etiqueta;
    private final String clase;

    TipoNotificacion(String etiqueta, String clase) {
        this.etiqueta = etiqueta;
        this.clase = clase;
    }

    public String getEtiqueta() { return etiqueta; }
    public String getClase() { return clase; }

    /** Título completo (máximo 100 caracteres, el límite de la columna). */
    public String titulo(String texto) {
        String t = etiqueta + SEPARADOR + texto;
        return t.length() <= 100 ? t : t.substring(0, 100);
    }

    public static Optional<TipoNotificacion> deTitulo(String titulo) {
        if (titulo == null) return Optional.empty();
        for (TipoNotificacion t : values()) {
            if (titulo.startsWith(t.etiqueta + SEPARADOR)) return Optional.of(t);
        }
        return Optional.empty();
    }

    /** Título sin el prefijo de tipo. */
    public static String sinPrefijo(String titulo) {
        return deTitulo(titulo).map(t -> titulo.substring(t.etiqueta.length() + SEPARADOR.length())).orElse(titulo);
    }
}
