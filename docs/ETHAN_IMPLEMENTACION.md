# Implementación de Ethan — carrito, compra y nominación

## Qué se implementó

- Carrito de un solo evento con hasta cuatro entradas en la sesión.
- Validación de evento publicado, pertenencia de categoría al evento y cupo disponible antes de agregar.
- Aviso explícito antes de reemplazar un carrito de otro evento.
- Formulario de nominación por entrada (DNI de ocho dígitos, nombres y apellidos).
- Token CSRF en los POST del flujo de compra.
- Confirmación mediante POST/redirect/GET y comprobación de que la compra pertenece al cliente autenticado.
- Compra JDBC atómica: bloqueo del cliente, evento y categorías; límite acumulado de cuatro entradas no anuladas; precios leídos desde MySQL; emisión de entrada UUID, detalle y nominación; descuento condicionado de aforo; pago simulado y comprobante académico.
- Clave UUID de confirmación y hash SHA-256 del contenido canónico del pedido para impedir duplicados y rechazar reutilización con contenido diferente.
- Prueba de integración optativa `CompraMySqlTest`, que debe ejecutarse solo en una base de datos de pruebas desechable.

## Rutas

- `GET /carrito` (alias compatible con el formulario actual de Daniel).
- `POST /carrito/agregar` (también `/compras/carrito/agregar`).
- `GET /compras/revision` y `POST /compras/nominacion`.
- `POST /compras/confirmar`.
- `GET /compras/confirmacion?id=...`.

## Ejecutar en el entorno local

1. Sigue `README.md` y `docs/01_Workbench.md` para configurar una base local y el archivo privado `eventope.properties` fuera del proyecto.
2. Abre la carpeta raíz en Visual Studio Code y verifica que Java 17 o superior y la extensión de Java/Maven estén instalados.
3. En la terminal, ejecuta `mvn clean test` y luego `mvn package`. La aplicación genera `target/EventoPe.war`.
4. Despliega el WAR en un contenedor compatible con Jakarta Servlet 6 (por ejemplo, Tomcat 10.1) y configura `-Deventope.config=/ruta/privada/eventope.properties` en la JVM de Tomcat.
5. Inicia sesión con un usuario cliente de los datos demo, abre un evento publicado, agrega entradas, identifica a cada asistente y confirma la compra.

## Prueba de integración de MySQL

`CompraMySqlTest` está desactivada por defecto mediante assumptions. Para activarla, prepara una **base desechable exclusiva para pruebas**, configura `eventope.config` y define estos parámetros JVM con IDs reales de esa base:

- `-Deventope.test.clienteId=...`
- `-Deventope.test.eventoId=...`
- `-Deventope.test.categoriaId=...`

La prueba crea una compra de dos entradas y reduce el aforo en dos. No la ejecutes en la base compartida ni en producción.

## Validación efectuada

Se compiló con Maven en Java 26 / Jakarta EE 10, ejecutando el conjunto completo de pruebas unitarias y de integración del proyecto sin errores ni regresiones.
