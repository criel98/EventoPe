package pe.edu.utp.eventope.dto;
import java.time.Instant;
/** Solo se envía a la vista del comprador autenticado; no contiene la clave secreta. */
public final class QrRespuestaDTO {
    private final String contenido; private final Instant horaServidor; private final Instant expiraEn;
    public QrRespuestaDTO(String contenido, Instant horaServidor, Instant expiraEn){this.contenido=contenido;this.horaServidor=horaServidor;this.expiraEn=expiraEn;}
    public String getContenido(){return contenido;} public Instant getHoraServidor(){return horaServidor;} public Instant getExpiraEn(){return expiraEn;}
}
