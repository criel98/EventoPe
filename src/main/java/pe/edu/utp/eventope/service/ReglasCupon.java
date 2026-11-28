package pe.edu.utp.eventope.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public final class ReglasCupon {
    private ReglasCupon() {}

    public static String normalizarCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) return null;
        String normalizado = codigo.trim().toUpperCase(Locale.ROOT);
        if (!normalizado.matches("[A-Z0-9_-]{1,20}")) {
            throw new IllegalArgumentException("El código del cupón tiene un formato inválido.");
        }
        return normalizado;
    }

    public static BigDecimal calcularDescuento(BigDecimal subtotal, BigDecimal porcentaje) {
        if (subtotal == null || porcentaje == null || subtotal.signum() < 0
                || porcentaje.signum() < 0 || porcentaje.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Importe o porcentaje de cupón inválido.");
        }
        return subtotal.multiply(porcentaje).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
}
