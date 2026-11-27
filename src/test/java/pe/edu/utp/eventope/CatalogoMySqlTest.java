package pe.edu.utp.eventope;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.service.ServicioCatalogo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "EVENTOPE_TEST_DB_URL", matches = ".+")
class CatalogoMySqlTest {
    private ProveedorConexion conexiones;
    private ServicioCatalogo servicio;

    @BeforeEach
    void preparar() throws Exception {
        conexiones = () -> DriverManager.getConnection(
                System.getenv("EVENTOPE_TEST_DB_URL"),
                System.getenv().getOrDefault("EVENTOPE_TEST_DB_USER", "root"),
                System.getenv().getOrDefault("EVENTOPE_TEST_DB_PASSWORD", "")
        );
        servicio = new ServicioCatalogo(conexiones);

        try (Connection c = conexiones.abrir(); Statement s = c.createStatement()) {
            if(!"eventope_base_test".equals(c.getCatalog()))throw new IllegalStateException("Solo se admite eventope_base_test");
            DatosPrueba.limpiar();

            s.executeUpdate("INSERT INTO organizador_evento(id, ruc, razon_social, email_contacto) VALUES (1, '20000000001', 'Demo', 'demo@example.invalid')");
            s.executeUpdate("INSERT INTO local_concierto(id, nombre_local, direccion, capacidad_max, ciudad) VALUES (1, 'Movistar Arena', 'Av. Demo 123', 2000, 'Lima')");
            s.executeUpdate("INSERT INTO artista(id, nombre_artistico, genero_musical, pais_origen) VALUES (1, 'Los Primos', 'Rock', 'Perú')");
            s.executeUpdate("INSERT INTO usuario_cliente(id, nombres, apellidos, dni, email, contrasena_hash) VALUES (1, 'Ana', 'García', '10000001', 'ana@example.invalid', 'hash-no-login')");
            s.executeUpdate("INSERT INTO administrador_plataforma(id, username, contrasena_hash, nivel_acceso) VALUES (1, 'guardia1', 'hash-no-login', 'VALIDACION')");
            s.executeUpdate("INSERT INTO evento_cultural(id, nombre, descripcion, tipo_evento, fecha, aforo, local_id, organizador_id, artista_id, estado) VALUES (1, 'Festival Demo', 'Evento publicado', 'FESTIVAL', '2026-11-15 20:00:00', 500, 1, 1, 1, 'PUBLICADO')");
            s.executeUpdate("INSERT INTO evento_cultural(id, nombre, descripcion, tipo_evento, fecha, aforo, local_id, organizador_id, artista_id, estado) VALUES (2, 'Borrador Demo', 'No visible', 'CONCIERTO', '2026-11-20 20:00:00', 200, 1, 1, 1, 'BORRADOR')");
            s.executeUpdate("INSERT INTO categoria_asiento(id, evento_id, nombre_zona, precio_base, aforo_total, aforo_disponible) VALUES (1, 1, 'General', 120.00, 200, 180)");
            s.executeUpdate("INSERT INTO categoria_asiento(id, evento_id, nombre_zona, precio_base, aforo_total, aforo_disponible) VALUES (2, 1, 'VIP', 250.00, 50, 30)");
        }
    }

    @Test
    void buscaSoloEventosPublicados() throws Exception {
        var eventos = servicio.buscar(null, null, null);
        assertEquals(1, eventos.size());
        assertEquals("Festival Demo", eventos.get(0).getNombre());
    }

    @Test
    void filtrosSinInyeccionUsanPreparedStatement() throws Exception {
        var eventos = servicio.buscar("%' OR '1'='1", "Los%", "FESTIVAL");
        assertEquals(0, eventos.size());
    }

    @Test
    void detalleDeEventoPublicadoDevuelveCategorias() throws Exception {
        var evento = servicio.detalle(1);
        assertNotNull(evento);
        assertEquals(2, evento.getCategorias().size());
    }

    @Test void ocultaBorradorEInexistente() throws Exception {
        assertThrows(IllegalArgumentException.class,()->servicio.detalle(2));assertThrows(IllegalArgumentException.class,()->servicio.detalle(999));assertThrows(IllegalArgumentException.class,()->servicio.detalle(-1));
    }
    @Test void filtrosIndividualesYCombinados() throws Exception {
        assertEquals(1,servicio.buscar("Festival",null,null).size());assertEquals(1,servicio.buscar(null,"Primos",null).size());assertEquals(1,servicio.buscar(null,null,"festival").size());assertEquals(1,servicio.buscar("Festival","Primos","FESTIVAL").size());assertTrue(servicio.buscar("Festival","Otro",null).isEmpty());assertEquals(1,servicio.buscar("  ","",null).size());
    }
    @Test void filtrosLargosYComillas() throws Exception {
        assertThrows(IllegalArgumentException.class,()->servicio.buscar("x".repeat(151),null,null));assertTrue(servicio.buscar("' OR 1=1 --",null,null).isEmpty());
    }
    @Test void zonasPertenecenAlEventoYConservanAgotado() throws Exception {
        DatosPrueba.sql("UPDATE categoria_asiento SET aforo_disponible=0 WHERE id=1");
        var detalle=servicio.detalle(1);assertTrue(detalle.getCategorias().stream().allMatch(c->c.getEventoId()==1));assertEquals(0,detalle.getCategorias().stream().filter(c->c.getId()==1).findFirst().orElseThrow().getAforoDisponible());
    }
}
