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
            String[] tablas = {
                    "detalle_transaccion", "metodo_pago", "comprobante_pago", "boleto_nominado",
                    "ticket_qr_dinamico", "historial_rotacion_qr", "entrada_digital", "control_acceso_puerta",
                    "dispositivo_validacion", "transaccion_compra", "politica_devolucion", "categoria_asiento",
                    "preferencias_musicales", "perfil_usuario", "evento_cultural", "cupon_descuento",
                    "administrador_plataforma", "usuario_cliente", "artista", "local_concierto", "organizador_evento"
            };
            for (String tabla : tablas) {
                s.executeUpdate("DELETE FROM " + tabla);
            }

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
}
