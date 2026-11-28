package pe.edu.utp.eventope;

import org.junit.jupiter.api.Test;
import pe.edu.utp.eventope.service.ReglasCupon;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ReglasCuponTest {
    @Test void normalizaYCalculaDescuentoServidor() {
        assertEquals("ETHAN10", ReglasCupon.normalizarCodigo(" ethan10 "));
        assertEquals(new BigDecimal("18.00"),
                ReglasCupon.calcularDescuento(new BigDecimal("180.00"), new BigDecimal("10")));
    }

    @Test void rechazaCodigoYPorcentajeInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> ReglasCupon.normalizarCodigo("cupón inválido"));
        assertThrows(IllegalArgumentException.class, () ->
                ReglasCupon.calcularDescuento(new BigDecimal("100"), new BigDecimal("101")));
    }
}
