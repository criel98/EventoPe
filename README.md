# EventoPe — Semana 10

Aplicación de venta de entradas nominadas con QR dinámico TOTP. El alcance de Semana 10 comprende la experiencia del cliente y una pantalla de validación en puerta. La organización del trabajo y el estado de implementación se describen en `LEEME_PRIMERO.md`.

## Tecnología

Java 17, Maven 3.9, WAR, Tomcat 10.1, Jakarta Servlet 6, JSP/JSTL 3, JDBC y MySQL 8.0.16 o posterior. POM compilado y probado con JDK 26 usando `release 17`; pendiente comprobar despliegue final del equipo sobre JDK 17/Tomcat. No hay JPA/Hibernate.

## Preparación

1. Para un entorno nuevo, crear una base y ejecutar `sql/01_esquema.sql` y `sql/02_datos_demo.sql` según `docs/01_Workbench.md`.
2. Copiar `config/eventope.properties.example` como `config/eventope.local.properties`. La copia local está ignorada por Git. Rellenar URL, usuario y contraseña de su base.
3. Ejecutar `mvn compile`. Generar una clave maestra privada con:

```powershell
java -cp target/classes pe.edu.utp.eventope.util.GenerarClaveMaestra
```

4. Copiar el resultado únicamente a `totp.masterKey` del archivo local. Conservar la misma clave para la misma base: cambiarla hace ilegibles secretos TOTP ya creados. Cada entorno de desarrollo puede usar su base y clave. No subirla a GitHub ni incluirla en capturas.
5. Configurar en la JVM de Tomcat `-Deventope.config=C:/ruta/privada/eventope.local.properties`, o la variable `EVENTOPE_CONFIG` con esa ruta absoluta. No introducir la clave en el POM.
6. Instanciar `Configuracion.cargar()`, `ConexionBD`, `CifradoSecretos(config.requerido("totp.masterKey"))` y `Clock.systemUTC()` al construir servicios. No cargar la configuración por cada botón ni exponerla al navegador.

## Compilar y probar

```powershell
mvn clean verify
```

Sin configuración de pruebas MySQL se ejecutan las pruebas unitarias y se omiten las de integración de forma explícita. Para ejecutar también integración, crear un esquema separado cuyo nombre sea EXACTAMENTE `eventope_base_test`, cargar SOLO `01_esquema.sql` y usar:

```powershell
$env:EVENTOPE_TEST_DB_URL='jdbc:mysql://127.0.0.1:3306/eventope_base_test?connectionTimeZone=UTC&forceConnectionTimeZoneToSession=true'
$env:EVENTOPE_TEST_DB_USER='USUARIO_DE_PRUEBAS'
$env:EVENTOPE_TEST_DB_PASSWORD='CONTRASENA_LOCAL_DE_PRUEBAS'
mvn clean verify
```

Las pruebas de integración eliminan y reconstruyen fixtures SOLO en ese esquema. Nunca poner el nombre de la base demo ni de una base con datos reales. La clave maestra usada en esas pruebas es aleatoria y exclusiva de la prueba. No hace falta copiarla a configuración.

Resultados registrados: `docs/06_Verificacion.md`. El recorrido cliente → compra → QR → seguridad también se comprobó en navegador con datos ficticios.

## Pruebas de catálogo y cuentas

`CatalogoMySqlTest` y `CuentaMySqlTest` usan las mismas variables `EVENTOPE_TEST_DB_*` y el esquema exclusivo `eventope_base_test`. Comprueban visibilidad, filtros, registro, duplicados, concurrencia, rollback, edición propia y roles. No requieren un archivo de configuración de desarrollo. Los resultados del alcance se documentan en `docs/08_Daniel_Frank.md`.

## Despliegue

El paquete es `target/EventoPe.war`. Desplegarlo en Tomcat 10.1. Rutas disponibles: `/EventoPe/eventos`, `/EventoPe/eventos/detalle?id=10`, `/EventoPe/registro`, `/EventoPe/login`, `/EventoPe/cuenta/perfil` y `/EventoPe/personal/login`. `/EventoPe/salud` verifica despliegue HTTP. La compra, mis entradas, el boleto TOTP y el panel de validación están conectados. La selección es un paso interno de compra, sin carrito en el menú.

`WEB-INF/web.xml` marca la cookie de sesión como Secure: el entorno debe servir HTTPS para probar login. No desactivar esta protección para la demostración desde móviles. La cámara de un teléfono también debe probarse bajo HTTPS con certificado confiable por ese teléfono. Tener una dirección HTTP en la red local no garantiza acceso a cámara.

El catálogo y las cuentas se comprobaron en Tomcat 10.1 con HTTPS local. Se verificó el recorrido de compra/validación y lectura mediante vídeo de prueba. Falta el ensayo de cámara física en teléfono. Ver `docs/08_Daniel_Frank.md`.

## Credenciales ficticias incluidas

- Cliente A: `cliente.a@example.invalid`, contraseña `ClienteDemo2026!`, DNI `10000001`.
- Cliente B: `cliente.b@example.invalid`, misma contraseña demo, DNI `10000002`.
- Guardia: usuario `guardia.demo`, contraseña `GuardiaDemo2026!`.
- Dispositivo demo: id 1, operador 1, evento 10 (Rock Fest), puerta Norte. Credencial ficticia de enrolamiento `DispositivoDemo2026-solo-local`. Su hash, no su valor, está en SQL. La credencial se resuelve en el servidor mediante configuración y asignación de sesión; no se incorpora a JavaScript público.

Son accesos públicos de prueba. No son cuentas de una aplicación desplegada ni contraseñas reales. El login de clientes y del personal de validación está implementado. Los datos incluyen una compra y una entrada de demostración para desarrollar el boleto de forma independiente del checkout. Estos datos no acreditan una compra realizada mediante la interfaz.

## Fuentes técnicas

- TOTP: https://www.rfc-editor.org/rfc/rfc6238
- Contraseñas PBKDF2-HMAC-SHA256 con 600000 iteraciones y sal: https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html
- Tomcat 10.1: https://tomcat.apache.org/migration-10.1.html
- JSTL: https://jakarta.ee/specifications/tags/3.0/

El código propio de TOTP está contrastado con los 18 vectores SHA1/SHA256/SHA512 del RFC; EventoPe fija SHA256, ocho dígitos y 30 segundos. No constituye una auditoría de seguridad de producción.


## Actualización de cartelera y ensayo de Semana 10

En instalaciones que ya importaron los datos anteriores, ejecutar únicamente `sql/04_cartelera_demo.sql` en su base local. Oculta Concierto EventoPe y asigna el dispositivo ficticio al Rock Fest (10), conservando las compras previas. No volver a importar el esquema ni regenerar la clave maestra.

Para el ensayo, registrar un cliente y comprar una entrada del Rock Fest. Abrir su boleto en el teléfono del cliente. En otro dispositivo iniciar sesión con `guardia.demo` / `GuardiaDemo2026!`, escribir el DNI del asistente y activar la cámara. El primer ingreso debe permitirse y el segundo denegarse. Servir HTTPS con certificado confiable para ambos dispositivos.

El recordatorio de credenciales debajo del acceso de seguridad se controla con `demo.mostrarCredenciales=true` en la configuración externa; dejar `false` al terminar la demostración. `seguridad.dispositivoToken.1` debe contener la credencial del dispositivo ficticio indicada en el ejemplo. No publicar configuración privada.

`assets/vendor/qrcode.min.js` es la distribución oficial de QRCode.js, bajo licencia MIT conservada en `LICENSE-qrcodejs.txt`. Fuente: https://github.com/davidshimjs/qrcodejs . SHA-256 de la copia: c541ef06327885a8415bca8df6071e14189b4855336def4f36db54bde8484f36.
