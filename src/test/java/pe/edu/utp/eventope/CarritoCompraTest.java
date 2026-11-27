package pe.edu.utp.eventope;

import org.junit.jupiter.api.Test;
import pe.edu.utp.eventope.dto.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarritoCompraTest {

    @Test
    void lineaCarritoValidacion() {
        assertThrows(IllegalArgumentException.class, () -> new LineaCarrito(-1, "VIP", 1, "100.00"));
        assertThrows(IllegalArgumentException.class, () -> new LineaCarrito(1, null, 1, "100.00"));
        assertThrows(IllegalArgumentException.class, () -> new LineaCarrito(1, "VIP", 0, "100.00"));
        assertThrows(IllegalArgumentException.class, () -> new LineaCarrito(1, "VIP", 5, "100.00"));
        assertThrows(IllegalArgumentException.class, () -> new LineaCarrito(1, "VIP", 2, null));

        LineaCarrito linea = new LineaCarrito(1, "VIP", 2, "150.50");
        assertEquals(1, linea.getCategoriaId());
        assertEquals("VIP", linea.getZona());
        assertEquals(2, linea.getCantidad());
        assertEquals("150.50", linea.getPrecioUnitario());
    }

    @Test
    void nominacionAsistenteValidacion() {
        assertThrows(IllegalArgumentException.class, () -> new NominacionAsistente("123", "Juan", "Perez"));
        assertThrows(IllegalArgumentException.class, () -> new NominacionAsistente("1234567a", "Juan", "Perez"));
        assertThrows(IllegalArgumentException.class, () -> new NominacionAsistente("12345678", "   ", "Perez"));
        assertThrows(IllegalArgumentException.class, () -> new NominacionAsistente("12345678", "Juan", ""));
        assertThrows(IllegalArgumentException.class, () -> new NominacionAsistente("12345678", "A".repeat(101), "Perez"));

        NominacionAsistente nom = new NominacionAsistente(" 12345678 ", "  Juan Carlos  ", "  Perez Gomez ");
        assertEquals("12345678", nom.getDni());
        assertEquals("Juan Carlos", nom.getNombres());
        assertEquals("Perez Gomez", nom.getApellidos());
    }

    @Test
    void carritoLimiteCuatroEntradas() {
        LineaCarrito l1 = new LineaCarrito(10, "General", 2, "50.00");
        CarritoCompra carrito = new CarritoCompra(1, "Concierto", List.of(l1));
        assertEquals(1, carrito.getEventoId());
        assertEquals("Concierto", carrito.getEventoNombre());
        assertEquals(2, carrito.getCantidadTotal());

        // Agregar a la misma categoría
        carrito.agregar(new LineaCarrito(10, "General", 2, "50.00"));
        assertEquals(4, carrito.getCantidadTotal());

        // Exceder límite de 4 debe fallar
        assertThrows(IllegalArgumentException.class, () -> carrito.agregar(new LineaCarrito(10, "General", 1, "50.00")));
        assertThrows(IllegalArgumentException.class, () -> carrito.agregar(new LineaCarrito(20, "VIP", 1, "100.00")));
    }

    @Test
    void claveConfirmacionYNominaciones() {
        LineaCarrito l = new LineaCarrito(10, "General", 1, "50.00");
        CarritoCompra carrito = new CarritoCompra(1, "Concierto", List.of(l));
        assertNull(carrito.getClaveConfirmacion());
        carrito.asegurarClave();
        String clave1 = carrito.getClaveConfirmacion();
        assertNotNull(clave1);
        carrito.asegurarClave();
        assertEquals(clave1, carrito.getClaveConfirmacion());

        carrito.setNominaciones(List.of(new NominacionAsistente("87654321", "Maria", "Lopez")));
        assertEquals(1, carrito.getNominaciones().size());

        // Al agregar otra entrada, se reinician nominaciones y clave
        carrito.agregar(new LineaCarrito(20, "VIP", 1, "100.00"));
        assertTrue(carrito.getNominaciones().isEmpty());
        assertNull(carrito.getClaveConfirmacion());
    }

    @Test
    void compraConfirmadaPropiedades() {
        CompraConfirmada cc = new CompraConfirmada(99, "UUID-KEY", "AC-001", "Festival", new BigDecimal("250.00"), 3);
        assertEquals(99, cc.getId());
        assertEquals("UUID-KEY", cc.getCodigo());
        assertEquals("AC-001", cc.getComprobante());
        assertEquals("Festival", cc.getEvento());
        assertEquals(new BigDecimal("250.00"), cc.getTotal());
        assertEquals(3, cc.getCantidad());
    }
}
