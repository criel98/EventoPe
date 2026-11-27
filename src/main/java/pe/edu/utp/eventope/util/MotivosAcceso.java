package pe.edu.utp.eventope.util;

import java.util.Set;

/**
 * Catálogo único de los motivos que ServicioAcceso registra en control_acceso_puerta.motivo.
 * Centraliza texto legible, severidad de la incidencia y si el motivo admite ingreso manual.
 */
public final class MotivosAcceso {
    /** Motivos que un administrador puede salvar con justificación. El resto indica fraude, anulación o dato inexistente. */
    private static final Set<String> ADMITEN_INGRESO_MANUAL = Set.of(
            "QR_VENCIDO_O_FUERA_DE_INTERVALO", "IDENTIDAD_NO_COINCIDE", "SIN_NOMINACION", "QR_NO_EMITIDO");

    private MotivosAcceso() {}

    public static String descripcion(String motivo) {
        if (motivo == null) return "motivo no especificado";
        return switch (motivo) {
            case "VALIDO" -> "ingreso válido";
            case "YA_UTILIZADA" -> "la entrada ya había sido utilizada";
            case "ANULADA" -> "la entrada está anulada";
            case "QR_ALTERADO" -> "el código QR presentado no era auténtico";
            case "QR_VENCIDO_O_FUERA_DE_INTERVALO" -> "el código QR estaba vencido (posible captura de pantalla)";
            case "IDENTIDAD_NO_COINCIDE" -> "el DNI presentado no coincide con el asistente nominado";
            case "EVENTO_INCORRECTO" -> "la entrada corresponde a otro evento";
            case "SIN_NOMINACION" -> "la entrada no tiene asistente nominado";
            case "QR_NO_EMITIDO" -> "la entrada aún no tiene un QR emitido";
            case "QR_MALFORMADO" -> "el contenido leído no tiene formato de QR de EventoPe";
            case "QR_DESCONOCIDO" -> "no existe una entrada asociada al código";
            default -> motivo.startsWith("INGRESO_MANUAL") ? "ingreso manual autorizado por un administrador" : motivo;
        };
    }

    public static String severidad(String motivo) {
        if (motivo == null) return "MEDIA";
        return switch (motivo) {
            case "QR_ALTERADO", "YA_UTILIZADA", "IDENTIDAD_NO_COINCIDE" -> "ALTA";
            case "QR_VENCIDO_O_FUERA_DE_INTERVALO" -> "BAJA";
            default -> "MEDIA";
        };
    }

    public static boolean admiteIngresoManual(String motivo) {
        return motivo != null && ADMITEN_INGRESO_MANUAL.contains(motivo);
    }
}
