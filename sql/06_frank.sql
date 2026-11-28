-- Cambios SQL de Frank (RF-20 y RF-18) para entorno de pruebas/desarrollo.
-- Ejecutar después de 01_esquema.sql y 02_datos_demo.sql.
-- Contraseñas de prueba (12+ caracteres, formato pbkdf2-sha256 de PasswordUtil):
--   admin.demo   -> AdminDemo2026!
--   soporte.demo -> SoporteDemo2026!

SET NAMES utf8mb4;
SET time_zone = '+00:00';

START TRANSACTION;

INSERT INTO administrador_plataforma (id, username, contrasena_hash, nivel_acceso, area_soporte, estado)
VALUES
  (2, 'admin.demo', 'pbkdf2-sha256$600000$oh6ZT/5S1GikPXiJAyf5Ag==$VwfC2fxVgIaE+8Zskin1QGCMysLkvuWf1JhCR3KvW7w=', 'ADMIN', 'Administración', 'ACTIVO'),
  (3, 'soporte.demo', 'pbkdf2-sha256$600000$oh6ZT/5S1GikPXiJAyf5Ag==$VwfC2fxVgIaE+8Zskin1QGCMysLkvuWf1JhCR3KvW7w=', 'SOPORTE', 'Atención al Cliente', 'ACTIVO')
ON DUPLICATE KEY UPDATE
  contrasena_hash = VALUES(contrasena_hash),
  nivel_acceso = VALUES(nivel_acceso),
  area_soporte = VALUES(area_soporte),
  estado = VALUES(estado);

CREATE INDEX ix_reclamo_cliente_fecha ON reclamo_soporte (cliente_id, fecha_creacion);
CREATE INDEX ix_reclamo_estado_fecha ON reclamo_soporte (estado_reclamo, fecha_creacion);

COMMIT;
