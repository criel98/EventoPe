# Ethan — carrito, compra simulada y nominación

Resultado esperado: desde el evento, un cliente compra de una a cuatro entradas, identifica a cada asistente y recibe confirmación. La entrega también incluye administración y validación de cupones, política por evento y devolución completa de compras elegibles. No implementar tarjetas reales ni temporizador de reserva.

## Tareas en orden

1. Crear DTO de carrito/línea/nominación en `src/main/java/pe/edu/utp/eventope/dto/`. Crear `controller/CarritoServlet.java` GET `/carrito` y `AgregarCarritoServlet.java` POST `/carrito/agregar`. Aceptar los tres campos de Daniel, verificar ids y cantidades y guardar carrito en sesión. Un solo evento por carrito; si cambia de evento, pedir reemplazar el anterior. Vista `src/main/webapp/WEB-INF/views/compras/carrito.jsp`.
2. Crear `RevisionCompraServlet.java` GET `/compras/revision` y formulario de nominaciones: DNI/nombres/apellidos por cada entrada. Revisar identidad de sesión y valores de BD; no aceptar precio/total del navegador. Usar importes `BigDecimal`. Crear una clave UUID de confirmación ligada a ese pedido; doble clic conserva la misma clave. Mostrar “pago simulado” y no pedir números de tarjeta. Vistas `compras/revision.jsp` y, si hace falta, `compras/nominacion.jsp`.
3. Crear `service/ServicioCompra.java` y `dao/CompraDAO.java`. Consultar contratos antes de escribir SQL. Una Connection y una transacción para toda la compra. Bloquear cliente, evento y categorías en orden; comprobar PUBLICADO, relación evento/zona, stock y límite acumulado de cuatro entradas no anuladas por cliente/evento. Recalcular subtotal/total desde precios persistidos. Insertar compra, entradas, detalles, nominaciones, pago y comprobante; descontar cupo con condición suficiente y revisar filas afectadas. Commit solo al terminar; cualquier fallo hace rollback. Pago rechazado no crea compra confirmada. La clave repetida devuelve compra previa del mismo usuario/pedido; otro contenido con igual clave se rechaza.
4. Crear `ConfirmarCompraServlet.java` POST `/compras/confirmar` y `ConfirmacionCompraServlet.java` GET `/compras/confirmacion`. Después del POST redirigir a GET para evitar reenvío. Mostrar resumen/comprobante académico y enlace `/entradas` de Glenn. No generar TOTP dentro de la compra: el servicio de Glenn lo prepara al abrir el boleto. Crear `compras/confirmacion.jsp`.
5. Implementar la administración propia de Ethan: acceso autenticado para rol ADMIN, formularios con CSRF para crear/actualizar cupones y políticas por evento, y listados persistidos. Mantener el login de VALIDACION de Frank separado.
6. Implementar devolución completa del propietario: bloquear compra/evento/política/entradas, comprobar estado y plazo, calcular penalización, anular entradas, restaurar cupos y registrar el importe dentro de la misma transacción. La repetición debe devolver el resultado ya persistido sin volver a modificar datos.
7. Completar pruebas MySQL aisladas para cupones válido/inválido/vencido/inactivo, compra repetida, stock, límite cuatro, pago rechazado, devolución válida/repetida/ajena/usada/fuera de plazo y rollback. Documentar esquema, diagramas y evidencias reales.

## Dependencias

Puedes crear y probar el servicio con SQL demo sin esperar pantallas. Recibes el formulario de Daniel y la sesión de Frank. Glenn necesita la entrada/nominación persistida por tu compra; avisarle cuando el servicio esté probado. No cambies el modelo `EntradaDigital` ni el formato UUID de `codigoEntrada`.

## Pruebas y checklist

- [x] Compra de dos entradas crea 1 compra, 2 entradas/detalles/nominaciones, 1 pago y 1 comprobante.
- [x] Stock disminuye exactamente dos; la suma coincide con precios de BD.
- [x] Cambiar precio/total/cantidad/cliente en el formulario no altera reglas.
- [x] Categoría de otro evento, evento no publicado y DNI inválido se rechazan.
- [x] Compra rechazada o fallo intermedio no deja filas parciales ni descuenta cupo.
- [x] Doble clic/reintento con la misma clave produce una sola compra.
- [x] Dos clientes compitiendo por el último cupo no compran ambos.
- [x] Dos compras concurrentes del mismo cliente no superan el límite de cuatro.
- [x] Confirmación ajena no es accesible cambiando id en URL.
- [x] Pruebas aisladas para compra con cupón, persistencia de descuento por entrada y confirmación repetida.
- [x] Pruebas MySQL de cupones, pago, idempotencia, límite de entradas, devolución, propiedad, estados, plazo y rollback.
- [x] Panel de administración autenticado y gestión de cupón/política probados por HTTP local.
- [ ] Recorrido manual de compra/devolución en navegador y capturas reales para 3.8.

Divide las entregas en carrito, servicio probado y pantallas conectadas. No entregues primero una página que simule éxito sin guardar la compra.

El informe, los diagramas, el diccionario de las cinco clases y la evidencia registrada están en `docs/ETHAN_IMPLEMENTACION.md`.
