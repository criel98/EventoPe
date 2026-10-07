# Integración, demostración y sección 3.8

## Orden de integración

1. Integrador: publicar la base revisada y confirmar que los cuatro usan la misma revisión, esquema y contratos. Cada uno usa BD local propia. Configuración y claves quedan fuera de Git.
2. Frank: login y sesiones de cliente/guardia. Integrador: Tomcat y HTTPS accesible desde los dispositivos de demostración.
3. Daniel: catálogo y detalle; Ethan: carrito que recibe ese formulario. En paralelo Glenn conecta la entrada DEMO con boleto/panel.
4. Ethan: compra transaccional y nominaciones; Glenn: mostrar sus entradas recién creadas.
5. Todos: recorrer el flujo completo en la misma instalación y corregir errores antes de capturar. Guardar versión/commit utilizado. No integrar cambios sin revisión al final de la presentación.

Los compañeros trabajan en sus archivos; cada pequeña entrega se integra antes de seguir con la siguiente. La central queda para la fase posterior. Las dependencias de sesión, esquema y compra existen aunque cada compañero tenga una tarea propia.

## Ensayo de la demostración

1. Preparar MySQL, servidor HTTPS, cuenta de cliente y cuenta de guardia. Verificar reloj del servidor y cámara del teléfono. Usar solo datos ficticios.
2. Cliente: abrir cartelera, elegir evento y zona, indicar cantidad, nominar a cada asistente, confirmar pago simulado y abrir “Mis entradas”. La compra debe hacerse por la interfaz; la compra del SQL sirve solo de apoyo al desarrollo.
3. Cliente: abrir boleto en teléfono y observar el cambio de QR al terminar el intervalo.
4. Guardia: iniciar sesión, abrir su único panel, escanear QR vigente y escribir el DNI presentado. Mostrar PERMITIDO y estado UTILIZADA en la BD.
5. Escanear de nuevo esa entrada: DENEGADO por uso anterior.
6. Con otra entrada sin consumir, probar primero DNI incorrecto y después una captura de QR vencido. Ambos denegados y la entrada permanece disponible. Finalmente presentar QR nuevo y DNI correcto para un ingreso válido.
7. Cortar conexión: el panel no debe mostrar permiso. Recuperar conexión y tratar correctamente reintentos, sin autorizar un ingreso adicional.

No reiniciar a mano el estado de una entrada usada para hacer pasar una prueba. Crear entradas distintas para casos distintos y documentar sus identificadores de prueba.

## Evidencias para 3.8

| Evidencia | Captura real | Código a explicar | RF relacionado |
|---|---|---|---|
| E01 | Cartelera y filtros | Servlet/DAO de Daniel | RF-04 |
| E02 | Registro/login/perfil | Servicio de Frank y sesión | RF-01 parcial |
| E03 | Carrito y nominaciones | Validación del pedido | RF-05, RF-11 |
| E04 | Confirmación de compra y filas resultantes | Transacción de ServicioCompra | RF-07, RF-08, RF-19 parcial |
| E05 | Boleto móvil y expiración | ServicioQr y contador de la vista | RF-09, RF-10 |
| E06 | Único panel: permitido | ServicioAcceso y actualización atómica | RF-12, RF-13 |
| E07 | Denegado: usado/vencido/alterado | Motivos y registro de incidencia | RF-13, RF-14 |
| E08 | Cliente en escritorio y móvil | JSP/CSS adaptable | RNF-06, pruebas pendientes por navegador |

Por cada evidencia escribir: pantalla, propósito, pasos, resultado esperado, resultado obtenido, fecha, navegador/dispositivo, versión del código y RF. Insertar fragmento corto del código final con archivo y explicación. Las capturas deben mostrar resultados del sistema propio, no maquetas ni pantallas generadas.

Los tests automatizados de la base pueden anexarse como evidencia del núcleo. No sustituyen capturas de pantallas terminadas, pruebas de compra ni mediciones de rendimiento. Borrar de capturas contraseñas, claves maestras y datos personales reales.

## Qué queda para después

RF-02 y RF-03: administración por pantalla (los datos demo no completan esos RF). RF-06 cupones. RF-15 resolución/ingreso manual. RF-16 devoluciones. RF-17 notificaciones. RF-18 soporte. RF-20 administración del personal (un login de guardia no completa ese RF). RF-01 queda parcial si falta historial/preferencias. RF-19 queda parcial si falta la verificación adicional ante abuso. No afirmar que los veinte RF están terminados.

## Checklist de cierre de Semana 10

- [ ] Cuatro partes integradas y compilación/pruebas correctas.
- [ ] Cliente realiza compra por UI y ve su boleto nuevo.
- [ ] Guardia demuestra permitido, repetido, vencido y alterado.
- [ ] Probado en teléfono y computadora; cámara bajo HTTPS.
- [ ] Stock/propiedad/identidad/permisos y reintentos verificados.
- [ ] Informe actualizado: alcance, TOTP, UML/ER, 3.7 y 3.8 con evidencia real.
- [ ] Matriz de observaciones y anexos coherentes; revisar rúbrica original para formato y conclusiones.
- [ ] ZIP fuente sin target, contraseñas, configuración local ni claves maestras.
- [ ] PDF consolidado, archivo(s) de entrega y enlace GitHub preparados según instrucciones originales del curso.
- [ ] Historial Git conserva aportes reales de los cuatro; no inventar commits ni porcentajes de avance.

Este alcance facilita la demostración solicitada; el cumplimiento de porcentajes backend/frontend debe evaluarse con la rúbrica del profesor y el trabajo efectivamente integrado.
