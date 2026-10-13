# Verificación del segundo entregable

## Resultado integrado registrado el 12 de octubre de 2026

La ejecución con MySQL aislado registró 79 casos: 77 ejecutados, 0 fallos, 0 errores y 2 omitidos. El recorrido del navegador registró 54 comprobaciones, con capturas de teléfono y computadora. Estos resultados corresponden al entorno de prueba descrito abajo; no certifican todos los requisitos del proyecto final.

- Registro, inicio de sesión, perfil y catálogo.
- Selección de dos entradas, nominación y compra simulada persistida.
- Consulta de entradas, generación TOTP, cambio de intervalo y reconexión.
- Lectura de QR mediante vídeo simulado y decodificador real.
- Primer ingreso permitido y nuevo intento rechazado por entrada utilizada.
- Acceso simultáneo y rollback de control cubiertos por las pruebas de la base común.

## Entorno

Windows, JDK 26.0.1 compilando para Java 17, Maven, Tomcat 10.1 y MySQL 8. Las pruebas de integración utilizan una instancia aislada y datos ficticios. No deben ejecutarse sobre la base de trabajo del equipo.

El proyecto utiliza 24 tablas y la vista historial_compra. Los precios y el cupo se consultan en el servidor. La compra utiliza una transacción JDBC. Los códigos TOTP usan HMAC-SHA-256, ocho dígitos e intervalos de 30 segundos; los vectores de referencia también comprueban SHA1 y SHA512.

## Repetir las comprobaciones

`mvn test` ejecuta las pruebas habilitadas. Las pruebas que requieren `EVENTOPE_TEST_DB_URL` se omiten cuando no hay una base exclusiva configurada. `CompraMySqlTest` requiere además los parámetros documentados en esa clase. La carga automática de eventos está deshabilitada para impedir que una prueba altere la base personal.

`mvn package` genera `target/EventoPe.war`. Los resultados XML se regeneran en `target/surefire-reports`; estos archivos de compilación no forman parte del repositorio.

## Comprobaciones pendientes

- Cámara física de teléfono y recorrido completo con HTTPS confiable en los dispositivos de exposición.
- Pruebas formales de compradores concurrentes, carga, disponibilidad y rendimiento.
- Funciones del alcance posterior: central, soporte, devoluciones, cupones y administración visual.

Los registros de `evidencias/pruebas_base.csv` y `evidencias/pruebas_daniel_frank.csv` corresponden a etapas anteriores. Las capturas y el resumen integrado se documentan en la sección 3.8 del informe del segundo entregable.

TOTP, nominación y consumo único reducen reutilización y transferencia informal. El guardia debe contrastar la identidad del asistente; un QR temporal por sí solo no garantiza la eliminación de toda reventa.
