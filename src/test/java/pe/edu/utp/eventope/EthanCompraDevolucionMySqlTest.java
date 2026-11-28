package pe.edu.utp.eventope;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.dto.CarritoCompra;
import pe.edu.utp.eventope.dto.CompraConfirmada;
import pe.edu.utp.eventope.dto.LineaCarrito;
import pe.edu.utp.eventope.dto.NominacionAsistente;
import pe.edu.utp.eventope.service.ServicioAutenticacion;
import pe.edu.utp.eventope.service.ServicioCompra;
import pe.edu.utp.eventope.service.ServicioDevolucion;
import pe.edu.utp.eventope.util.PasswordUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "EVENTOPE_TEST_DB_URL", matches = ".+")
class EthanCompraDevolucionMySqlTest {
    private final ProveedorConexion conexiones = DatosPrueba.conexiones();
    private final ServicioCompra compras = new ServicioCompra(conexiones);
    private final ServicioDevolucion devoluciones = new ServicioDevolucion(conexiones);

    @BeforeEach
    void preparar() throws Exception {
        DatosPrueba.limpiar();
        try (Connection c = conexiones.abrir(); Statement s = c.createStatement()) {
            s.executeUpdate("INSERT INTO usuario_cliente(id,nombres,apellidos,dni,email,contrasena_hash) "
                    + "VALUES (1,'Cliente','Uno','10000001','a@example.invalid','hash-no-login'),"
                    + "(2,'Cliente','Dos','10000002','b@example.invalid','hash-no-login')");
            s.executeUpdate("INSERT INTO organizador_evento(id,ruc,razon_social,email_contacto) "
                    + "VALUES(1,'20000000001','Demo','demo@example.invalid')");
            s.executeUpdate("INSERT INTO local_concierto(id,nombre_local,direccion,capacidad_max,ciudad) "
                    + "VALUES(1,'Local de prueba','Dirección de prueba',100,'Lima')");
            s.executeUpdate("INSERT INTO evento_cultural(id,nombre,tipo_evento,fecha,aforo,local_id,organizador_id,estado) "
                    + "VALUES(1,'Evento de prueba','CONCIERTO',DATE_ADD(UTC_TIMESTAMP(),INTERVAL 30 DAY),100,1,1,'PUBLICADO')");
            s.executeUpdate("INSERT INTO categoria_asiento(id,evento_id,nombre_zona,precio_base,aforo_total,aforo_disponible) "
                    + "VALUES(1,1,'General',100.00,100,100),(2,1,'Preferente',50.00,100,100)");
            s.executeUpdate("INSERT INTO politica_devolucion(evento_id,dias_minimos_previos,penalizacion_porcentaje,estado_activo) "
                    + "VALUES(1,7,10.00,TRUE)");
        }
    }

    @Test
    void cupónVigenteCalculaYDistribuyeElDescuentoEnDetalles() throws Exception {
        insertarCupon("ETHAN10", "10.00", LocalDateTime.now(ZoneOffset.UTC).plusDays(2), "ACTIVO");
        CompraConfirmada compra = comprar(1, List.of(1, 2), List.of(1, 1), "ETHAN10", false);

        assertEquals(0, new BigDecimal("150.00").compareTo(decimal(
                "SELECT subtotal FROM transaccion_compra WHERE id=?", compra.getId())));
        assertEquals(0, new BigDecimal("15.00").compareTo(decimal(
                "SELECT descuento FROM transaccion_compra WHERE id=?", compra.getId())));
        assertEquals(0, new BigDecimal("135.00").compareTo(decimal(
                "SELECT total FROM transaccion_compra WHERE id=?", compra.getId())));
        assertEquals(0, new BigDecimal("15.00").compareTo(decimal(
                "SELECT SUM(descuento_aplicado) FROM detalle_transaccion WHERE transaccion_id=?", compra.getId())));
        assertEquals(0, scalar("SELECT COUNT(*) FROM detalle_transaccion WHERE transaccion_id=? "
                + "AND descuento_aplicado > precio_aplicado", compra.getId()));
        assertTrue(scalar("SELECT cupon_id FROM transaccion_compra WHERE id=?", compra.getId()) > 0);
    }

    @Test
    void rechazaCuponInexistenteSinCrearFilasNiConsumirCupo() throws Exception {
        CarritoCompra carrito = carrito(1, List.of(1), List.of(1), "NOEXISTE", false);
        assertThrows(SQLException.class, () -> compras.confirmar(1, carrito));
        assertEquals(0, DatosPrueba.count("transaccion_compra"));
        assertEquals(0, DatosPrueba.count("entrada_digital"));
        assertEquals(100, stock(1));
    }

    @Test
    void rechazaCuponVencido() throws Exception {
        insertarCupon("VENCIDO", "10.00", LocalDateTime.now(ZoneOffset.UTC).minusDays(1), "ACTIVO");
        assertThrows(SQLException.class,
                () -> compras.confirmar(1, carrito(1, List.of(1), List.of(1), "VENCIDO", false)));
        assertEquals(0, DatosPrueba.count("transaccion_compra"));
        assertEquals(100, stock(1));
    }

    @Test
    void rechazaCuponInactivo() throws Exception {
        insertarCupon("INACTIVO", "10.00", LocalDateTime.now(ZoneOffset.UTC).plusDays(2), "INACTIVO");
        assertThrows(SQLException.class,
                () -> compras.confirmar(1, carrito(1, List.of(1), List.of(1), "INACTIVO", false)));
        assertEquals(0, DatosPrueba.count("transaccion_compra"));
        assertEquals(100, stock(1));
    }

    @Test
    void compraAprobadaRegistraPagoYReduceCupo() throws Exception {
        CompraConfirmada compra = comprar(1, List.of(1), List.of(2), null, false);
        assertEquals(1, DatosPrueba.count("transaccion_compra"));
        assertEquals(2, scalar("SELECT COUNT(*) FROM detalle_transaccion WHERE transaccion_id=?", compra.getId()));
        assertEquals(1, scalar("SELECT COUNT(*) FROM metodo_pago WHERE transaccion_id=?", compra.getId()));
        assertEquals(98, stock(1));
    }

    @Test
    void confirmacionRepetidaDevuelveLaMismaCompraUnaSolaVez() throws Exception {
        CarritoCompra carrito = carrito(1, List.of(1), List.of(2), null, false);
        CompraConfirmada primera = compras.confirmar(1, carrito);
        CompraConfirmada repetida = compras.confirmar(1, carrito);
        assertEquals(primera.getId(), repetida.getId());
        assertEquals(1, DatosPrueba.count("transaccion_compra"));
        assertEquals(2, DatosPrueba.count("entrada_digital"));
        assertEquals(98, stock(1));
    }

    @Test
    void rechazaCompraSinCupoDisponible() throws Exception {
        actualizar("UPDATE categoria_asiento SET aforo_disponible=0 WHERE id=1");
        assertThrows(SQLException.class,
                () -> compras.confirmar(1, carrito(1, List.of(1), List.of(1), null, false)));
        assertEquals(0, DatosPrueba.count("transaccion_compra"));
        assertEquals(0, DatosPrueba.count("entrada_digital"));
        assertEquals(0, stock(1));
    }

    @Test
    void noPermiteComprarLaQuintaEntradaDelMismoEvento() throws Exception {
        comprar(1, List.of(1), List.of(4), null, false);
        assertThrows(SQLException.class,
                () -> compras.confirmar(1, carrito(1, List.of(2), List.of(1), null, false)));
        assertEquals(1, DatosPrueba.count("transaccion_compra"));
        assertEquals(4, DatosPrueba.count("entrada_digital"));
        assertEquals(96, stock(1));
        assertEquals(100, stock(2));
    }

    @Test
    void comprasConcurrentesNoSuperanElLimiteDeCuatro() throws Exception {
        CarritoCompra primero = carrito(1, List.of(1), List.of(3), null, false);
        CarritoCompra segundo = carrito(1, List.of(2), List.of(3), null, false);
        ExecutorService ejecutor = Executors.newFixedThreadPool(2);
        CountDownLatch inicio = new CountDownLatch(1);
        try {
            Future<SQLException> a = ejecutor.submit(() -> confirmarConcurrente(inicio, primero));
            Future<SQLException> b = ejecutor.submit(() -> confirmarConcurrente(inicio, segundo));
            inicio.countDown();
            SQLException errorA = a.get(10, TimeUnit.SECONDS);
            SQLException errorB = b.get(10, TimeUnit.SECONDS);
            assertEquals(1, (errorA == null ? 1 : 0) + (errorB == null ? 1 : 0));
            SQLException rechazado = errorA == null ? errorB : errorA;
            assertNotNull(rechazado);
            assertTrue(rechazado.getMessage().contains("cuatro entradas"));
            assertEquals(1, DatosPrueba.count("transaccion_compra"));
            assertEquals(3, DatosPrueba.count("entrada_digital"));
            assertEquals(97, stock(errorA == null ? 1 : 2));
            assertEquals(100, stock(errorA == null ? 2 : 1));
        } finally {
            ejecutor.shutdownNow();
        }
    }

    @Test
    void pagoRechazadoNoCreaEntradasNiDescuentaCupos() throws Exception {
        assertThrows(SQLException.class,
                () -> compras.confirmar(1, carrito(1, List.of(1), List.of(2), null, true)));
        assertEquals(0, DatosPrueba.count("transaccion_compra"));
        assertEquals(0, DatosPrueba.count("entrada_digital"));
        assertEquals(0, DatosPrueba.count("metodo_pago"));
        assertEquals(100, stock(1));
    }

    @Test
    void devolucionValidaAnulaEntradasYRecuperaCupoConPenalizacion() throws Exception {
        CompraConfirmada compra = comprar(1, List.of(1), List.of(2), null, false);
        var resultado = devoluciones.devolver(1, compra.getId());
        assertFalse(resultado.yaDevuelta());
        assertEquals(0, new BigDecimal("180.00").compareTo(resultado.importeReembolso()));
        assertEquals("DEVUELTA", texto("SELECT estado FROM transaccion_compra WHERE id=?", compra.getId()));
        assertEquals(2, scalar("SELECT COUNT(*) FROM entrada_digital e JOIN detalle_transaccion d "
                + "ON d.entrada_id=e.id WHERE d.transaccion_id=? AND e.estado_uso='ANULADA'", compra.getId()));
        assertEquals(100, stock(1));
    }

    @Test
    void devolucionRepetidaNoDuplicaReembolsoNiRestauraCupoOtraVez() throws Exception {
        CompraConfirmada compra = comprar(1, List.of(1), List.of(2), null, false);
        var primera = devoluciones.devolver(1, compra.getId());
        var segunda = devoluciones.devolver(1, compra.getId());
        assertFalse(primera.yaDevuelta());
        assertTrue(segunda.yaDevuelta());
        assertEquals(primera.importeReembolso(), segunda.importeReembolso());
        assertEquals(100, stock(1));
        assertEquals(1, scalar("SELECT COUNT(*) FROM transaccion_compra WHERE id=? AND estado='DEVUELTA'", compra.getId()));
    }

    @Test
    void rechazaDevolucionDeCompraAjenaSinCambiarLaCompra() throws Exception {
        CompraConfirmada compra = comprar(1, List.of(1), List.of(1), null, false);
        assertThrows(SQLException.class, () -> devoluciones.devolver(2, compra.getId()));
        assertEquals("CONFIRMADA", texto("SELECT estado FROM transaccion_compra WHERE id=?", compra.getId()));
        assertEquals(99, stock(1));
    }

    @Test
    void rechazaDevolucionSiUnaEntradaFueUtilizada() throws Exception {
        CompraConfirmada compra = comprar(1, List.of(1), List.of(2), null, false);
        actualizar("UPDATE entrada_digital e JOIN detalle_transaccion d ON d.entrada_id=e.id "
                + "SET e.estado_uso='UTILIZADA' WHERE d.transaccion_id=" + compra.getId() + " LIMIT 1");
        assertThrows(SQLException.class, () -> devoluciones.devolver(1, compra.getId()));
        assertEquals("CONFIRMADA", texto("SELECT estado FROM transaccion_compra WHERE id=?", compra.getId()));
        assertEquals(98, stock(1));
        assertEquals(1, scalar("SELECT COUNT(*) FROM entrada_digital e JOIN detalle_transaccion d "
                + "ON d.entrada_id=e.id WHERE d.transaccion_id=? AND e.estado_uso='UTILIZADA'", compra.getId()));
    }

    @Test
    void rechazaDevolucionFueraDelPlazoConfigurado() throws Exception {
        CompraConfirmada compra = comprar(1, List.of(1), List.of(1), null, false);
        actualizar("UPDATE evento_cultural SET fecha=DATE_ADD(UTC_TIMESTAMP(),INTERVAL 5 DAY) WHERE id=1");
        assertThrows(SQLException.class, () -> devoluciones.devolver(1, compra.getId()));
        assertEquals("CONFIRMADA", texto("SELECT estado FROM transaccion_compra WHERE id=?", compra.getId()));
        assertEquals(99, stock(1));
    }

    @Test
    void unFalloDuranteElPagoRevierteTodaLaCompra() throws Exception {
        try (Connection c = conexiones.abrir(); Statement s = c.createStatement()) {
            s.execute("CREATE TRIGGER ethan_falla_pago BEFORE INSERT ON metodo_pago "
                    + "FOR EACH ROW SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Fallo inducido de prueba'");
        }
        try {
            assertThrows(SQLException.class,
                    () -> compras.confirmar(1, carrito(1, List.of(1), List.of(2), null, false)));
            assertEquals(0, DatosPrueba.count("transaccion_compra"));
            assertEquals(0, DatosPrueba.count("entrada_digital"));
            assertEquals(100, stock(1));
        } finally {
            try (Connection c = conexiones.abrir(); Statement s = c.createStatement()) {
                s.execute("DROP TRIGGER ethan_falla_pago");
            }
        }
    }

    @Test
    void rollbackDeDevolucionNoDejaCuposParciales() throws Exception {
        CompraConfirmada compra = comprar(1, List.of(1), List.of(2), null, false);
        try (Connection c = conexiones.abrir(); Statement s = c.createStatement()) {
            s.execute("CREATE TRIGGER ethan_falla_devolucion BEFORE UPDATE ON transaccion_compra "
                    + "FOR EACH ROW BEGIN IF NEW.estado='DEVUELTA' THEN "
                    + "SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Fallo inducido de prueba'; END IF; END");
        }
        try {
            assertThrows(SQLException.class, () -> devoluciones.devolver(1, compra.getId()));
            assertEquals("CONFIRMADA", texto("SELECT estado FROM transaccion_compra WHERE id=?", compra.getId()));
            assertEquals(98, stock(1));
            assertEquals(2, scalar("SELECT COUNT(*) FROM entrada_digital e JOIN detalle_transaccion d "
                    + "ON d.entrada_id=e.id WHERE d.transaccion_id=? AND e.estado_uso='DISPONIBLE'", compra.getId()));
        } finally {
            try (Connection c = conexiones.abrir(); Statement s = c.createStatement()) {
                s.execute("DROP TRIGGER ethan_falla_devolucion");
            }
        }
    }

    @Test
    void loginDeAdministracionExigeRolAdminYContraseñaValida() throws Exception {
        try (Connection c = conexiones.abrir();
             PreparedStatement p = c.prepareStatement("INSERT INTO administrador_plataforma"
                     + "(id,username,contrasena_hash,nivel_acceso,estado) VALUES(1,'ethan.admin',?,'ADMIN','ACTIVO')")) {
            p.setString(1, PasswordUtil.crear("EthanAdminDemo2026!".toCharArray()));
            p.executeUpdate();
        }
        ServicioAutenticacion autenticacion = new ServicioAutenticacion(conexiones);
        assertTrue(autenticacion.autenticarAdministrador("ethan.admin", "EthanAdminDemo2026!".toCharArray()).isPresent());
        assertTrue(autenticacion.autenticarAdministrador("ethan.admin", "incorrecta".toCharArray()).isEmpty());
        assertTrue(autenticacion.autenticarPersonal("ethan.admin", "EthanAdminDemo2026!".toCharArray()).isEmpty());
    }

    private CompraConfirmada comprar(int clienteId, List<Integer> categorias, List<Integer> cantidades,
                                     String cupon, boolean pagoRechazado) throws SQLException {
        return compras.confirmar(clienteId, carrito(clienteId, categorias, cantidades, cupon, pagoRechazado));
    }

    private SQLException confirmarConcurrente(CountDownLatch inicio, CarritoCompra carrito) throws Exception {
        inicio.await();
        try {
            compras.confirmar(1, carrito);
            return null;
        } catch (SQLException e) {
            return e;
        }
    }

    private static CarritoCompra carrito(int clienteId, List<Integer> categorias, List<Integer> cantidades,
                                         String cupon, boolean pagoRechazado) {
        List<LineaCarrito> lineas = new ArrayList<>();
        int total = 0;
        for (int i = 0; i < categorias.size(); i++) {
            lineas.add(new LineaCarrito(categorias.get(i), "Zona de prueba", cantidades.get(i), "0.00"));
            total += cantidades.get(i);
        }
        CarritoCompra carrito = new CarritoCompra(1, "Evento de prueba", lineas);
        List<NominacionAsistente> asistentes = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            asistentes.add(new NominacionAsistente(String.format("%08d", 10000000 + clienteId * 100 + i),
                    "Asistente " + i, "Prueba"));
        }
        carrito.setNominaciones(asistentes);
        carrito.setCodigoCupon(cupon);
        carrito.setPagoRechazado(pagoRechazado);
        return carrito;
    }

    private void insertarCupon(String codigo, String porcentaje, LocalDateTime expiracion, String estado) throws Exception {
        try (Connection c = conexiones.abrir();
             PreparedStatement p = c.prepareStatement("INSERT INTO cupon_descuento"
                     + "(codigo_cupon,porcentaje_descuento,fecha_expiracion,estado) VALUES(?,?,?,?)")) {
            p.setString(1, codigo);
            p.setBigDecimal(2, new BigDecimal(porcentaje));
            p.setTimestamp(3, Timestamp.valueOf(expiracion));
            p.setString(4, estado);
            p.executeUpdate();
        }
    }

    private int stock(int categoriaId) throws Exception {
        return scalar("SELECT aforo_disponible FROM categoria_asiento WHERE id=?", categoriaId);
    }

    private int scalar(String sql, int id) throws Exception {
        try (Connection c = conexiones.abrir(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            try (ResultSet r = p.executeQuery()) {
                assertTrue(r.next());
                return r.getInt(1);
            }
        }
    }

    private BigDecimal decimal(String sql, int id) throws Exception {
        try (Connection c = conexiones.abrir(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            try (ResultSet r = p.executeQuery()) {
                assertTrue(r.next());
                return r.getBigDecimal(1);
            }
        }
    }

    private String texto(String sql, int id) throws Exception {
        try (Connection c = conexiones.abrir(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            try (ResultSet r = p.executeQuery()) {
                assertTrue(r.next());
                return r.getString(1);
            }
        }
    }

    private void actualizar(String sql) throws Exception {
        try (Connection c = conexiones.abrir(); Statement s = c.createStatement()) {
            s.executeUpdate(sql);
        }
    }
}
