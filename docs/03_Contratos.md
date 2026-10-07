# Acuerdos comunes para que las cuatro partes encajen

Estos acuerdos definen las interfaces y dependencias del trabajo de Semana 10. La central y los paneles de administración corresponden a una fase posterior; sus modelos se conservan.

## Archivos compartidos

La base define los nombres compartidos de modelos y tablas; los cambios deben coordinarse antes de su implementación. El integrador es Leonardo. Si alguien necesita otra columna, propone el cambio al integrador antes de editar SQL por su cuenta. Los nombres definitivos están en `Diccionario.md`.

Frank es dueño de login/sesión; Daniel de catálogo; Ethan de compra/entrada/nominación; Glenn de páginas de boleto/seguridad. Los servicios TOTP y acceso de esta base son compartidos y ya probados. Se debe reutilizar su implementación y mantener sus pruebas al conectar las interfaces.

## Identidad

La sesión HTTP guarda un objeto `SesionUsuario` bajo el nombre `sesionUsuario`. CLIENTE y PERSONAL son tipos separados; sus ids pueden coincidir sin ser la misma persona. Frank verifica contraseña con `PasswordUtil.verificar`, estado y rol antes de construirlo. Renueva el id de sesión después del login e invalida la sesión al salir. Guarda CSRF con `Csrf.token(session)` y envía el valor en formulario o cabecera `X-CSRF-Token`. Los filtros de la base no hacen login por sí solos.

## Rutas acordadas

| Autor | Ruta | Acción |
|---|---|---|
| Daniel | GET `/eventos` | Cartelera y filtros |
| Daniel | GET `/eventos/detalle?id=...` | Zonas y precios del evento publicado |
| Frank | GET/POST `/login`, `/registro` | Clientes |
| Frank | POST `/logout` | Salir, con CSRF |
| Frank | GET/POST `/personal/login` | Acceso mínimo del guardia; no perfil ni panel central |
| Frank | GET/POST `/cuenta/perfil` | Datos propios |
| Ethan | GET `/compras/carrito`; POST `/compras/carrito` | Carrito de un evento; zona y cantidad |
| Ethan | GET/POST `/compras/nominacion` | Un asistente por boleto |
| Ethan | GET `/compras/revision`; POST `/compras/confirmar` | Pago simulado y compra atómica |
| Ethan | GET `/compras/confirmacion?id=...` | Compra/comprobante propio |
| Glenn | GET `/entradas`; GET `/entradas/boleto?id=...` | Lista y boleto del comprador |
| Glenn | POST `/entradas/qr` | Obtener credencial temporal con `ServicioQr.obtener` |
| Glenn | GET `/seguridad/panel`; POST `/seguridad/validar` | Única pantalla del guardia y comprobación |

Todas las rutas se resuelven bajo el contexto `/EventoPe`; JSP con `c:url`. Las JSP privadas van en `WEB-INF/views`. No usar ids, roles ni precios del cliente como autoridad.

## Carrito y compra

Ethan crea DTO auxiliares de carrito/solicitud, no nuevas entidades persistentes: un evento, líneas de categoría/cantidad y nominaciones (DNI/nombres/apellidos). Cantidad total de 1 a 4. El formulario de Daniel manda solo eventoId, categoriaId, cantidad al POST acordado. Carrito no reserva stock y no incluye temporizador de diez minutos.

Ethan genera UUID para `codigoUnico`; para `solicitudHash` usa una representación canónica del pedido (mismo orden de líneas y campos), nunca el total que envió el navegador. Bloquea cliente, evento y zonas ordenadas; comprueba publicación, stock y máximo de cuatro entradas no anuladas por cliente/evento, incluso entre sesiones. Obtiene precios de BD. Este alcance no aplica cupones: descuento=0 y cuponId=NULL. El IGV es solo un dato incluido del comprobante académico, no una suma adicional; la demo puede usar igv=0 sin afirmar cálculo tributario.

Una compra de n boletos inserta una transacción, n entradas, n detalles, n nominaciones, un pago aprobado y un comprobante. Entrada antes de detalle, porque detalle referencia entrada. `codigoEntrada` debe ser UUID minúsculo estándar, generado en servidor. `estadoUso=DISPONIBLE`. Los códigos públicos no son claves de acceso.

Todo en la MISMA Connection: `setAutoCommit(false)`, INSERT/UPDATE, commit o rollback. DAO no confirma ni cierra una conexión ajena. Usar claves generadas, nunca `MAX(id)+1`. El pago simulado rechazado no genera compra confirmada ni descuenta cupo. Reintento de misma clave devuelve la compra original solo al propietario y con el mismo contenido. Recalcular importes desde BD, no aceptar total de POST.

## Boleto y TOTP

Glenn consulta las entradas del cliente por `comprador_id`. Cada boleto tiene evento/zona/nominado y DNI parcialmente oculto. Llama a `ServicioQr.obtener(actor,entradaId)`. Recibe `contenido`, `horaServidor` y `expiraEn`; dibuja ese contenido con una biblioteca QR local fijada. La base aún no incluye la biblioteca de dibujo ni la de cámara: son dos dependencias frontend a escoger/fijar una sola vez por Glenn y registrar en README.

TOTP SHA256, ocho dígitos, ventana fija UTC de 30 s. Formato ya implementado: `EP1:codigoEntrada:intervalo:otp`. La clave única de cada entrada se crea al solicitar el primer QR y se cifra con AES-GCM y clave maestra externa. Nunca se entrega al navegador. Dos pestañas en el mismo intervalo muestran el mismo código; refrescar NO crea otra rotación. Cuando cambia el intervalo, consultar de nuevo y registrar una fila de auditoría si se entrega ese código. Si la vista está cerrada no se crea una fila cada treinta segundos; se auditan intervalos efectivamente mostrados, no una tarea que genera basura en segundo plano.

Una apertura cerca del cambio de intervalo puede tener menos de treinta segundos de vigencia. El contador usa `expiraEn` y hora del servidor, no treinta segundos desde que abrió la pantalla. Al expirar, ocultar el QR viejo hasta obtener el nuevo; error de red nunca prolonga su validez. Respuestas privadas con `Cache-Control: no-store`; no poner QR/DNI/clave en URLs o logs.

## La única pantalla de seguridad

Guardia entra por login mínimo de Frank. Solo VALIDACION abre el panel, con dispositivo asignado a evento/puerta. Puede leer QR con cámara y escribir el DNI del documento presentado. El DNI del QR no existe: el payload no lleva datos personales. Para esta demo, guardia compara identidad presencial y el servidor contrasta el DNI ingresado con la nominación.

Glenn genera `solicitudId` UUID al iniciar un intento, pausa escaneo mientras consulta y llama `ServicioAcceso.validar(actor, dispositivoId, tokenDispositivo, solicitudId, contenidoQr, dniPresentado)`. El dispositivo/credencial se resuelve por sesión/enrolamiento verificado, no por elegir libremente operador/puerta en un formulario. Mostrar permitido o denegado y motivo. Nunca convertir una excepción en permiso.

El resultado `recuperado=true` indica que ya se procesó la misma solicitud; mostrar “resultado recuperado, no autorizar un nuevo ingreso”. Si se pierde la respuesta, reenviar exactamente la misma solicitud y el mismo id; el servicio recupera el resultado previo. Un escaneo nuevo tiene otro id y se deniega si ya se usó. La comprobación exige la ventana actual, sin tolerancia al intervalo anterior para esta demo.

Ya se registra control e incidencia de denegaciones autenticadas. No construir todavía bandeja de incidencias, ingreso manual, notificaciones, mensajería, central o perfil del guardia. Los intentos sin sesión o dispositivo válido se rechazan antes de consultar el boleto. La limitación de 30 intentos/minuto/dispositivo está implementada en el servicio como control básico, no como protección completa de producción.

## Orden para integrar sin bloquearse

1. Todos importan base/SQL/datos demo; cada uno su propia BD y clave maestra.
2. Frank entrega login/sesión primero; mientras tanto los demás prueban DAO/servicios con JUnit, sin introducir bypass de login en Servlets.
3. Daniel entrega el formulario de zona al carrito de Ethan.
4. Glenn desarrolla boletos con la entrada DEMO y los servicios ya preparados; luego usa compras reales de Ethan.
5. Recorrido final: cliente compra dos entradas, abre un boleto en teléfono, guardia escanea, primer ingreso permitido y segundo denegado; probar vencido y DNI incorrecto.
