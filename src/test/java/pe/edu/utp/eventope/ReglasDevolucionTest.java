package pe.edu.utp.eventope;

import org.junit.jupiter.api.Test;
import pe.edu.utp.eventope.service.ReglasDevolucion;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ReglasDevolucionTest {
    @Test void calculaPenalizacionYPermiteDentroDePlazo() {
        LocalDateTime ahora=LocalDateTime.of(2026,1,1,10,0);
        LocalDateTime evento=ahora.plusDays(30);
        ReglasDevolucion.validarPlazo(ahora,evento,7,true);
        assertEquals(new BigDecimal("90.00"),
                ReglasDevolucion.calcularImporte(new BigDecimal("100"),new BigDecimal("10")));
    }

    @Test void rechazaFueraDePlazoUsadaODirecta() {
        LocalDateTime ahora=LocalDateTime.of(2026,1,1,10,0);
        assertThrows(IllegalArgumentException.class, () ->
                ReglasDevolucion.validarPlazo(ahora,ahora.plusDays(5),7,true));
        assertThrows(IllegalArgumentException.class, () ->
                ReglasDevolucion.validarPlazo(ahora,ahora.plusDays(30),7,false));
    }
}
