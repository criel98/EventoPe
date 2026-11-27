---
title: "Instalación y verificación del módulo (Glenn)"
---

1. **Sin cambios de BD.** Usa tu propia rama y tu propia base; el esquema `sql/01_esquema.sql` ya contiene todo lo necesario.
2. **Archivos nuevos:** servicios, DAO, DTO y servlets de incidencias y notificaciones, JSP en `WEB-INF/views/notificaciones` y `admin`, `incidencias.css`, `notificaciones.js`.
3. **Archivos modificados (cambios mínimos):** `ServicioAcceso`, `AccesoDAO`, `cabecera.jspf`, `seguridad.js`.
4. **Conexión con Ethan:** aplicar `docs/glenn/ethan_notificacion_compra.patch` en `ServicioCompra` (3 líneas marcadas `[Glenn]`). Sin el patch el módulo funciona, solo no hay aviso de compra.
5. **Login:** Frank debe permitir ADMIN y SOPORTE en `autorizarPersonal`; si no, el panel no es accesible.
6. **Rutas:** `/admin/incidencias`, `/admin/incidencias/detalle?id=`, `/notificaciones`, `/notificaciones/contador`.
7. **Verificar:** `mvn clean verify` (con `EVENTOPE_TEST_DB_URL`), y recorrer manualmente: denegar un QR → aparece incidencia → resolver / ingreso manual → el cliente ve su aviso.
