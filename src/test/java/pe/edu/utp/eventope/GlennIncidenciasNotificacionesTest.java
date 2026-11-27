package pe.edu.utp.eventope;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.dto.*;
import pe.edu.utp.eventope.service.*;
import pe.edu.utp.eventope.util.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Módulo de Glenn: incidencias, ingreso manual y notificaciones. Se ejecuta contra MySQL/MariaDB real y borra
 * fixtures SOLO en el esquema llamado exactamente eventope_base_test (igual que BaseMySqlTest).
 */
@EnabledIfEnvironmentVariable(named = "EVENTOPE_TEST_DB_URL", matches = ".+")
class GlennIncidenciasNotificacionesTest {
    static final String DISPOSITIVO = "dispositivo-solo-para-pruebas-123456";
    static final Instant AHORA = Instant.parse("2026-10-07T12:00:01Z");
    static final Instant RESOLUCION = Instant.parse("2026-10-07T12:30:00Z");

    static final SesionUsuario CLIENTE_A = new SesionUsuario(1, SesionUsuario.Tipo.CLIENTE, SesionUsuario.Rol.CLIENTE, "Cliente A");
    static final SesionUsuario CLIENTE_B = new SesionUsuario(2, SesionUsuario.Tipo.CLIENTE, SesionUsuario.Rol.CLIENTE, "Cliente B");
    static final SesionUsuario GUARDIA = new SesionUsuario(1, SesionUsuario.Tipo.PERSONAL, SesionUsuario.Rol.VALIDACION, "guardia1");
    static final SesionUsuario ADMIN = new SesionUsuario(3, SesionUsuario.Tipo.PERSONAL, SesionUsuario.Rol.ADMIN, "admin1");
    static final SesionUsuario SOPORTE = new SesionUsuario(4, SesionUsuario.Tipo.PERSONAL, SesionUsuario.Rol.SOPORTE, "soporte1");
    static final SesionUsuario ADMIN_INACTIVO = new SesionUsuario(5, SesionUsuario.Tipo.PERSONAL, SesionUsuario.Rol.ADMIN, "admin_inactivo");
    static final SesionUsuario ADMIN_2 = new SesionUsuario(6, SesionUsuario.Tipo.PERSONAL, SesionUsuario.Rol.ADMIN, "admin2");

    ProveedorConexion conexiones;
    CifradoSecretos cifrado;
    ServicioQr qr;
    ServicioAcceso acceso;
    ServicioIncidencias incidencias;
    ServicioIngresoManual manual;
    ServicioNotificaciones notificaciones;

    // ------------------------------------------------------------------ fixtures

    @BeforeEach
    void preparar() throws Exception {
        conexiones = () -> DriverManager.getConnection(System.getenv("EVENTOPE_TEST_DB_URL"),
                System.getenv().getOrDefault("EVENTOPE_TEST_DB_USER", "root"), System.getenv().getOrDefault("EVENTOPE_TEST_DB_PASSWORD", ""));
        cifrado = new CifradoSecretos(Base64.getEncoder().encodeToString(Totp.nuevaClave()));
        qr = new ServicioQr(conexiones, cifrado, Clock.fixed(AHORA, ZoneOffset.UTC));
        acceso = new ServicioAcceso(conexiones, cifrado, Clock.fixed(AHORA, ZoneOffset.UTC));
        incidencias = new ServicioIncidencias(conexiones, Clock.fixed(RESOLUCION, ZoneOffset.UTC));
        manual = new ServicioIngresoManual(conexiones, Clock.fixed(RESOLUCION, ZoneOffset.UTC));
        notificaciones = new ServicioNotificaciones(conexiones);
        try (Connection c = conexiones.abrir(); Statement s = c.createStatement()) {
            if (!"eventope_base_test".equals(c.getCatalog())) throw new IllegalStateException("Se rechaza borrar un esquema distinto de eventope_base_test");
            for (String t : List.of("notificacion_usuario", "reclamo_soporte", "incidencia_acceso", "control_acceso_puerta", "dispositivo_validacion",
                    "historial_rotacion_qr", "ticket_qr_dinamico", "boleto_nominado", "comprobante_pago", "metodo_pago", "detalle_transaccion",
                    "entrada_digital", "transaccion_compra", "politica_devolucion", "categoria_asiento", "preferencias_musicales", "perfil_usuario",
                    "evento_cultural", "cupon_descuento", "administrador_plataforma", "usuario_cliente", "artista", "local_concierto", "organizador_evento")) {
                s.executeUpdate("DELETE FROM " + t);
            }
            s.executeUpdate("INSERT INTO usuario_cliente(id,nombres,apellidos,dni,email,contrasena_hash) VALUES (1,'A','Prueba','10000001','a@example.invalid','hash-no-login'),(2,'B','Prueba','10000002','b@example.invalid','hash-no-login')");
            s.executeUpdate("INSERT INTO administrador_plataforma(id,username,contrasena_hash,nivel_acceso,estado) VALUES "
                    + "(1,'guardia1','hash-no-login','VALIDACION','ACTIVO'),(2,'guardia2','hash-no-login','VALIDACION','ACTIVO'),"
                    + "(3,'admin1','hash-no-login','ADMIN','ACTIVO'),(4,'soporte1','hash-no-login','SOPORTE','ACTIVO'),"
                    + "(5,'admin_inactivo','hash-no-login','ADMIN','INACTIVO'),(6,'admin2','hash-no-login','ADMIN','ACTIVO')");
            s.executeUpdate("INSERT INTO organizador_evento(id,ruc,razon_social,email_contacto) VALUES(1,'20000000001','Demo','demo@example.invalid')");
            s.executeUpdate("INSERT INTO local_concierto(id,nombre_local,direccion,capacidad_max,ciudad) VALUES(1,'Local','Demo',100,'Lima')");
            s.executeUpdate("INSERT INTO evento_cultural(id,nombre,tipo_evento,fecha,aforo,local_id,organizador_id,estado) VALUES(1,'Evento Demo','CONCIERTO','2026-11-01 20:00:00',100,1,1,'PUBLICADO'),(2,'Otro Evento','CONCIERTO','2026-11-01 20:00:00',100,1,1,'PUBLICADO')");
            s.executeUpdate("INSERT INTO categoria_asiento(id,evento_id,nombre_zona,precio_base,aforo_total,aforo_disponible) VALUES(1,1,'General',100,100,100)");
            try (PreparedStatement p = c.prepareStatement("INSERT INTO dispositivo_validacion(id,operador_id,evento_id,nombre_puerta,token_acceso_hash) VALUES(1,1,1,'Norte',?)")) {
                p.setString(1, HashUtil.sha256(DISPOSITIVO));
                p.executeUpdate();
            }
        }
    }

    static String codigo(int id) { return String.format("00000000-0000-4000-8000-%012d", id); }
    static String dni(int id) { return String.format("%08d", 20000000 + id); }

    void entrada(int id, int comprador) throws Exception {
        sql("INSERT INTO entrada_digital(id,codigo_entrada,evento_id,categoria_id,comprador_id) VALUES(" + id + ",'" + codigo(id) + "',1,1," + comprador + ")");
        sql("INSERT INTO boleto_nominado(entrada_id,dni_asistente,nombres_asistente,apellidos_asistente) VALUES(" + id + ",'" + dni(id) + "','Asistente','" + id + "')");
    }

    static SesionUsuario dueno(int comprador) { return comprador == 1 ? CLIENTE_A : CLIENTE_B; }

    String token(int entradaId, int comprador) throws Exception { return qr.obtener(dueno(comprador), entradaId).getContenido(); }

    ResultadoAccesoDTO escanear(String contenido, String dni) throws Exception {
        return acceso.validar(GUARDIA, 1, DISPOSITIVO, UUID.randomUUID().toString(), contenido, dni);
    }

    /** Provoca una denegación por QR vencido de una entrada nueva y devuelve el id de la incidencia generada. */
    int incidenciaVencida(int entradaId, int comprador) throws Exception {
        entrada(entradaId, comprador);
        String t = token(entradaId, comprador);
        var tarde = new ServicioAcceso(conexiones, cifrado, Clock.fixed(AHORA.plusSeconds(60), ZoneOffset.UTC));
        var r = tarde.validar(GUARDIA, 1, DISPOSITIVO, UUID.randomUUID().toString(), t, dni(entradaId));
        assertFalse(r.isPermitido());
        assertEquals("QR_VENCIDO_O_FUERA_DE_INTERVALO", r.getMotivo());
        return incidenciaDeControl(r.getControlId());
    }

    int incidenciaDeControl(int controlId) throws Exception { return (int) valor("SELECT id FROM incidencia_acceso WHERE control_id=" + controlId); }

    void sql(String texto) throws Exception { try (Connection c = conexiones.abrir(); Statement s = c.createStatement()) { s.executeUpdate(texto); } }

    Object valor(String consulta) throws Exception {
        try (Connection c = conexiones.abrir(); Statement s = c.createStatement(); ResultSet r = s.executeQuery(consulta)) {
            assertTrue(r.next(), "Sin filas: " + consulta);
            return r.getObject(1);
        }
    }

    int cuenta(String tabla, String condicion) throws Exception { return ((Number) valor("SELECT COUNT(*) FROM " + tabla + " WHERE " + condicion)).intValue(); }

    // ================================================================== NOTIFICACIONES

    @Test
    void denegacionNotificaSoloAlDuenoYSinDatosSensibles() throws Exception {
        entrada(1, 1);
        String t = token(1, 1);
        assertFalse(escanear(t, "99999999").isPermitido());
        assertEquals(1, cuenta("notificacion_usuario", "cliente_id=1 AND titulo_mensaje LIKE 'Seguridad:%'"));
        assertEquals(0, cuenta("notificacion_usuario", "cliente_id=2"));
        String contenido = (String) valor("SELECT contenido FROM notificacion_usuario WHERE cliente_id=1");
        assertFalse(contenido.contains(t), "El aviso no debe contener el contenido del QR");
        assertFalse(contenido.contains("99999999") || contenido.contains(dni(1)), "El aviso no debe contener DNI");
        assertFalse(contenido.contains(codigo(1)), "El aviso no debe contener el código de la entrada");
    }

    @Test
    void ingresoPermitidoNotificaValidacionAlDueno() throws Exception {
        entrada(1, 1);
        assertTrue(escanear(token(1, 1), dni(1)).isPermitido());
        assertEquals(1, cuenta("notificacion_usuario", "cliente_id=1 AND titulo_mensaje LIKE 'Validación:%'"));
        assertEquals(0, cuenta("notificacion_usuario", "cliente_id=2"));
    }

    @Test
    void intentoSinEntradaIdentificadaNoGeneraAviso() throws Exception {
        String desconocido = new QrPayload(UUID.randomUUID().toString(), Totp.intervalo(AHORA), "12345678").codificar();
        assertFalse(escanear(desconocido, "10000001").isPermitido());
        assertEquals(0, cuenta("notificacion_usuario", "1=1"));
        assertEquals(1, cuenta("incidencia_acceso", "1=1"), "La denegación sí deja incidencia");
    }

    @Test
    void bandejaSoloMuestraYModificaLasPropias() throws Exception {
        sql("INSERT INTO notificacion_usuario(id,cliente_id,titulo_mensaje,contenido) VALUES (1,1,'Compra: a','de A'),(2,1,'Seguridad: a','de A 2'),(3,2,'Compra: b','de B')");
        var deA = notificaciones.listar(CLIENTE_A, false);
        assertEquals(2, deA.size());
        assertTrue(deA.stream().noneMatch(n -> n.getContenido().equals("de B")));
        assertEquals(1, notificaciones.listar(CLIENTE_B, false).size());

        assertFalse(notificaciones.marcarLeida(CLIENTE_A, 3), "No se puede marcar una notificación ajena");
        assertEquals(0, cuenta("notificacion_usuario", "id=3 AND leido=TRUE"));
        assertFalse(notificaciones.marcarLeida(CLIENTE_A, 999), "Una inexistente responde igual que una ajena");

        assertTrue(notificaciones.marcarLeida(CLIENTE_A, 1));
        assertEquals(1, notificaciones.contarNoLeidas(CLIENTE_A));
        assertEquals(1, notificaciones.listar(CLIENTE_A, true).size());

        notificaciones.marcarTodasLeidas(CLIENTE_A);
        assertEquals(0, notificaciones.contarNoLeidas(CLIENTE_A));
        assertEquals(1, notificaciones.contarNoLeidas(CLIENTE_B), "Marcar todas no toca las de otro cliente");
    }

    @Test
    void laFechaDelAvisoSeGuardaEnUtcAunqueLaSesionTengaOtraZona() throws Exception {
        try (Connection c = conexiones.abrir(); Statement s = c.createStatement()) {
            s.execute("SET time_zone = '-05:00'");
            c.setAutoCommit(false);
            ServicioNotificaciones.notificarCompra(c, 1, "Evento Demo", "EP-000009", 1, new BigDecimal("100"));
            c.commit();
            try (ResultSet r = s.executeQuery("SELECT ABS(TIMESTAMPDIFF(MINUTE, fecha_envio, UTC_TIMESTAMP())) FROM notificacion_usuario")) {
                assertTrue(r.next());
                assertTrue(r.getInt(1) <= 1, "fecha_envio debe estar en UTC, diferencia en minutos: " + r.getInt(1));
            }
        }
    }

    @Test
    void tipoSeDerivaDelPrefijoDelTitulo() throws Exception {
        sql("INSERT INTO notificacion_usuario(id,cliente_id,titulo_mensaje,contenido) VALUES (1,1,'Compra: Compra confirmada','x'),(2,1,'Validación: Entrada validada','x'),(3,1,'Seguridad: Intento','x'),(4,1,'Sin prefijo','x')");
        Map<String, String> porTitulo = new HashMap<>();
        notificaciones.listar(CLIENTE_A, false).forEach(n -> porTitulo.put(n.getTitulo(), n.getTipoEtiqueta()));
        assertEquals("Compra", porTitulo.get("Compra confirmada"));
        assertEquals("Validación", porTitulo.get("Entrada validada"));
        assertEquals("Seguridad", porTitulo.get("Intento"));
        assertEquals("Aviso", porTitulo.get("Sin prefijo"));
    }

    @Test
    void personalNoUsaLaBandejaDeClientes() {
        assertThrows(SecurityException.class, () -> notificaciones.listar(GUARDIA, false));
        assertThrows(SecurityException.class, () -> notificaciones.marcarLeida(ADMIN, 1));
        assertThrows(SecurityException.class, () -> notificaciones.contarNoLeidas(SOPORTE));
    }

    @Test
    void fallaDelAvisoNoAnulaElIngreso() throws Exception {
        entrada(1, 1);
        String t = token(1, 1);
        try (Connection c = conexiones.abrir(); Statement s = c.createStatement()) {
            s.execute("CREATE TRIGGER falla_aviso BEFORE INSERT ON notificacion_usuario FOR EACH ROW SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Fallo inducido del aviso'");
        }
        try {
            var r = escanear(t, dni(1));
            assertTrue(r.isPermitido(), "El ingreso debe seguir adelante aunque el aviso falle");
            assertEquals("UTILIZADA", valor("SELECT estado_uso FROM entrada_digital WHERE id=1"));
            assertEquals(1, cuenta("control_acceso_puerta", "estado_acceso='PERMITIDO'"));
            assertEquals(0, cuenta("notificacion_usuario", "1=1"));
        } finally {
            try (Connection c = conexiones.abrir(); Statement s = c.createStatement()) { s.execute("DROP TRIGGER falla_aviso"); }
        }
    }

    @Test
    void avisoDeCompraEsIdempotente() throws Exception {
        try (Connection c = conexiones.abrir()) {
            c.setAutoCommit(false);
            ServicioNotificaciones.notificarCompra(c, 1, "Evento Demo", "EP-000001", 2, new BigDecimal("200"));
            ServicioNotificaciones.notificarCompra(c, 1, "Evento Demo", "EP-000001", 2, new BigDecimal("200"));
            c.commit();
        }
        assertEquals(1, cuenta("notificacion_usuario", "cliente_id=1 AND titulo_mensaje='Compra: Compra confirmada'"));
        String texto = (String) valor("SELECT contenido FROM notificacion_usuario WHERE cliente_id=1");
        assertTrue(texto.contains("EP-000001") && texto.contains("2 entradas") && texto.contains("200.00"), texto);
    }

    @Test
    void compraRealGeneraUnAvisoYElReintentoNoLoDuplica() throws Exception {
        var compra = new ServicioCompra(conexiones);
        var carrito = new CarritoCompra(1, "Evento Demo", List.of(new LineaCarrito(1, "General", 1, "100.00")));
        carrito.setNominaciones(List.of(new NominacionAsistente("30000001", "Persona", "Prueba")));
        var primera = compra.confirmar(1, carrito);
        var reintento = compra.confirmar(1, carrito);
        assertEquals(primera.getId(), reintento.getId());
        assertEquals(1, cuenta("transaccion_compra", "1=1"));
        assertEquals(1, cuenta("notificacion_usuario", "cliente_id=1 AND titulo_mensaje LIKE 'Compra:%'"));
        assertEquals(0, cuenta("notificacion_usuario", "cliente_id=2"));
    }

    // ================================================================== INCIDENCIAS

    @Test
    void denegacionRegistraSeveridadYDescripcionSegunElMotivo() throws Exception {
        entrada(1, 1);
        String t = token(1, 1);
        escanear(t, "99999999");                                        // identidad: ALTA
        int vencida = incidenciaVencida(2, 1);                           // vencido: BAJA
        assertEquals("ALTA", valor("SELECT severidad FROM incidencia_acceso WHERE tipo_incidencia='IDENTIDAD_NO_COINCIDE'"));
        assertEquals("BAJA", valor("SELECT severidad FROM incidencia_acceso WHERE id=" + vencida));
        String d = (String) valor("SELECT descripcion FROM incidencia_acceso WHERE id=" + vencida);
        assertTrue(d.contains("vencido"), d);
    }

    @Test
    void resolverGuardaQuienCuandoYPorQue() throws Exception {
        int id = incidenciaVencida(1, 1);
        incidencias.resolver(ADMIN, id, "RESUELTA", "  El asistente mostró el QR actualizado y se verificó su DNI.  ");
        try (Connection c = conexiones.abrir(); Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT estado_resolucion,resuelta_por_id,fecha_resolucion,motivo_resolucion FROM incidencia_acceso WHERE id=" + id)) {
            assertTrue(r.next());
            assertEquals("RESUELTA", r.getString(1));
            assertEquals(3, r.getInt(2), "Quién");
            assertEquals(LocalDateTime.ofInstant(RESOLUCION, ZoneOffset.UTC), r.getObject(3, LocalDateTime.class), "Cuándo (UTC, del reloj del servidor)");
            assertEquals("El asistente mostró el QR actualizado y se verificó su DNI.", r.getString(4), "Por qué (sin espacios sobrantes)");
        }
        var dto = incidencias.detalle(ADMIN, id).orElseThrow();
        assertEquals("admin1", dto.getResueltaPor());
        assertEquals("RESUELTA", dto.getEstado());
        assertFalse(dto.getFechaResolucion().isBlank());
    }

    @Test
    void soporteTambienResuelveYDescartaPeroNoElGuardiaNiElClienteNiUnaCuentaInactiva() throws Exception {
        int a = incidenciaVencida(1, 1), b = incidenciaVencida(2, 1);
        incidencias.resolver(SOPORTE, a, "DESCARTADA", "Falsa alarma: el guardia leyó dos veces el mismo QR.");
        assertEquals("DESCARTADA", valor("SELECT estado_resolucion FROM incidencia_acceso WHERE id=" + a));
        assertEquals(4, valor("SELECT resuelta_por_id FROM incidencia_acceso WHERE id=" + a));

        assertThrows(SecurityException.class, () -> incidencias.resolver(GUARDIA, b, "RESUELTA", "Intento de un guardia sin permiso."));
        assertThrows(SecurityException.class, () -> incidencias.resolver(ADMIN_INACTIVO, b, "RESUELTA", "Cuenta desactivada no debe poder."));
        assertThrows(SecurityException.class, () -> incidencias.listar(GUARDIA, "ABIERTA", null, 1));
        assertThrows(SecurityException.class, () -> incidencias.detalle(CLIENTE_A, b));
        assertEquals("ABIERTA", valor("SELECT estado_resolucion FROM incidencia_acceso WHERE id=" + b));
    }

    @Test
    void unaSesionDesactualizadaNoSirve() throws Exception {
        int id = incidenciaVencida(1, 1);
        sql("UPDATE administrador_plataforma SET estado='INACTIVO' WHERE id=3");
        assertThrows(SecurityException.class, () -> incidencias.resolver(ADMIN, id, "RESUELTA", "La cuenta se desactivó tras iniciar sesión."));
        sql("UPDATE administrador_plataforma SET estado='ACTIVO', nivel_acceso='SOPORTE' WHERE id=3");
        assertThrows(SecurityException.class, () -> manual.autorizar(ADMIN, id, "El rol cambió a soporte y ya no es administrador."));
        assertEquals("ABIERTA", valor("SELECT estado_resolucion FROM incidencia_acceso WHERE id=" + id));
    }

    @Test
    void noSePuedeResolverDosVecesNiSobrescribirLaPrimera() throws Exception {
        int id = incidenciaVencida(1, 1);
        incidencias.resolver(ADMIN, id, "RESUELTA", "Primera resolución con su motivo.");
        assertThrows(IllegalStateException.class, () -> incidencias.resolver(SOPORTE, id, "DESCARTADA", "Segundo intento que no debe aplicarse."));
        assertEquals(3, valor("SELECT resuelta_por_id FROM incidencia_acceso WHERE id=" + id));
        assertEquals("Primera resolución con su motivo.", valor("SELECT motivo_resolucion FROM incidencia_acceso WHERE id=" + id));
    }

    @Test
    void motivoYDecisionSonObligatoriosYAcotados() throws Exception {
        int id = incidenciaVencida(1, 1);
        assertThrows(IllegalArgumentException.class, () -> incidencias.resolver(ADMIN, id, "RESUELTA", null));
        assertThrows(IllegalArgumentException.class, () -> incidencias.resolver(ADMIN, id, "RESUELTA", "   corto   "));
        assertThrows(IllegalArgumentException.class, () -> incidencias.resolver(ADMIN, id, "RESUELTA", "x".repeat(201)));
        assertThrows(IllegalArgumentException.class, () -> incidencias.resolver(ADMIN, id, "RESUELTA", "Motivo con carácter de control \u0007 inválido."));
        assertThrows(IllegalArgumentException.class, () -> incidencias.resolver(ADMIN, id, "RESUELTA", "Motivo con byte nulo \u0000 inválido."));
        assertThrows(IllegalArgumentException.class, () -> incidencias.resolver(ADMIN, id, "BORRADA", "Decisión que no existe en la lista."));
        assertThrows(IllegalArgumentException.class, () -> incidencias.resolver(ADMIN, 99999, "RESUELTA", "Incidencia inexistente en la base."));
        assertEquals("ABIERTA", valor("SELECT estado_resolucion FROM incidencia_acceso WHERE id=" + id));
        assertNull(valor("SELECT resuelta_por_id FROM incidencia_acceso WHERE id=" + id));
        // Los saltos de línea y espacios repetidos no se rechazan: se normalizan a un solo espacio.
        incidencias.resolver(ADMIN, id, "RESUELTA", "Motivo escrito en\n dos líneas,   con espacios.");
        assertEquals("Motivo escrito en dos líneas, con espacios.", valor("SELECT motivo_resolucion FROM incidencia_acceso WHERE id=" + id));
    }

    @Test
    void listarFiltraPorEstadoYSeveridadYPagina() throws Exception {
        for (int i = 1; i <= 25; i++) incidenciaVencida(i, 1);           // 25 abiertas BAJA
        entrada(100, 1);
        escanear(token(100, 1), "99999999");                              // 1 ALTA
        int primera = (int) valor("SELECT MIN(id) FROM incidencia_acceso");
        incidencias.resolver(ADMIN, primera, "RESUELTA", "Resuelta para probar el filtro de estado.");

        var abiertas = incidencias.listar(SOPORTE, "ABIERTA", null, 1);
        assertEquals(25, abiertas.getTotal());
        assertEquals(2, abiertas.getTotalPaginas());
        assertEquals(ServicioIncidencias.TAMANO_PAGINA, abiertas.getIncidencias().size());
        assertEquals("ALTA", abiertas.getIncidencias().get(0).getSeveridad(), "Las de mayor severidad van primero");
        assertEquals(5, incidencias.listar(SOPORTE, "ABIERTA", null, 2).getIncidencias().size());
        assertEquals(1, incidencias.listar(SOPORTE, "RESUELTA", null, 1).getTotal());
        assertEquals(1, incidencias.listar(SOPORTE, "TODAS", "ALTA", 1).getTotal());
        assertEquals(26, incidencias.listar(SOPORTE, "TODAS", "valor-inventado", 1).getTotal(), "Un filtro desconocido se ignora, no se concatena a SQL");
        assertEquals(2, incidencias.listar(SOPORTE, "ABIERTA", null, 999).getPagina(), "Una página fuera de rango se corrige");
    }

    // ================================================================== INGRESO MANUAL

    @Test
    void ingresoManualPermiteUnaSolaVezYRegistraLaAutorizacion() throws Exception {
        int id = incidenciaVencida(1, 1);
        var r = manual.autorizar(ADMIN, id, "Verifiqué el DNI físico; el QR venció por mala señal del teléfono.");

        assertEquals("UTILIZADA", valor("SELECT estado_uso FROM entrada_digital WHERE id=1"));
        try (Connection c = conexiones.abrir(); Statement s = c.createStatement();
             ResultSet k = s.executeQuery("SELECT estado_acceso,motivo,autorizado_por_id,operador_id,dispositivo_id,entrada_id FROM control_acceso_puerta WHERE id=" + r.getControlId())) {
            assertTrue(k.next());
            assertEquals("PERMITIDO", k.getString(1));
            assertEquals("INGRESO_MANUAL_INC_" + id, k.getString(2));
            assertEquals(3, k.getInt(3), "Queda registrado QUIÉN autorizó");
            assertEquals(1, k.getInt(4), "Se conserva el guardia de la puerta");
            assertEquals(1, k.getInt(5));
            assertEquals(1, k.getInt(6));
        }
        assertEquals("RESUELTA", valor("SELECT estado_resolucion FROM incidencia_acceso WHERE id=" + id));
        assertTrue(((String) valor("SELECT motivo_resolucion FROM incidencia_acceso WHERE id=" + id)).startsWith("Ingreso manual autorizado: Verifiqué"));
        assertEquals(3, valor("SELECT resuelta_por_id FROM incidencia_acceso WHERE id=" + id));
        assertEquals(1, cuenta("notificacion_usuario", "cliente_id=1 AND titulo_mensaje LIKE 'Validación: Ingreso autorizado%'"));

        // No hay segundo ingreso: ni por el mismo QR, ni por una segunda autorización manual.
        var otro = escanear(token0(1), dni(1));
        assertFalse(otro.isPermitido());
        assertEquals("YA_UTILIZADA", otro.getMotivo());
        assertThrows(IllegalStateException.class, () -> manual.autorizar(ADMIN_2, id, "Segunda autorización que no debe aplicarse."));
        assertEquals(1, cuenta("control_acceso_puerta", "entrada_id=1 AND estado_acceso='PERMITIDO'"));
    }

    /** QR válido de la entrada aunque ya esté utilizada: se arma directamente con la clave guardada (solo para la prueba). */
    String token0(int entradaId) throws Exception {
        try (Connection c = conexiones.abrir(); Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT clave_totp_cifrada FROM ticket_qr_dinamico WHERE entrada_id=" + entradaId)) {
            assertTrue(r.next());
            byte[] clave = cifrado.descifrar(r.getString(1), codigo(entradaId));
            return new QrPayload(codigo(entradaId), Totp.intervalo(AHORA), Totp.generar(clave, AHORA)).codificar();
        }
    }

    @Test
    void soloUnAdministradorActivoPuedeAutorizar() throws Exception {
        int id = incidenciaVencida(1, 1);
        for (SesionUsuario intruso : List.of(SOPORTE, GUARDIA, CLIENTE_A, ADMIN_INACTIVO)) {
            assertThrows(SecurityException.class, () -> manual.autorizar(intruso, id, "Intento sin permiso de administrador."), intruso.getNombre());
        }
        assertEquals("DISPONIBLE", valor("SELECT estado_uso FROM entrada_digital WHERE id=1"));
        assertEquals("ABIERTA", valor("SELECT estado_resolucion FROM incidencia_acceso WHERE id=" + id));
        assertEquals(0, cuenta("control_acceso_puerta", "estado_acceso='PERMITIDO'"));
    }

    @Test
    void laJustificacionEsObligatoria() throws Exception {
        int id = incidenciaVencida(1, 1);
        assertThrows(IllegalArgumentException.class, () -> manual.autorizar(ADMIN, id, null));
        assertThrows(IllegalArgumentException.class, () -> manual.autorizar(ADMIN, id, "   ok   "));
        assertThrows(IllegalArgumentException.class, () -> manual.autorizar(ADMIN, id, "y".repeat(171)));
        assertEquals("DISPONIBLE", valor("SELECT estado_uso FROM entrada_digital WHERE id=1"));
        assertEquals(0, cuenta("control_acceso_puerta", "estado_acceso='PERMITIDO'"));
    }

    @Test
    void unaEntradaAnuladaNuncaSeRecupera() throws Exception {
        int id = incidenciaVencida(1, 1);
        sql("UPDATE entrada_digital SET estado_uso='ANULADA' WHERE id=1");
        var e = assertThrows(IllegalStateException.class, () -> manual.autorizar(ADMIN, id, "Intento de recuperar una entrada anulada."));
        assertTrue(e.getMessage().toLowerCase().contains("anulada"), e.getMessage());
        assertEquals("ANULADA", valor("SELECT estado_uso FROM entrada_digital WHERE id=1"));
        assertEquals(0, cuenta("control_acceso_puerta", "estado_acceso='PERMITIDO'"));
        assertEquals("ABIERTA", valor("SELECT estado_resolucion FROM incidencia_acceso WHERE id=" + id));
    }

    @Test
    void unaEntradaYaUtilizadaNoAdmiteOtroIngreso() throws Exception {
        entrada(1, 1);
        String t = token(1, 1);
        assertEquals("IDENTIDAD_NO_COINCIDE", escanear(t, "99999999").getMotivo());     // incidencia abierta y recuperable
        assertTrue(escanear(t, dni(1)).isPermitido());                                    // luego entra legítimamente
        int id = (int) valor("SELECT id FROM incidencia_acceso WHERE tipo_incidencia='IDENTIDAD_NO_COINCIDE'");
        var e = assertThrows(IllegalStateException.class, () -> manual.autorizar(ADMIN, id, "Intento de doble ingreso con la misma entrada."));
        assertTrue(e.getMessage().toLowerCase().contains("utilizada"), e.getMessage());
        assertEquals(1, cuenta("control_acceso_puerta", "entrada_id=1 AND estado_acceso='PERMITIDO'"));
    }

    @Test
    void motivosDeFraudeODatoInexistenteNoAdmitenIngresoManual() throws Exception {
        entrada(1, 1);
        String t = token(1, 1);
        String alterado = t.substring(0, t.length() - 1) + (t.endsWith("0") ? "1" : "0");
        assertEquals("QR_ALTERADO", escanear(alterado, dni(1)).getMotivo());
        String desconocido = new QrPayload(UUID.randomUUID().toString(), Totp.intervalo(AHORA), "12345678").codificar();
        assertEquals("QR_DESCONOCIDO", escanear(desconocido, dni(1)).getMotivo());
        sql("UPDATE dispositivo_validacion SET evento_id=2 WHERE id=1");
        assertEquals("EVENTO_INCORRECTO", escanear(t, dni(1)).getMotivo());
        sql("UPDATE dispositivo_validacion SET evento_id=1 WHERE id=1");

        List<Integer> ids = new ArrayList<>();
        try (Connection c = conexiones.abrir(); Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT id FROM incidencia_acceso")) {
            while (r.next()) ids.add(r.getInt(1));
        }
        assertEquals(3, ids.size());
        for (int id : ids) {
            assertThrows(IllegalStateException.class, () -> manual.autorizar(ADMIN, id, "Intento sobre un motivo que no es recuperable."));
            var dto = incidencias.detalle(ADMIN, id).orElseThrow();
            assertFalse(dto.isIngresoManualPosible(), "La pantalla no debe ofrecer el botón: " + dto.getTipo());
            assertNotNull(dto.getRazonSinIngresoManual());
        }
        assertEquals("DISPONIBLE", valor("SELECT estado_uso FROM entrada_digital WHERE id=1"));
    }

    @Test
    void laPantallaSoloOfreceIngresoManualCuandoEsPosible() throws Exception {
        int id = incidenciaVencida(1, 1);
        assertTrue(incidencias.detalle(ADMIN, id).orElseThrow().isIngresoManualPosible());
        assertNull(incidencias.detalle(ADMIN, id).orElseThrow().getRazonSinIngresoManual());
        manual.autorizar(ADMIN, id, "Se verificó la identidad del asistente en persona.");
        assertFalse(incidencias.detalle(ADMIN, id).orElseThrow().isIngresoManualPosible());
    }

    @RepeatedTest(10)
    void dosAdministradoresSimultaneosAutorizanUnaSolaVez() throws Exception {
        int id = incidenciaVencida(1, 1);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch inicio = new CountDownLatch(1);
        try {
            Callable<String> uno = () -> intentar(inicio, ADMIN, id);
            Callable<String> dos = () -> intentar(inicio, ADMIN_2, id);
            Future<String> a = pool.submit(uno), b = pool.submit(dos);
            inicio.countDown();
            List<String> resultados = List.of(a.get(15, TimeUnit.SECONDS), b.get(15, TimeUnit.SECONDS));
            assertEquals(1, resultados.stream().filter("OK"::equals).count(), resultados.toString());
            assertEquals(1, resultados.stream().filter("RECHAZADO"::equals).count(), resultados.toString());
            assertEquals(1, cuenta("control_acceso_puerta", "entrada_id=1 AND estado_acceso='PERMITIDO'"));
            assertEquals("UTILIZADA", valor("SELECT estado_uso FROM entrada_digital WHERE id=1"));
            assertEquals(1, cuenta("notificacion_usuario", "titulo_mensaje LIKE 'Validación: Ingreso autorizado%'"));
        } finally {
            pool.shutdownNow();
        }
    }

    private String intentar(CountDownLatch inicio, SesionUsuario admin, int id) throws Exception {
        inicio.await();
        try {
            manual.autorizar(admin, id, "Autorización simultánea de prueba de concurrencia.");
            return "OK";
        } catch (IllegalStateException e) {
            return "RECHAZADO";
        }
    }

    @Test
    void siFallaElRegistroNadaQuedaAMedias() throws Exception {
        int id = incidenciaVencida(1, 1);
        try (Connection c = conexiones.abrir(); Statement s = c.createStatement()) {
            s.execute("CREATE TRIGGER falla_manual BEFORE INSERT ON control_acceso_puerta FOR EACH ROW BEGIN "
                    + "IF NEW.motivo LIKE 'INGRESO_MANUAL%' THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Fallo inducido'; END IF; END");
        }
        try {
            assertThrows(SQLException.class, () -> manual.autorizar(ADMIN, id, "Esta autorización debe revertirse por completo."));
            assertEquals("DISPONIBLE", valor("SELECT estado_uso FROM entrada_digital WHERE id=1"), "La entrada no debe quedar consumida");
            assertEquals("ABIERTA", valor("SELECT estado_resolucion FROM incidencia_acceso WHERE id=" + id));
            assertEquals(0, cuenta("control_acceso_puerta", "estado_acceso='PERMITIDO'"));
            assertEquals(0, cuenta("notificacion_usuario", "titulo_mensaje LIKE 'Validación:%'"));
        } finally {
            try (Connection c = conexiones.abrir(); Statement s = c.createStatement()) { s.execute("DROP TRIGGER falla_manual"); }
        }
        manual.autorizar(ADMIN, id, "Reintento correcto una vez retirado el fallo.");
        assertEquals("UTILIZADA", valor("SELECT estado_uso FROM entrada_digital WHERE id=1"));
    }

    // ================================================================== MATRIZ 5 / 5 / 5 / 5

    @Test
    void matrizDeQuinceQrSoloLosVigentesPermitenIngreso() throws Exception {
        Map<String, List<ResultadoAccesoDTO>> resultados = new LinkedHashMap<>();
        List<Integer> vigentes = new ArrayList<>(), vencidos = new ArrayList<>(), utilizados = new ArrayList<>(), alterados = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            vigentes.add(101 + i);
            vencidos.add(111 + i);
            utilizados.add(121 + i);
            alterados.add(131 + i);
        }
        Map<Integer, Integer> comprador = new HashMap<>();
        for (int id : concat(vigentes, vencidos, utilizados, alterados)) {
            int dueno = id % 2 == 0 ? 1 : 2;
            comprador.put(id, dueno);
            entrada(id, dueno);
        }

        var tarde = new ServicioAcceso(conexiones, cifrado, Clock.fixed(AHORA.plusSeconds(60), ZoneOffset.UTC));
        for (int id : vigentes) resultados.computeIfAbsent("vigente", k -> new ArrayList<>()).add(escanear(token(id, comprador.get(id)), dni(id)));
        for (int id : vencidos) {
            String t = token(id, comprador.get(id));
            resultados.computeIfAbsent("vencido", k -> new ArrayList<>()).add(tarde.validar(GUARDIA, 1, DISPOSITIVO, UUID.randomUUID().toString(), t, dni(id)));
        }
        for (int id : utilizados) {
            String t = token(id, comprador.get(id));
            sql("UPDATE entrada_digital SET estado_uso='UTILIZADA' WHERE id=" + id);
            resultados.computeIfAbsent("utilizado", k -> new ArrayList<>()).add(escanear(t, dni(id)));
        }
        for (int id : alterados) {
            String t = token(id, comprador.get(id));
            String malo = t.substring(0, t.length() - 1) + (t.endsWith("0") ? "1" : "0");
            resultados.computeIfAbsent("alterado", k -> new ArrayList<>()).add(escanear(malo, dni(id)));
        }

        resultados.get("vigente").forEach(r -> { assertTrue(r.isPermitido()); assertEquals("VALIDO", r.getMotivo()); });
        resultados.get("vencido").forEach(r -> { assertFalse(r.isPermitido()); assertEquals("QR_VENCIDO_O_FUERA_DE_INTERVALO", r.getMotivo()); });
        resultados.get("utilizado").forEach(r -> { assertFalse(r.isPermitido()); assertEquals("YA_UTILIZADA", r.getMotivo()); });
        resultados.get("alterado").forEach(r -> { assertFalse(r.isPermitido()); assertEquals("QR_ALTERADO", r.getMotivo()); });

        assertEquals(20, cuenta("control_acceso_puerta", "1=1"));
        assertEquals(5, cuenta("control_acceso_puerta", "estado_acceso='PERMITIDO'"), "Solo ingresan los 5 vigentes");
        assertEquals(15, cuenta("incidencia_acceso", "estado_resolucion='ABIERTA'"), "Cada denegación deja una incidencia abierta");
        assertEquals(5, cuenta("incidencia_acceso", "severidad='ALTA' AND tipo_incidencia='YA_UTILIZADA'"));
        assertEquals(5, cuenta("incidencia_acceso", "severidad='ALTA' AND tipo_incidencia='QR_ALTERADO'"));
        assertEquals(5, cuenta("incidencia_acceso", "severidad='BAJA' AND tipo_incidencia='QR_VENCIDO_O_FUERA_DE_INTERVALO'"));
        for (int id : vencidos) assertEquals("DISPONIBLE", valor("SELECT estado_uso FROM entrada_digital WHERE id=" + id), "Un QR vencido no consume la entrada");
        for (int id : alterados) assertEquals("DISPONIBLE", valor("SELECT estado_uso FROM entrada_digital WHERE id=" + id), "Un QR alterado no consume la entrada");
        assertEquals(5, cuenta("notificacion_usuario", "titulo_mensaje LIKE 'Validación:%'"));
        assertEquals(15, cuenta("notificacion_usuario", "titulo_mensaje LIKE 'Seguridad:%'"));
        for (int cliente : List.of(1, 2)) {
            assertEquals(((Number) valor("SELECT COUNT(*) FROM notificacion_usuario n WHERE n.cliente_id=" + cliente)).intValue(),
                    ((Number) valor("SELECT COUNT(*) FROM control_acceso_puerta k JOIN entrada_digital e ON e.id=k.entrada_id WHERE e.comprador_id=" + cliente)).intValue(),
                    "Cada aviso llegó al dueño de la entrada correspondiente");
        }
    }

    @SafeVarargs
    private static List<Integer> concat(List<Integer>... listas) {
        List<Integer> todo = new ArrayList<>();
        for (var l : listas) todo.addAll(l);
        return todo;
    }
}
