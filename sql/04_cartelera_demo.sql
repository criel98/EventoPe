-- Ejecutar UNA vez en la base de desarrollo seleccionada. No cambia tablas ni elimina compras.
START TRANSACTION;
UPDATE evento_cultural SET estado='INACTIVO' WHERE id=1 AND nombre='Concierto EventoPe';
UPDATE dispositivo_validacion d JOIN administrador_plataforma a ON a.id=d.operador_id
JOIN evento_cultural e ON e.id=10 AND e.nombre='Rock Fest Lima 2026: Libido & Mar de Copas'
SET d.evento_id=e.id WHERE d.id=1 AND a.username='guardia.demo' AND d.evento_id=1;
COMMIT;
-- La demostración cliente → seguridad debe comprar una entrada del Rock Fest (evento 10).
