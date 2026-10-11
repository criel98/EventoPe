package pe.edu.utp.eventope.config;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
/** Configuración externa. No admite valores secretos predeterminados. */
public final class Configuracion {
    private final Properties datos;
    public Configuracion(Properties datos) { this.datos = new Properties(); this.datos.putAll(datos); }
    public static Configuracion cargar() {
        String archivo = System.getProperty("eventope.config");
        if (archivo == null || archivo.isBlank()) archivo = System.getenv("EVENTOPE_CONFIG");
        if (archivo == null || archivo.isBlank()) throw new IllegalStateException("Configure EVENTOPE_CONFIG o -Deventope.config con un archivo externo");
        Properties p = new Properties();
        try (var r = Files.newBufferedReader(Path.of(archivo))) { p.load(r); }
        catch (IOException e) { throw new IllegalStateException("No se pudo leer la configuración externa", e); }
        return new Configuracion(p);
    }
    public String requerido(String clave) {
        String valor = datos.getProperty(clave);
        if (valor == null || valor.isBlank() || valor.equals("REEMPLAZAR")) throw new IllegalStateException("Falta configurar " + clave);
        return valor;
    }
}
