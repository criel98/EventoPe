-- Migración/datos demo para cupones y devoluciones.
-- Ejecutar una vez contra el esquema local ya creado.
SET NAMES utf8mb4;
SET time_zone = '+00:00';

INSERT INTO cupon_descuento(codigo_cupon,porcentaje_descuento,fecha_expiracion,estado)
SELECT 'ETHAN10',10.00,UTC_TIMESTAMP()+INTERVAL 365 DAY,'ACTIVO'
WHERE NOT EXISTS (SELECT 1 FROM cupon_descuento WHERE codigo_cupon='ETHAN10');

INSERT INTO politica_devolucion(evento_id,dias_minimos_previos,penalizacion_porcentaje,estado_activo)
VALUES(10,7,10.00,TRUE),(12,7,10.00,TRUE)
ON DUPLICATE KEY UPDATE dias_minimos_previos=VALUES(dias_minimos_previos),
penalizacion_porcentaje=VALUES(penalizacion_porcentaje),estado_activo=TRUE;

-- Cuenta ficticia para administrar este módulo en la demo local.
INSERT INTO administrador_plataforma(username,contrasena_hash,nivel_acceso,area_soporte)
SELECT 'ethan.admin',
       'pbkdf2-sha256$600000$e90QE6gHoZxS9cf4wiAowA==$+PGHOya5sZM75+EIG1Ureo4/eM7ib5Q+InCSH7UUiy8=',
       'ADMIN','Cupones y devoluciones'
WHERE NOT EXISTS (SELECT 1 FROM administrador_plataforma WHERE username='ethan.admin');
