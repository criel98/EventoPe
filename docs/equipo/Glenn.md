# Glenn — mis entradas, QR y única pantalla de seguridad

Resultado esperado: el cliente abre su boleto en el teléfono y ve un QR temporal; el guardia escanea ese QR en un único panel y obtiene permitido/denegado. Los cálculos y transacciones de QR/acceso están implementados. El trabajo principal consiste en conectarlos con consultas, Servlets y pantallas.

## Tareas en orden

1. Crear `dao/MisEntradasDAO.java` en `src/main/java/pe/edu/utp/eventope/` y DTO de listado/detalle. Consultar por comprador de sesión, uniendo evento/zona/nominación y compras propias. Crear `controller/MisEntradasServlet.java` GET `/entradas` y `BoletoServlet.java` GET `/entradas/boleto`. JSP en `src/main/webapp/WEB-INF/views/entradas/` (`lista.jsp`, `boleto.jsp`). Mostrar DNI parcialmente oculto. Comprobar dueño de nuevo al consultar cada boleto.
2. Crear `controller/QrServlet.java` POST `/entradas/qr`: verificar sesión y CSRF, llamar `ServicioQr.obtener(actor, entradaId)`, devolver únicamente contenido/horaServidor/expiraEn en JSON con no-store. Construir servicio con proveedor de conexión, `CifradoSecretos` y `Clock.systemUTC()`. Nunca enviar la clave secreta ni aceptar una hora del navegador. Manejar errores sin devolver detalles SQL ni stack trace. Usar una biblioteca JSON fijada o serialización correctamente escapada, no concatenación de datos sin control.
3. Crear `assets/js/boleto.js` y elegir UNA biblioteca local de dibujo QR con versión/licencia documentadas bajo `assets/vendor/`. Dibujar el contenido exacto del DTO. Calcular cuenta regresiva con horaServidor/expiraEn; al vencer ocultar QR viejo, solicitar el siguiente y redibujar. Volver a sincronizar al regresar a una pestaña suspendida. Dos pestañas en el mismo intervalo pueden mostrar el mismo código. Sin conexión no prolongar validez. Entrada usada/anulada no muestra un QR nuevo.
4. Crear `controller/PanelSeguridadServlet.java` GET `/seguridad/panel`, `ValidarAccesoServlet.java` POST `/seguridad/validar`, vista `seguridad/panel.jsp` y `assets/js/seguridad.js`. Panel único: evento/puerta asignados, cámara, campo DNI y resultado. Fijar biblioteca local de escaneo y documentar versión/licencia. Autenticación mínima la entrega Frank. Resolver dispositivo/credencial en servidor para ese operador; llamar `ServicioAcceso.validar` con sus parámetros reales. Pausar escaneo mientras se valida. Crear UUID por intento; repetir el mismo UUID si se perdió la respuesta, no generar otro automáticamente. `recuperado=true` muestra resultado anterior, nunca instrucción para permitir un segundo ingreso. No construir perfil, mensajes, central ni gestión de incidencias.

## Dependencias

Empieza con la entrada DEMO del SQL; no dependes del checkout para construir páginas. Frank entrega login y sesión del dispositivo; Ethan crea las entradas definitivas de las pruebas. Coordinar la clave maestra en la configuración del servidor: todas las instancias que usan la misma BD requieren la misma clave, sin compartirla por Git. No regenerarla en cada arranque.

La cámara debe probarse en HTTPS confiable para el teléfono. Si se usa entrada manual del contenido para probar el servicio, identificarla como prueba manual; no presentarla como escaneo de cámara aprobado.

## Pruebas y checklist

- [ ] Usuario A no puede ver ni generar QR de boleto de B cambiando id.
- [ ] QR cambia al pasar el intervalo y expira aunque falle la red.
- [ ] Dos pestañas no duplican auditoría del mismo intervalo.
- [ ] Captura antigua, OTP alterado, evento distinto y DNI incorrecto se deniegan.
- [ ] QR vigente/identidad correcta permiten un acceso; siguiente escaneo se deniega.
- [ ] Pérdida/reintento de la respuesta muestra “resultado recuperado”.
- [ ] Guardia sin sesión, cuenta inactiva o dispositivo revocado no valida.
- [ ] Permiso de cámara rechazado y cámara inexistente producen mensajes útiles.
- [ ] Pantalla de cliente y panel funcionan en teléfono y computadora.
- [ ] Ejecutar los tests existentes; añadir pruebas de permisos/respuestas HTTP al conectar los Servlets.
- [ ] Guardar capturas boleto, permitido y denegado, con fecha/dispositivo para 3.8.

No reemplaces `Totp`, `CifradoSecretos`, `QrDAO`, `AccesoDAO`, `ServicioQr` o `ServicioAcceso` por implementaciones alternativas sin revisión. Reproducir cualquier problema con una prueba antes de modificar el núcleo compartido.
