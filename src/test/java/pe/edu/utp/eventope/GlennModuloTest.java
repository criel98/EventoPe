package pe.edu.utp.eventope;

import org.junit.jupiter.api.Test;
import pe.edu.utp.eventope.dto.BoletoDetalleDTO;
import pe.edu.utp.eventope.dto.DispositivoOperadorDTO;
import pe.edu.utp.eventope.dto.EntradaResumenDTO;
import pe.edu.utp.eventope.dto.SesionUsuario;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GlennModuloTest {

    @Test
    void testEntradaResumenDniOculto() {
        var resumen = new EntradaResumenDTO(
                1, "COD-001", "Concierto Rock",
                LocalDateTime.of(2026, 11, 20, 20, 0),
                "Estadio Nacional", "Lima", "VIP",
                new BigDecimal("150.00"), "DISPONIBLE",
                "Juan Perez", "12345678"
        );

        assertEquals(1, resumen.getId());
        assertEquals("COD-001", resumen.getCodigoEntrada());
        assertEquals("Concierto Rock", resumen.getEventoNombre());
        assertEquals("VIP", resumen.getZona());
        assertEquals("DISPONIBLE", resumen.getEstadoUso());
        assertEquals("****5678", resumen.getAsistenteDniEnmascarado());
    }

    @Test
    void testBoletoDetalleDniOculto() {
        var boleto = new BoletoDetalleDTO(
                2, "COD-002", 5, "Festival Folk",
                LocalDateTime.of(2026, 12, 5, 18, 0),
                "Parque de la Exposicion", "Av. 28 de Julio", "Lima", "General",
                new BigDecimal("80.00"), "UTILIZADA",
                "Maria", "Lopez", "87654321",
                "COMP-2026-0001"
        );

        assertEquals(2, boleto.getId());
        assertEquals("Festival Folk", boleto.getEventoNombre());
        assertEquals("Maria Lopez", boleto.getAsistenteNombreCompleto());
        assertEquals("****4321", boleto.getAsistenteDniEnmascarado());
        assertEquals("COMP-2026-0001", boleto.getComprobante());
    }

    @Test
    void testDniCortoOcultamiento() {
        var corto = new EntradaResumenDTO(
                3, "COD-003", "Evento",
                LocalDateTime.now(), "Lugar", "Lima", "Zona",
                new BigDecimal("50.00"), "DISPONIBLE",
                "Asistente", "123"
        );
        assertEquals("****", corto.getAsistenteDniEnmascarado());

        var nulo = new EntradaResumenDTO(
                4, "COD-004", "Evento",
                LocalDateTime.now(), "Lugar", "Lima", "Zona",
                new BigDecimal("50.00"), "DISPONIBLE",
                null, null
        );
        assertEquals("****", nulo.getAsistenteDniEnmascarado());
        assertEquals("Sin asignar", nulo.getAsistenteNombre());
    }

    @Test
    void testDispositivoOperadorDTO() {
        var d = new DispositivoOperadorDTO(
                1, 2, 5, "Rock Fest 2026", "Puerta Norte 1", "Móvil Entrada Norte"
        );

        assertEquals(1, d.getDispositivoId());
        assertEquals(2, d.getOperadorId());
        assertEquals(5, d.getEventoId());
        assertEquals("Rock Fest 2026", d.getEventoNombre());
        assertEquals("Puerta Norte 1", d.getNombrePuerta());
        assertEquals("Móvil Entrada Norte", d.getModeloCelular());
    }

    @Test
    void testSesionUsuarioRestriccionesGlenn() {
        var cliente = new SesionUsuario(1, SesionUsuario.Tipo.CLIENTE, SesionUsuario.Rol.CLIENTE, "Cliente Demo");
        cliente.exigirCliente();
        assertThrows(SecurityException.class, cliente::exigirValidacion);

        var guardia = new SesionUsuario(2, SesionUsuario.Tipo.PERSONAL, SesionUsuario.Rol.VALIDACION, "Guardia Demo");
        guardia.exigirValidacion();
        assertThrows(SecurityException.class, guardia::exigirCliente);
    }
}
