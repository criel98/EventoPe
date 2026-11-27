package pe.edu.utp.eventope;

import org.junit.jupiter.api.Test;
import pe.edu.utp.eventope.config.Configuracion;
import pe.edu.utp.eventope.config.ConexionBD;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

@org.junit.jupiter.api.Disabled("Carga manual sustituida por sql/04_cartelera_demo.sql; nunca modificar la BD local al ejecutar pruebas")
public class CargarEventosDemoTest {

    @Test
    void sembrarEventosNuevosSiEsPosible() {
        Properties p = new Properties();
        try {
            Path propPath = Path.of("config/eventope.local.properties");
            if (!propPath.toFile().exists()) {
                propPath = Path.of("../config/eventope.local.properties");
            }
            if (!propPath.toFile().exists()) {
                System.out.println("No se encontró eventope.local.properties, omitiendo inserción en BD directa");
                return;
            }
            try (var in = new FileInputStream(propPath.toFile())) {
                p.load(in);
            }
            ConexionBD conexion = new ConexionBD(new Configuracion(p));
            try (Connection c = conexion.abrir(); Statement s = c.createStatement()) {
                // Verificar si ya existe Rock Fest
                try (ResultSet r = s.executeQuery("SELECT id FROM evento_cultural WHERE nombre LIKE '%Rock Fest%'")) {
                    if (r.next()) {
                        System.out.println("Los eventos de demostración ya existen en la base de datos.");
                        return;
                    }
                }

                // Asegurar organizador 1
                s.executeUpdate("INSERT IGNORE INTO organizador_evento(id, ruc, razon_social, email_contacto, telefono) " +
                        "VALUES (1, '20000000001', 'Live Nation & EventoPe Producciones', 'contacto@eventope.pe', '999888777')");

                // Locales
                s.executeUpdate("INSERT IGNORE INTO local_concierto(id, nombre_local, direccion, capacidad_max, ciudad) VALUES " +
                        "(2, 'Estadio Nacional', 'Calle José Díaz s/n', 45000, 'Lima'), " +
                        "(3, 'Gran Teatro Nacional', 'Av. Javier Prado Este 2225', 1500, 'Lima'), " +
                        "(4, 'La Estación de Barranco', 'Av. Pedro de Osma 112', 400, 'Lima'), " +
                        "(5, 'Explanada Costa Verde', 'Circuito de Playas s/n', 10000, 'Lima')");

                // Artistas
                s.executeUpdate("INSERT IGNORE INTO artista(id, nombre_artistico, genero_musical, pais_origen) VALUES " +
                        "(2, 'Libido & Mar de Copas', 'Rock Alternativo', 'Perú'), " +
                        "(3, 'Ensamble Teatral Clásico', 'Teatro Musical', 'Perú'), " +
                        "(4, 'Carlos Galdós & Amigos', 'Stand-Up Comedy', 'Perú'), " +
                        "(5, 'DJ Coastal & Global Beats', 'Electrónica', 'Perú')");

                // Eventos
                s.executeUpdate("INSERT INTO evento_cultural(id, nombre, descripcion, tipo_evento, fecha, aforo, local_id, organizador_id, artista_id, estado) VALUES " +
                        "(10, 'Rock Fest Lima 2026: Libido & Mar de Copas', 'Una noche legendaria con los mayores himnos del rock peruano en vivo, sonido envolvente y luces de última generación.', 'CONCIERTO', UTC_TIMESTAMP() + INTERVAL 25 DAY, 5000, 2, 1, 2, 'PUBLICADO'), " +
                        "(11, 'La Tragedia de la Sombra Real: Teatro en Vivo', 'Una aclamada producción dramática en vivo con vestuario renacentista, orquesta de cámara y actuaciones memorables.', 'TEATRO', UTC_TIMESTAMP() + INTERVAL 35 DAY, 800, 3, 1, 3, 'PUBLICADO'), " +
                        "(12, 'Noche de Risas: Stand-Up Comedy Live', 'El show de comedia y humor más aclamado. Monólogos exclusivos, risas y la mejor vibra con los mejores comediantes.', 'TEATRO', UTC_TIMESTAMP() + INTERVAL 14 DAY, 350, 4, 1, 4, 'PUBLICADO'), " +
                        "(13, 'Lima Summer Beats Festival: Coastal Rave', 'El festival playero de música electrónica más esperado de la temporada. Escenarios gigantes, lásers y pirotecnia.', 'FESTIVAL', UTC_TIMESTAMP() + INTERVAL 45 DAY, 6000, 5, 1, 5, 'PUBLICADO') " +
                        "ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), estado='PUBLICADO'");

                // Categorias de asientos
                s.executeUpdate("INSERT INTO categoria_asiento(evento_id, nombre_zona, precio_base, aforo_total, aforo_disponible) VALUES " +
                        "(10, 'Campo VIP', 180.00, 2000, 2000), " +
                        "(10, 'Tribuna Occidente', 120.00, 1500, 1500), " +
                        "(10, 'General', 65.00, 1500, 1500), " +
                        "(11, 'Platea Preferencial', 140.00, 300, 300), " +
                        "(11, 'Palco', 95.00, 250, 250), " +
                        "(11, 'Galería', 45.00, 250, 250), " +
                        "(12, 'Mesa VIP Primera Fila', 85.00, 100, 100), " +
                        "(12, 'General', 50.00, 250, 250), " +
                        "(13, 'Ultra VIP Lounge', 220.00, 1500, 1500), " +
                        "(13, 'Dance Floor', 110.00, 4500, 4500) " +
                        "ON DUPLICATE KEY UPDATE precio_base=VALUES(precio_base)");

                System.out.println("Eventos y zonas insertados con éxito en la base de datos.");
            }
        } catch (Exception e) {
            System.out.println("No se pudo conectar a la base de datos para sembrar (tal vez MySQL esté en otro puerto/password): " + e.getMessage());
        }
    }
}
