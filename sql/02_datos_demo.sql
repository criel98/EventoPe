-- DATOS FICTICIOS para desarrollo. Ejecutar UNA vez en esquema recién creado.
-- Cuentas públicas de demostración, nunca reutilizar en producción.
-- El login y las pantallas aún los construye el equipo.
SET time_zone = '+00:00';
START TRANSACTION;
INSERT INTO organizador_evento(id,ruc,razon_social,email_contacto,telefono) VALUES(1,'20000000001','Organizador Demo','organizador@example.invalid','900000001');
INSERT INTO local_concierto(id,nombre_local,direccion,capacidad_max,ciudad) VALUES(1,'Recinto Demo','Dirección ficticia',120,'Lima');
INSERT INTO artista(id,nombre_artistico,genero_musical,pais_origen) VALUES(1,'Artista Demo','Pop','Perú');
INSERT INTO evento_cultural(id,nombre,descripcion,tipo_evento,fecha,aforo,local_id,organizador_id,artista_id,estado) VALUES
(1,'Concierto EventoPe','Evento ficticio para demostrar la compra y el QR','CONCIERTO',UTC_TIMESTAMP()+INTERVAL 30 DAY,120,1,1,1,'PUBLICADO'),
(2,'Evento en preparación','No debe aparecer en la cartelera','TEATRO',UTC_TIMESTAMP()+INTERVAL 40 DAY,100,1,1,NULL,'BORRADOR');
INSERT INTO categoria_asiento(id,evento_id,nombre_zona,precio_base,aforo_total,aforo_disponible) VALUES(1,1,'General',100,100,99),(2,1,'VIP',150,20,20);
INSERT INTO usuario_cliente(id,nombres,apellidos,dni,email,contrasena_hash,telefono) VALUES
(1,'Cliente','Demo A','10000001','cliente.a@example.invalid','pbkdf2-sha256$600000$JWSJI9RqGoqjdX0iyPHbMg==$EpOtXplF5eFWHDceC/dGPZOam15rhCKw6c50GskcE/o=','900000001'),
(2,'Cliente','Demo B','10000002','cliente.b@example.invalid','pbkdf2-sha256$600000$JWSJI9RqGoqjdX0iyPHbMg==$EpOtXplF5eFWHDceC/dGPZOam15rhCKw6c50GskcE/o=','900000002');
INSERT INTO perfil_usuario(cliente_id,pais) VALUES(1,'Perú'),(2,'Perú');
INSERT INTO preferencias_musicales(cliente_id,genero_favorito) VALUES(1,'Pop'),(2,'Rock');
INSERT INTO administrador_plataforma(id,username,contrasena_hash,nivel_acceso,area_soporte) VALUES(1,'guardia.demo','pbkdf2-sha256$600000$oh6ZT/5S1GikPXiJAyf5Ag==$VwfC2fxVgIaE+8Zskin1QGCMysLkvuWf1JhCR3KvW7w=','VALIDACION','Puerta Norte');
INSERT INTO dispositivo_validacion(id,operador_id,evento_id,nombre_puerta,modelo_celular,token_acceso_hash) VALUES(1,1,1,'Norte','Dispositivo de demostración','3b4fbce9173d357ba0bd94146504a1a7f01f61c31f2743c0eac4033eeef251d1');
-- Compra inicial para que Glenn no dependa del checkout de Ethan.
-- Es una fixture identificada como DEMO, no evidencia de compra por navegador.
INSERT INTO transaccion_compra(id,codigo_unico,solicitud_hash,cliente_id,evento_id,subtotal,descuento,igv,total) VALUES(1,'DEMO-COMPRA-001',SHA2('DEMO-COMPRA-001',256),1,1,100,0,0,100);
INSERT INTO entrada_digital(id,codigo_entrada,evento_id,categoria_id,comprador_id) VALUES(1,'00000000-0000-4000-8000-000000000001',1,1,1);
INSERT INTO detalle_transaccion(transaccion_id,entrada_id,precio_aplicado,descuento_aplicado) VALUES(1,1,100,0);
INSERT INTO boleto_nominado(entrada_id,dni_asistente,nombres_asistente,apellidos_asistente) VALUES(1,'10000001','Asistente','Demo');
INSERT INTO metodo_pago(transaccion_id,nombre_metodo,nro_operacion) VALUES(1,'SIMULADO','DEMO-PAGO-001');
INSERT INTO comprobante_pago(transaccion_id,serie_correlativo,tipo_comprobante) VALUES(1,'DEMO-000001','ACADEMICO');
COMMIT;
