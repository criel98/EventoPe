-- Incremental RF-02/RF-03 performance indexes. No original table or data is changed.
-- Apply once to the EventoPe schema after sql/01_esquema.sql.
ALTER TABLE evento_cultural
  ADD INDEX ix_evento_estado_fecha_tipo (estado, fecha, tipo_evento),
  ADD INDEX ix_evento_local_aforo (local_id, aforo);

ALTER TABLE local_concierto
  ADD INDEX ix_local_ciudad (ciudad);

ALTER TABLE categoria_asiento
  ADD INDEX ix_categoria_evento_precio (evento_id, precio_base);

ALTER TABLE transaccion_compra
  ADD INDEX ix_compra_evento (evento_id);

ALTER TABLE entrada_digital
  ADD INDEX ix_entrada_evento_categoria (evento_id, categoria_id);
