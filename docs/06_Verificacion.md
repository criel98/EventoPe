# Verificación de la base común

Fecha: 7 de octubre de 2026. Ejecución completa con MySQL de pruebas: BUILD SUCCESS; 49 pruebas, 0 fallos, 0 errores, 0 omitidas. Se generó EventoPe.war. Este resultado corresponde a los componentes de la base común; las pantallas permanecen pendientes de implementación.

| Clase | Pruebas | Fallos | Errores | Omitidas |
|---|---:|---:|---:|---:|
| pe.edu.utp.eventope.BaseMySqlTest | 22 | 0 | 0 | 0 |
| pe.edu.utp.eventope.CifradoTest | 2 | 0 | 0 | 0 |
| pe.edu.utp.eventope.ModelosTest | 3 | 0 | 0 | 0 |
| pe.edu.utp.eventope.PasswordTest | 2 | 0 | 0 | 0 |
| pe.edu.utp.eventope.TotpTest | 20 | 0 | 0 | 0 |

El resumen exportado está en `evidencias/pruebas_base.csv`; el código de todas las pruebas está en `src/test/java`. Los informes XML completos se regeneran en `target/surefire-reports` al ejecutar Maven; target se excluye del ZIP.

## Entorno y comprobaciones

- Windows, JDK 26.0.1 compilando con release 17, Maven y MySQL 8.0.46. Queda pendiente ejecutar y desplegar con el JDK 17/Tomcat 10.1 del equipo.
- Integración ejecutada en una instancia MySQL local aislada con datos ficticios y un esquema exclusivo de pruebas.
- `01_esquema.sql` crea 24 tablas y una vista. `02_datos_demo.sql` y `03_comprobar.sql` se ejecutaron sin error en otro esquema temporal vacío: dos clientes, un guardia, un evento publicado, uno borrador, una compra/entrada nominada y todavía ninguna clave TOTP. El primer QR genera esa clave mediante el servicio.
- TOTP contrastado con los 18 vectores del RFC 6238 (SHA1, SHA256, SHA512), además de límites de intervalo/formato. EventoPe usa SHA256.
- Cifrado: recuperación, aleatoriedad, alteración, clave incorrecta y vinculación a la entrada. Contraseñas: sal y verificación.
- Integración: emisión por propietario, estados, auditoría por intervalo, identidad/evento, código vencido/alterado/desconocido, dispositivo/rol, consumo único y recuperación de solicitud repetida.
- Diez repeticiones de acceso simultáneo con dos dispositivos: exactamente un ingreso permitido por entrada. Fallo provocado al registrar control verifica rollback del consumo. Las diez repeticiones forman parte de las 49 pruebas, no son 49 casos independientes de acceso.
- Filtros registrados con orden explícito UTF-8, sesión y CSRF. Su recorrido HTTP queda pendiente de prueba al crear los controladores de autenticación.

## Lo que aún no está verificado

Se encuentran pendientes la implementación y validación del catálogo, login, checkout, páginas del boleto, cámara, despliegue Tomcat y navegación en teléfono. Tampoco se ejecutaron pruebas de compradores concurrentes ni mediciones formales de rendimiento/disponibilidad del informe. Los responsables y criterios están en `equipo/` y `07_Demostracion_Semana10.md`.

TOTP, nominación y consumo único reducen reutilización y transferencia informal, pero no prueban que toda reventa sea imposible. El guardia debe contrastar la identidad; no presentar el QR temporal como garantía absoluta por sí solo.
