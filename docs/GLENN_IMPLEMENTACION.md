# Implementación del Módulo de Glenn: Mis Entradas, QR Dinámico y Panel de Validación

Este documento detalla la integración y puesta en marcha del módulo asignado a **Glenn**, correspondiente a la visualización de boletos digitales con códigos QR dinámicos (TOTP) para clientes y el panel único de control de acceso para el personal de seguridad.

---

## 1. Arquitectura y Componentes Creados

### A. Capa de Transferencia de Datos (DTO)
- [EntradaResumenDTO.java](../src/main/java/pe/edu/utp/eventope/dto/EntradaResumenDTO.java): Información sintetizada de las entradas del comprador, incluyendo enmascaramiento seguro del DNI (`****5678`) y estado de disponibilidad.
- [BoletoDetalleDTO.java](../src/main/java/pe/edu/utp/eventope/dto/BoletoDetalleDTO.java): Información completa del boleto individual, vinculación al titular, evento, ubicación y comprobante.
- [DispositivoOperadorDTO.java](../src/main/java/pe/edu/utp/eventope/dto/DispositivoOperadorDTO.java): Datos del punto de acceso asignado al operador de seguridad (dispositivo, puerta y evento activo).

### B. Capa de Acceso a Datos (DAO)
- [MisEntradasDAO.java](../src/main/java/pe/edu/utp/eventope/dao/MisEntradasDAO.java):
  - `listarPorComprador(Connection, int compradorId)`: Consulta boletos adquiridos por el cliente con datos del evento y asistente nominado.
  - `detalleBoleto(Connection, int compradorId, int entradaId)`: Revalida la propiedad del boleto por el comprador antes de emitir información sensible.
  - `dispositivoActivoPorOperador(Connection, int operadorId)`: Obtiene el dispositivo activo y puerta asignada al operador de validación.

### C. Controladores (Servlets)
- [MisEntradasServlet.java](../src/main/java/pe/edu/utp/eventope/controller/MisEntradasServlet.java) (`GET /entradas`): Lista los boletos disponibles y usados del cliente en sesión.
- [BoletoServlet.java](../src/main/java/pe/edu/utp/eventope/controller/BoletoServlet.java) (`GET /entradas/boleto`): Presenta la vista digital del boleto individual y prepara el contexto del QR dinámico.
- [QrServlet.java](../src/main/java/pe/edu/utp/eventope/controller/QrServlet.java) (`POST /entradas/qr`):
  - Valida CSRF y rol del cliente.
  - Invoca `ServicioQr.obtener(actor, entradaId)` de forma segura.
  - Retorna `{ "ok": true, "contenido": "...", "horaServidor": "...", "expiraEn": "..." }` con cabecera `Cache-Control: no-store`.
  - Nunca expone secretos HMAC ni trazas de excepción SQL.
- [PanelSeguridadServlet.java](../src/main/java/pe/edu/utp/eventope/controller/PanelSeguridadServlet.java) (`GET /seguridad/panel`):
  - Exclusivo para usuarios con `rol == VALIDACION`.
  - Resuelve la puerta y dispositivo asignados.
  - Genera el identificador UUID idempotente de solicitud.
- [ValidarAccesoServlet.java](../src/main/java/pe/edu/utp/eventope/controller/ValidarAccesoServlet.java) (`POST /seguridad/validar`):
  - Valida el token TOTP mediante `ServicioAcceso.validar`.
  - Registra incidencias y auditoría en BD.
  - Detecta repetición de solicitud (`recuperado=true`) y devuelve el resultado anterior sin admitir un segundo ingreso.

### D. Vistas JSP y Scripts Frontend
- [lista.jsp](../src/main/webapp/WEB-INF/views/entradas/lista.jsp): Tarjetas de boletos, badges de estado ("Válida", "Utilizada", "Anulada") y botón de acceso.
- [boleto.jsp](../src/main/webapp/WEB-INF/views/entradas/boleto.jsp): Contenedor optimizado para teléfonos con cuenta regresiva en vivo, barra de progreso y advertencias antifraude.
- [panel.jsp](../src/main/webapp/WEB-INF/views/seguridad/panel.jsp): Panel único para el guardia con visor de cámara, campo para corroborar DNI físico y modo manual con indicación explícita.
- [boleto.js](../src/main/webapp/assets/js/boleto.js):
  - Auto-recarga sincronizada con el reloj del servidor.
  - Escucha `visibilitychange` para refrescar de inmediato al reactivar pestañas en segundo plano.
  - Bloqueo en caso de corte de red (no alarga vigencia si expiró).
- [seguridad.js](../src/main/webapp/assets/js/seguridad.js):
  - Detección de cámara vía `getUserMedia` con mensajes claros ante denegación o ausencia de dispositivo.
  - Escaneo con `jsQR` y aceleración nativa mediante `BarcodeDetector`.
  - Pausa de escaneo durante la validación para prevenir lecturas múltiples accidentales.
  - Mantenimiento del UUID ante pérdida de paquetes para asegurar idempotencia.

---

## 2. Bibliotecas Locales (Vendor)

| Biblioteca | Versión | Licencia | Archivo |
|---|---|---|---|
| **QRCode.js** (Davidshimjs) | 0.4.0 | MIT | `assets/vendor/qrcode.min.js` |
| **jsQR** | 1.4.0 | Apache 2.0 | `assets/vendor/jsqr.min.js` |

*Ambas bibliotecas residen de forma 100% local en el proyecto, sin depender de redes externas ni CDNs en producción.*

---

## 3. Pruebas y Validación

1. **Pruebas Unitarias**:
   - `pe.edu.utp.eventope.GlennModuloTest`: Comprobación de ocultamiento de DNI, inmutabilidad de DTOs y restricciones de rol (`exigirCliente()` y `exigirValidacion()`).
   - `pe.edu.utp.eventope.TotpTest` (20 pruebas completadas).
   - `pe.edu.utp.eventope.ModelosTest` (3 pruebas completadas).
   - Total de pruebas en build: **69 pruebas (0 fallos, 0 errores)**.

2. **Credenciales de Demostración**:
   - **Cliente**: `cliente.a@example.invalid` / `ClienteDemo2026!` (tiene entrada #1 preconfigurada en `sql/02_datos_demo.sql`).
   - **Guardia de Seguridad**: `guardia.demo` / `GuardiaDemo2026!` (dispositivo #1 asignado a Puerta Norte).

3. **Despliegue**:
   - WAR compilado y empaquetado exitosamente en `target/EventoPe.war`.
   - Copiado a `webapps/EventoPe.war` de Tomcat.
