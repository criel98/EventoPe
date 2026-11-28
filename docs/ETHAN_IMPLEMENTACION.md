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

Se compiló con Maven en Java 17/25 y Jakarta EE 10. El conjunto unitario ejecutado contiene 74 pruebas, con 33 pruebas de integración omitidas por no tener MySQL local configurado.

## Cupones y descuentos
- Ruta de administración: `/admin/ethan`, protegida para el rol `ADMIN`.
- El cupón se valida en el servidor: formato, vigencia, estado activo y existencia real.
- El porcentaje se consulta con bloqueo de fila y el descuento se calcula sobre el subtotal persistido.
- El `cupon_id`, `subtotal`, `descuento` y `total` se guardan dentro de la misma transacción de compra.
- Dato demo: `ETHAN10`, 10%.

## Devoluciones
- Ruta: `/compras/devolver?id=ID_TRANSACCION`.
- Requiere compra propia, estado `CONFIRMADA`, política activa, plazo suficiente y entradas disponibles.
- Se bloquean compra, evento, política y entradas; se restauran cupos por categoría y se anulan todas las entradas.
- Repetir la devolución devuelve el reembolso existente sin duplicar movimientos ni restaurar cupos otra vez.
- Una entrada `UTILIZADA` o `ANULADA` impide devolver el conjunto completo.

## Pago simulado y pruebas
- En revisión se puede seleccionar `RECHAZADO`. Antes de crear filas se aborta la transacción y no se modifican cupos ni entradas.
- Se conservan la comprobación de doble clic, el límite acumulado de cuatro entradas y el bloqueo concurrente.
- Casos unitarios incluidos: normalización/cálculo de cupón, cupón inválido, penalización y plazo de devolución.

## Base de datos
Ejecutar una vez sobre el esquema local:

```sql
SOURCE sql/07_ethan.sql;
```

No requiere `ALTER TABLE`. La transacción usa `cupon_descuento`, `politica_devolucion`, `transaccion_compra`, `entrada_digital`, `detalle_transaccion` y `categoria_asiento`.
