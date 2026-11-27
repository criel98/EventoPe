package pe.edu.utp.eventope.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/** La base guarda fechas en UTC; las pantallas muestran la hora de Lima (UTC-5, sin horario de verano). */
public final class FormatoFecha {
    private static final ZoneId LIMA = ZoneId.of("America/Lima");
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private FormatoFecha() {}

    public static String lima(LocalDateTime utc) {
        if (utc == null) return "";
        return FORMATO.format(utc.atOffset(ZoneOffset.UTC).atZoneSameInstant(LIMA));
    }
}
