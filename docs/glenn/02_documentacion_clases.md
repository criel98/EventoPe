---
title: "Documentación de las 5 clases"
---

Cada clase es un POJO/DTO ligado a una tabla; la lógica vive en los servicios y DAO.

# DispositivoValidacion
**Tabla:** `dispositivo_validacion`. Celular/puerta autorizado para validar entradas de un evento.
**Atributos:** `id`, `operadorId` (guardia), `eventoId`, `nombrePuerta`, `modeloCelular`, `tokenAccesoHash` (solo el hash, único), `estadoDispositivo` (ACTIVO/REVOCADO).
**Relaciones:** un evento y un operador; un dispositivo registra muchos `ControlAccesoPuerta`.
**Reglas:** un dispositivo REVOCADO no valida. **Usada por:** `ServicioAcceso`, `AccesoDAO`.

# ControlAccesoPuerta
**Tabla:** `control_acceso_puerta`. Un intento de ingreso, permitido o denegado.
**Atributos:** `id`, `solicitudId` (único, idempotencia), `solicitudHash`, `eventoId`, `dispositivoId`, `operadorId`, `entradaId` (nulo si no hay entrada), `horaIngreso` (UTC), `estadoAcceso` (PERMITIDO/DENEGADO), `motivo`, `autorizadoPorId` (administrador en ingreso manual).
**Reglas:** repetir el mismo `solicitudId` devuelve el resultado previo (resultado recuperado) y no autoriza otro ingreso; un ingreso manual deja `motivo = INGRESO_MANUAL_INC_<id>` y `autorizadoPorId`.
**Usada por:** `ServicioAcceso`, `ServicioIngresoManual`.

# IncidenciaAcceso
**Tabla:** `incidencia_acceso`. Se crea por cada control DENEGADO.
**Atributos:** `id`, `controlId`, `tipoIncidencia`, `descripcion`, `severidad` (ALTA/MEDIA/BAJA), `estadoResolucion` (ABIERTA/RESUELTA/DESCARTADA), `resueltaPorId`, `fechaResolucion`, `motivoResolucion`.
**Reglas:** solo se cierra una ABIERTA; el cierre registra quién, cuándo y por qué y es inmutable.
**Usada por:** `ServicioIncidencias`, `ServicioIngresoManual`, `IncidenciaDAO`.

# NotificacionUsuario
**Tabla:** `notificacion_usuario`. Aviso interno para un cliente.
**Atributos:** `id`, `clienteId`, `tituloMensaje` (con prefijo de tipo), `contenido`, `fechaEnvio` (UTC), `leido`.
**Reglas:** toda consulta y modificación filtra por `clienteId`; no incluye QR, DNI ni códigos.
**Usada por:** `ServicioNotificaciones`, `NotificacionDAO`, `NotificacionesServlet`.

# HistorialRotacionQR
**Tabla:** `historial_rotacion_qr`. Auditoría de cada rotación del QR dinámico (TOTP, ventana de 30 s).
**Atributos:** `id`, `qrId`, `intervalo`, `horaGeneracion`, `hashAnterior`, `hashNuevo`.
**Relaciones:** muchos registros por `TicketQRDinamico`.
**Reglas:** solo se guardan hashes, nunca el secreto. **Usada por:** `ServicioQr`, `QrDAO` (código compartido, sin cambios en este módulo).

