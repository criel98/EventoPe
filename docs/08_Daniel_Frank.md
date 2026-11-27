# Catálogo y cuentas — revisión del 12 de octubre de 2026

## Alcance completado

- Catálogo público con filtros por nombre, artista y tipo; detalle restringido a eventos publicados; zonas, precios, agotados y selección de una a cuatro entradas.
- Registro transaccional de cliente, perfil y preferencias opcionales, con validación de datos, correo/DNI únicos y errores de campo.
- Inicio de sesión de cliente y guardia, renovación de sesión, cierre por POST y protección CSRF común.
- Consulta y edición del perfil propio en `/cuenta/perfil`; `/perfil` conserva compatibilidad. La identidad se obtiene de la sesión, no del formulario.
- Vistas JSP/JSTL con escape de datos, codificación UTF-8 y adaptación a móvil y escritorio. Las respuestas privadas no se almacenan en caché.

## Verificación

Maven: **67 pruebas aprobadas, cero fallos, cero errores y ninguna omitida**. Incluye siete pruebas de catálogo y once de cuentas; el resto verifica el núcleo compartido existente. Las pruebas se ejecutaron en una instancia MySQL 8.0.46 aislada y el esquema exclusivo `eventope_base_test`.

Navegador: **38 comprobaciones aprobadas** en Edge automatizado, con tamaños de 360 × 800 y 1440 × 900. Se verificaron vistas, registro, credenciales incorrectas, login, renovación de sesión, edición, cierre de sesión, roles, CSRF, escape de contenido y ausencia de desplazamiento horizontal. Entorno: Tomcat 10.1.61 y HTTPS local con certificado exclusivo de pruebas; no se modificó la configuración del servidor habitual. Las capturas móviles son emulación de tamaño, no una prueba en un teléfono físico.

La base de desarrollo habitual y sus credenciales se conservaron. Las pruebas del navegador utilizaron otro esquema con datos ficticios. No se procesaron compras ni validaciones de entradas.

## Dependencias pendientes

- **Ethan:** implementar el receptor POST `/carrito/agregar` y el flujo de compra. El formulario del catálogo ya envía `eventoId`, `categoriaId`, `cantidad` y `csrf`, pero no se afirma que la compra esté integrada.
- **Glenn:** implementar `/entradas` y `/seguridad/panel`, consulta de boletos, QR y validación. El login del guardia autentica y redirige al destino acordado; el panel todavía no existe.
- La asociación del dispositivo de validación a la sesión debe completarse al integrar el panel de Glenn, verificando operador y credencial de dispositivo en el servidor.
- Ejecutar el recorrido completo en un teléfono físico con HTTPS confiable cuando se integren las pantallas restantes.

## Repetir las pruebas

Usar `EVENTOPE_TEST_DB_URL`, `EVENTOPE_TEST_DB_USER` y `EVENTOPE_TEST_DB_PASSWORD` según README y ejecutar `mvn clean verify`. El nombre del esquema debe ser exactamente `eventope_base_test`; las pruebas vacían sus datos ficticios. Sin esa configuración se omiten las pruebas de integración. No utilizar la base personal o de demostración.

Para repetir la revisión manual: buscar eventos y abrir detalle; registrar un cliente ficticio; probar contraseña incorrecta y correcta; editar perfil; cerrar sesión; autenticar guardia y comprobar que no puede abrir el perfil de cliente. No enviar datos reales ni claves en capturas.
