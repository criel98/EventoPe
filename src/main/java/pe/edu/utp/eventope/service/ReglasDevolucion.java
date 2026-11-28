package pe.edu.utp.eventope.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

public final class ReglasDevolucion {
    private ReglasDevolucion() {}

    public static void validarPlazo(LocalDateTime ahora, LocalDateTime fechaEvento,
                                    int diasMinimosPrevios, boolean activa) {
        if (ahora == null || fechaEvento == null || diasMinimosPrevios < 0) {
            throw new IllegalArgumentException("La política o las fechas de devolución son inválidas.");
        }
        if (!activa) throw new IllegalArgumentException("La política de devolución está desactivada.");
        Duration plazo = Duration.between(ahora, fechaEvento);
        if (plazo.isNegative() || plazo.compareTo(Duration.ofDays(diasMinimosPrevios)) < 0) {
            throw new IllegalArgumentException("La política ya no permite devolver esta compra.");
        }
    }

    public static BigDecimal calcularImporte(BigDecimal total, BigDecimal penalizacion) {
        if (total == null || total.signum() < 0 || penalizacion == null
                || penalizacion.signum() < 0 || penalizacion.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Importe o penalización inválidos.");
        }
        BigDecimal montoPenalizacion = total.multiply(penalizacion)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        return total.subtract(montoPenalizacion).setScale(2);
    }
}
