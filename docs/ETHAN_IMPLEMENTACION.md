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

## Rama de GitHub

Desde la raíz del repositorio, crea una rama personal antes de subir cambios:

```bash
git switch -c feature/ethan-compra
 git status
 git add src/main/java/pe/edu/utp/eventope/dto/LineaCarrito.java src/main/java/pe/edu/utp/eventope/dto/NominacionAsistente.java src/main/java/pe/edu/utp/eventope/dto/CarritoCompra.java src/main/java/pe/edu/utp/eventope/dto/CompraConfirmada.java src/main/java/pe/edu/utp/eventope/dao/CompraDAO.java src/main/java/pe/edu/utp/eventope/service/ServicioCompra.java src/main/java/pe/edu/utp/eventope/controller/CarritoServlet.java src/main/java/pe/edu/utp/eventope/controller/AgregarCarritoServlet.java src/main/java/pe/edu/utp/eventope/controller/RevisionCompraServlet.java src/main/java/pe/edu/utp/eventope/controller/ConfirmarCompraServlet.java src/main/java/pe/edu/utp/eventope/controller/ConfirmacionCompraServlet.java src/main/java/pe/edu/utp/eventope/util/Csrf.java src/main/webapp/WEB-INF/views/compras docs/ETHAN_IMPLEMENTACION.md src/test/java/pe/edu/utp/eventope/CompraMySqlTest.java
git commit -m "Implementa carrito y compra nominada"
git push -u origin feature/ethan-compra
```

Revisa `git status` antes del commit y no agregues `target/`, contraseñas, claves maestras ni archivos de configuración privados.

## Validación efectuada en este entorno

Se compiló con `javac` el DAO/servicio/DTO y se hizo una comprobación sintáctica de los nuevos servlets usando interfaces temporales de Servlet. No se pudo ejecutar `mvn clean test` ni probar la transacción contra MySQL en este entorno porque Maven y una base de datos configurada no están disponibles aquí. La verificación final debe hacerse con Maven y la base local del equipo.
