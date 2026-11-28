-- Migracion aditiva para el dump legacy eventope_db.sql.
-- No ejecutar sql/05_daniel.sql antes de este archivo: el dump no contiene varias
-- columnas usadas por esos indices. MySQL 8.0.16+.
--
-- PRECAUCION: hacer backup y restaurarlo en una base de pruebas primero.
-- Ejecutar las consultas de prevalidacion de esta seccion y verificar que todos
-- los conteos sean 0. Si algun conteo no es 0, detenerse y resolver la ambiguedad
-- de los datos antes de ejecutar los ALTER/UPDATE de abajo.
-- La migracion es de una sola ejecucion; no volver a ejecutarla.

-- Eventos cuyo aforo supera el aforo del local o tiene valores no validos.
SELECT COUNT(*) AS eventos_con_aforo_invalido
FROM evento_cultural e
LEFT JOIN local_concierto l ON l.id=e.local_id
WHERE e.aforo<=0 OR l.id IS NULL OR l.capacidad_max<=0 OR e.aforo>l.capacidad_max;

-- Precios y disponibilidades que no admiten una migracion valida.
SELECT COUNT(*) AS zonas_con_precio_o_cupo_invalido
FROM categoria_asiento
WHERE precio_base<0 OR aforo_disponible<0;

SELECT COUNT(*) AS zonas_sin_capacidad_inferible
FROM categoria_asiento c
WHERE c.aforo_disponible + (
  SELECT COUNT(*)
  FROM entrada_digital d
  WHERE d.evento_id=c.evento_id AND d.estado_uso<>'ANULADA'
)=0;

-- No se puede inferir la zona de entradas antiguas si un evento tiene varias zonas.
SELECT COUNT(*) AS eventos_con_zonas_ambiguas_y_entradas
FROM (
  SELECT c.evento_id
  FROM categoria_asiento c
  GROUP BY c.evento_id
  HAVING COUNT(*)>1
     AND EXISTS (SELECT 1 FROM entrada_digital d WHERE d.evento_id=c.evento_id)
) q;

-- La capacidad inferida de las zonas no debe exceder el aforo del evento.
SELECT COUNT(*) AS eventos_cuya_capacidad_inferida_excede_aforo
FROM (
  SELECT e.id
  FROM evento_cultural e
  JOIN categoria_asiento c ON c.evento_id=e.id
  LEFT JOIN entrada_digital d
    ON d.evento_id=e.id AND d.estado_uso<>'ANULADA'
  GROUP BY e.id,e.aforo
  HAVING SUM(c.aforo_disponible)+COUNT(DISTINCT d.id)>e.aforo
) q;

-- Cada entrada debe poder vincularse a una zona por evento.
SELECT COUNT(*) AS entradas_sin_zona
FROM entrada_digital d
LEFT JOIN categoria_asiento c ON c.evento_id=d.evento_id
WHERE c.id IS NULL;

-- Cada compra debe tener entradas y pertenecer a un solo evento.
SELECT COUNT(*) AS compras_sin_evento_unico
FROM (
  SELECT t.id
  FROM transaccion_compra t
  LEFT JOIN detalle_transaccion dt ON dt.transaccion_id=t.id
  LEFT JOIN entrada_digital d ON d.id=dt.entrada_id
  GROUP BY t.id
  HAVING COUNT(DISTINCT d.evento_id)<>1
) q;

-- Nombres de zona duplicados impedirian crear la restriccion de unicidad.
SELECT COUNT(*) AS nombres_de_zona_duplicados
FROM (
  SELECT evento_id,nombre_zona
  FROM categoria_asiento
  GROUP BY evento_id,nombre_zona
  HAVING COUNT(*)>1
) q;

-- Valores fuera de los estados que la aplicacion puede conservar/mapear.
SELECT COUNT(*) AS estados_evento_desconocidos
FROM evento_cultural
WHERE estado NOT IN ('ACTIVO','PUBLICADO','BORRADOR','INACTIVO');

SELECT COUNT(*) AS estados_entrada_desconocidos
FROM entrada_digital
WHERE estado_uso NOT IN ('SIN_USAR','DISPONIBLE','UTILIZADA','ANULADA');

-- ============================================================================
-- MIGRACION: ejecutar solo despues de confirmar que los conteos anteriores son 0.
-- ============================================================================

-- El esquema antiguo no distinguia categoria del evento; se asigna CONCIERTO como
-- valor inicial. Corregir tipo_evento a TEATRO/FESTIVAL cuando corresponda.
ALTER TABLE evento_cultural
  ADD COLUMN tipo_evento VARCHAR(20) NOT NULL DEFAULT 'CONCIERTO' AFTER descripcion,
  MODIFY COLUMN artista_id INT NULL,
  MODIFY COLUMN estado VARCHAR(20) NOT NULL DEFAULT 'BORRADOR';

UPDATE evento_cultural SET estado='PUBLICADO' WHERE estado='ACTIVO';

ALTER TABLE evento_cultural
  ADD CONSTRAINT ck_evento_aforo_daniel CHECK (aforo>0),
  ADD CONSTRAINT ck_evento_estado_daniel CHECK (estado IN ('BORRADOR','PUBLICADO','INACTIVO'));

-- El dump guardaba solo aforo disponible. Con una zona por evento, la capacidad
-- total se reconstruye sumando entradas emitidas no anuladas.
ALTER TABLE categoria_asiento
  ADD COLUMN aforo_total INT NULL AFTER precio_base;

UPDATE categoria_asiento c
SET c.aforo_total = c.aforo_disponible + (
  SELECT COUNT(*)
  FROM entrada_digital d
  WHERE d.evento_id=c.evento_id AND d.estado_uso<>'ANULADA'
);

ALTER TABLE categoria_asiento
  MODIFY COLUMN aforo_total INT NOT NULL,
  ADD CONSTRAINT ck_categoria_precio_daniel CHECK (precio_base>=0),
  ADD CONSTRAINT ck_categoria_aforo_daniel CHECK (aforo_total>0 AND aforo_disponible BETWEEN 0 AND aforo_total),
  ADD CONSTRAINT uq_categoria_evento_nombre_daniel UNIQUE (evento_id,nombre_zona),
  DROP FOREIGN KEY categoria_asiento_ibfk_1,
  ADD CONSTRAINT fk_categoria_evento_daniel FOREIGN KEY (evento_id) REFERENCES evento_cultural(id) ON DELETE RESTRICT;

-- Las entradas antiguas no guardaban la zona. La prevalidacion exige una sola zona
-- por evento con entradas, por lo que la asociacion se puede recuperar sin elegir
-- arbitrariamente entre varias zonas.
ALTER TABLE entrada_digital
  ADD COLUMN categoria_id INT NULL AFTER evento_id,
  ADD COLUMN fecha_emision DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;

UPDATE entrada_digital d
JOIN categoria_asiento c ON c.evento_id=d.evento_id
SET d.categoria_id=c.id;

UPDATE entrada_digital SET estado_uso='DISPONIBLE' WHERE estado_uso='SIN_USAR';

ALTER TABLE entrada_digital
  MODIFY COLUMN categoria_id INT NOT NULL,
  MODIFY COLUMN estado_uso VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
  ADD CONSTRAINT ck_entrada_estado_daniel CHECK (estado_uso IN ('DISPONIBLE','UTILIZADA','ANULADA')),
  ADD CONSTRAINT fk_entrada_categoria_daniel FOREIGN KEY (categoria_id) REFERENCES categoria_asiento(id) ON DELETE RESTRICT,
  ADD INDEX ix_entrada_evento_categoria (evento_id,categoria_id);

-- La compra antigua no tenia evento_id; se reconstruye desde sus entradas.
ALTER TABLE transaccion_compra
  ADD COLUMN solicitud_hash CHAR(64) NULL AFTER codigo_unico,
  ADD COLUMN evento_id INT NULL AFTER cliente_id,
  ADD COLUMN descuento DECIMAL(10,2) NOT NULL DEFAULT 0 AFTER subtotal,
  ADD COLUMN estado VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADA' AFTER total,
  ADD COLUMN importe_reembolso DECIMAL(10,2) NULL,
  ADD COLUMN fecha_reembolso DATETIME NULL;

UPDATE transaccion_compra t
JOIN (
  SELECT dt.transaccion_id, MIN(d.evento_id) AS evento_id
  FROM detalle_transaccion dt
  JOIN entrada_digital d ON d.id=dt.entrada_id
  GROUP BY dt.transaccion_id
) x ON x.transaccion_id=t.id
SET t.evento_id=x.evento_id;

UPDATE transaccion_compra
SET solicitud_hash=SHA2(codigo_unico,256)
WHERE solicitud_hash IS NULL;

ALTER TABLE transaccion_compra
  MODIFY COLUMN solicitud_hash CHAR(64) NOT NULL,
  MODIFY COLUMN evento_id INT NOT NULL,
  ADD CONSTRAINT fk_compra_evento_daniel FOREIGN KEY (evento_id) REFERENCES evento_cultural(id) ON DELETE RESTRICT,
  ADD INDEX ix_compra_evento (evento_id);

ALTER TABLE detalle_transaccion
  ADD COLUMN descuento_aplicado DECIMAL(10,2) NOT NULL DEFAULT 0;

ALTER TABLE evento_cultural
  ADD INDEX ix_evento_estado_fecha_tipo (estado,fecha,tipo_evento),
  ADD INDEX ix_evento_local_aforo (local_id,aforo);

ALTER TABLE local_concierto
  ADD INDEX ix_local_ciudad (ciudad);

ALTER TABLE categoria_asiento
  ADD INDEX ix_categoria_evento_precio (evento_id,precio_base);

-- Comprobacion posterior: deben ser 0 los eventos cuya suma de aforos de zonas
-- excede el aforo del evento; revise los datos antes de publicar cualquiera.
SELECT e.id,e.nombre,e.aforo,SUM(c.aforo_total) AS aforo_zonas
FROM evento_cultural e
JOIN categoria_asiento c ON c.evento_id=e.id
GROUP BY e.id,e.nombre,e.aforo
HAVING SUM(c.aforo_total)>e.aforo;
