-- Seleccionar la base de desarrollo correcta antes de ejecutar. Solo lectura.
SELECT DATABASE() AS base_seleccionada;
SELECT TABLE_TYPE,COUNT(*) AS cantidad FROM information_schema.TABLES WHERE TABLE_SCHEMA=DATABASE() GROUP BY TABLE_TYPE;
SELECT id,nombre,estado,fecha FROM evento_cultural;
SELECT id,nombre_zona,aforo_total,aforo_disponible FROM categoria_asiento;
SELECT id,nombres,email,estado FROM usuario_cliente;
SELECT id,username,nivel_acceso,estado FROM administrador_plataforma;
SELECT * FROM historial_compra;
SELECT e.id,e.estado_uso,b.nombres_asistente FROM entrada_digital e JOIN boleto_nominado b ON b.entrada_id=e.id;
-- No consultar contraseñas, clave TOTP o datos personales reales para capturas públicas.
