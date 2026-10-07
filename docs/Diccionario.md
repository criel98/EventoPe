# Diccionario de clases y columnas

Fuente de verdad de esta base: `sql/01_esquema.sql`. Propiedades Java en camelCase y columnas SQL en snake_case.


## OrganizadorEvento → organizador_evento

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| ruc | ruc | String | CHAR(11) NOT NULL UNIQUE |
| razonSocial | razon_social | String | VARCHAR(150) NOT NULL |
| emailContacto | email_contacto | String | VARCHAR(100) NOT NULL |
| telefono | telefono | String | VARCHAR(20) NULL |

## LocalConcierto → local_concierto

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| nombreLocal | nombre_local | String | VARCHAR(100) NOT NULL |
| direccion | direccion | String | VARCHAR(200) NOT NULL |
| capacidadMax | capacidad_max | int | INT NOT NULL CHECK (capacidad_max > 0) |
| ciudad | ciudad | String | VARCHAR(50) NOT NULL |

## Artista → artista

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| nombreArtistico | nombre_artistico | String | VARCHAR(100) NOT NULL |
| generoMusical | genero_musical | String | VARCHAR(50) NULL |
| paisOrigen | pais_origen | String | VARCHAR(50) NULL |
| redSocial | red_social | String | VARCHAR(150) NULL |

## UsuarioCliente → usuario_cliente

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| nombres | nombres | String | VARCHAR(100) NOT NULL |
| apellidos | apellidos | String | VARCHAR(100) NOT NULL |
| dni | dni | String | CHAR(8) NOT NULL UNIQUE |
| email | email | String | VARCHAR(100) NOT NULL UNIQUE |
| contrasenaHash | contrasena_hash | String | VARCHAR(255) NOT NULL |
| telefono | telefono | String | VARCHAR(20) NULL |
| estado | estado | String | VARCHAR(20) NOT NULL DEFAULT 'ACTIVO' CHECK (estado IN ('ACTIVO','INACTIVO')) |

## AdministradorPlataforma → administrador_plataforma

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| username | username | String | VARCHAR(50) NOT NULL UNIQUE |
| contrasenaHash | contrasena_hash | String | VARCHAR(255) NOT NULL |
| nivelAcceso | nivel_acceso | String | VARCHAR(20) NOT NULL CHECK (nivel_acceso IN ('ADMIN','VALIDACION','SOPORTE')) |
| areaSoporte | area_soporte | String | VARCHAR(50) NULL |
| estado | estado | String | VARCHAR(20) NOT NULL DEFAULT 'ACTIVO' CHECK (estado IN ('ACTIVO','INACTIVO')) |

## CuponDescuento → cupon_descuento

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| codigoCupon | codigo_cupon | String | VARCHAR(20) NOT NULL UNIQUE |
| porcentajeDescuento | porcentaje_descuento | BigDecimal | DECIMAL(5,2) NOT NULL CHECK (porcentaje_descuento BETWEEN 0 AND 100) |
| fechaExpiracion | fecha_expiracion | LocalDateTime | DATETIME NOT NULL |
| estado | estado | String | VARCHAR(20) NOT NULL DEFAULT 'ACTIVO' CHECK (estado IN ('ACTIVO','INACTIVO')) |

## EventoCultural → evento_cultural

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| nombre | nombre | String | VARCHAR(150) NOT NULL |
| descripcion | descripcion | String | TEXT NULL |
| tipoEvento | tipo_evento | String | VARCHAR(20) NOT NULL |
| fecha | fecha | LocalDateTime | DATETIME NOT NULL |
| aforo | aforo | int | INT NOT NULL CHECK (aforo > 0) |
| localId | local_id | int | INT NOT NULL |
| organizadorId | organizador_id | int | INT NOT NULL |
| artistaId | artista_id | Integer | INT NULL |
| estado | estado | String | VARCHAR(20) NOT NULL DEFAULT 'BORRADOR' CHECK (estado IN ('BORRADOR','PUBLICADO','INACTIVO')) |

## PerfilUsuario → perfil_usuario

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| clienteId | cliente_id | int | INT NOT NULL UNIQUE |
| fechaRegistro | fecha_registro | LocalDateTime | DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP |
| estadoVerificacion | estado_verificacion | boolean | BOOLEAN NOT NULL DEFAULT FALSE |
| pais | pais | String | VARCHAR(50) NULL |
| fechaNacimiento | fecha_nacimiento | LocalDate | DATE NULL |

## PreferenciasMusicales → preferencias_musicales

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| clienteId | cliente_id | int | INT NOT NULL UNIQUE |
| generoFavorito | genero_favorito | String | VARCHAR(50) NULL |
| artistaFavorito | artista_favorito | String | VARCHAR(100) NULL |

## CategoriaAsiento → categoria_asiento

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| eventoId | evento_id | int | INT NOT NULL |
| nombreZona | nombre_zona | String | VARCHAR(50) NOT NULL |
| precioBase | precio_base | BigDecimal | DECIMAL(10,2) NOT NULL CHECK (precio_base >= 0) |
| aforoTotal | aforo_total | int | INT NOT NULL CHECK (aforo_total > 0) |
| aforoDisponible | aforo_disponible | int | INT NOT NULL CHECK (aforo_disponible BETWEEN 0 AND aforo_total) |

## PoliticaDevolucion → politica_devolucion

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| eventoId | evento_id | int | INT NOT NULL UNIQUE |
| diasMinimosPrevios | dias_minimos_previos | int | INT NOT NULL CHECK (dias_minimos_previos >= 0) |
| penalizacionPorcentaje | penalizacion_porcentaje | BigDecimal | DECIMAL(5,2) NOT NULL CHECK (penalizacion_porcentaje BETWEEN 0 AND 100) |
| estadoActivo | estado_activo | boolean | BOOLEAN NOT NULL DEFAULT TRUE |

## TransaccionCompra → transaccion_compra

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| codigoUnico | codigo_unico | String | VARCHAR(50) NOT NULL UNIQUE |
| solicitudHash | solicitud_hash | String | CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL |
| clienteId | cliente_id | int | INT NOT NULL |
| eventoId | evento_id | int | INT NOT NULL |
| cuponId | cupon_id | Integer | INT NULL |
| fechaHora | fecha_hora | LocalDateTime | DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP |
| subtotal | subtotal | BigDecimal | DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0) |
| descuento | descuento | BigDecimal | DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (descuento BETWEEN 0 AND subtotal) |
| igv | igv | BigDecimal | DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (igv >= 0) |
| total | total | BigDecimal | DECIMAL(10,2) NOT NULL CHECK (total = subtotal - descuento) |
| estado | estado | String | VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADA' CHECK (estado IN ('CONFIRMADA','DEVUELTA')) |
| importeReembolso | importe_reembolso | BigDecimal | DECIMAL(10,2) NULL |
| fechaReembolso | fecha_reembolso | LocalDateTime | DATETIME NULL |

## EntradaDigital → entrada_digital

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| codigoEntrada | codigo_entrada | String | VARCHAR(50) CHARACTER SET ascii COLLATE ascii_bin NOT NULL UNIQUE |
| eventoId | evento_id | int | INT NOT NULL |
| categoriaId | categoria_id | int | INT NOT NULL |
| compradorId | comprador_id | int | INT NOT NULL |
| estadoUso | estado_uso | String | VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE' CHECK (estado_uso IN ('DISPONIBLE','UTILIZADA','ANULADA')) |
| fechaEmision | fecha_emision | LocalDateTime | DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP |

## DetalleTransaccion → detalle_transaccion

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| transaccionId | transaccion_id | int | INT NOT NULL |
| entradaId | entrada_id | int | INT NOT NULL UNIQUE |
| precioAplicado | precio_aplicado | BigDecimal | DECIMAL(10,2) NOT NULL CHECK (precio_aplicado >= 0) |
| descuentoAplicado | descuento_aplicado | BigDecimal | DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (descuento_aplicado BETWEEN 0 AND precio_aplicado) |

## MetodoPago → metodo_pago

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| transaccionId | transaccion_id | int | INT NOT NULL UNIQUE |
| nombreMetodo | nombre_metodo | String | VARCHAR(50) NOT NULL |
| nroOperacion | nro_operacion | String | VARCHAR(50) NOT NULL UNIQUE |
| estadoTransaccion | estado_transaccion | String | VARCHAR(20) NOT NULL DEFAULT 'APROBADO' CHECK (estado_transaccion = 'APROBADO') |

## ComprobantePago → comprobante_pago

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| transaccionId | transaccion_id | int | INT NOT NULL UNIQUE |
| serieCorrelativo | serie_correlativo | String | VARCHAR(20) NOT NULL UNIQUE |
| tipoComprobante | tipo_comprobante | String | VARCHAR(20) NOT NULL DEFAULT 'ACADEMICO' |
| fechaEmision | fecha_emision | LocalDateTime | DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP |

## BoletoNominado → boleto_nominado

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| entradaId | entrada_id | int | INT NOT NULL UNIQUE |
| dniAsistente | dni_asistente | String | CHAR(8) NOT NULL |
| nombresAsistente | nombres_asistente | String | VARCHAR(100) NOT NULL |
| apellidosAsistente | apellidos_asistente | String | VARCHAR(100) NOT NULL |

## TicketQRDinamico → ticket_qr_dinamico

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| entradaId | entrada_id | int | INT NOT NULL UNIQUE |
| claveTotpCifrada | clave_totp_cifrada | String | VARCHAR(255) CHARACTER SET ascii COLLATE ascii_bin NOT NULL |
| ultimoIntervalo | ultimo_intervalo | Long | BIGINT NULL |
| fechaGeneracion | fecha_generacion | LocalDateTime | DATETIME NULL |
| fechaExpiracion | fecha_expiracion | LocalDateTime | DATETIME NULL |

## HistorialRotacionQR → historial_rotacion_qr

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| qrId | qr_id | int | INT NOT NULL |
| intervalo | intervalo | long | BIGINT NOT NULL |
| horaGeneracion | hora_generacion | LocalDateTime | DATETIME NOT NULL |
| hashAnterior | hash_anterior | String | CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NULL |
| hashNuevo | hash_nuevo | String | CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL |

## DispositivoValidacion → dispositivo_validacion

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| operadorId | operador_id | int | INT NOT NULL |
| eventoId | evento_id | int | INT NOT NULL |
| nombrePuerta | nombre_puerta | String | VARCHAR(50) NOT NULL |
| modeloCelular | modelo_celular | String | VARCHAR(50) NULL |
| tokenAccesoHash | token_acceso_hash | String | CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL UNIQUE |
| estadoDispositivo | estado_dispositivo | String | VARCHAR(20) NOT NULL DEFAULT 'ACTIVO' CHECK (estado_dispositivo IN ('ACTIVO','REVOCADO')) |

## ControlAccesoPuerta → control_acceso_puerta

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| solicitudId | solicitud_id | String | CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL UNIQUE |
| solicitudHash | solicitud_hash | String | CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL |
| eventoId | evento_id | int | INT NOT NULL |
| dispositivoId | dispositivo_id | int | INT NOT NULL |
| operadorId | operador_id | int | INT NOT NULL |
| entradaId | entrada_id | Integer | INT NULL |
| horaIngreso | hora_ingreso | LocalDateTime | DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP |
| estadoAcceso | estado_acceso | String | VARCHAR(20) NOT NULL CHECK (estado_acceso IN ('PERMITIDO','DENEGADO')) |
| motivo | motivo | String | VARCHAR(100) NOT NULL |
| autorizadoPorId | autorizado_por_id | Integer | INT NULL |

## IncidenciaAcceso → incidencia_acceso

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| controlId | control_id | int | INT NOT NULL |
| tipoIncidencia | tipo_incidencia | String | VARCHAR(50) NOT NULL |
| descripcion | descripcion | String | TEXT NULL |
| severidad | severidad | String | VARCHAR(20) NOT NULL |
| estadoResolucion | estado_resolucion | String | VARCHAR(20) NOT NULL DEFAULT 'ABIERTA' |
| resueltaPorId | resuelta_por_id | Integer | INT NULL |
| fechaResolucion | fecha_resolucion | LocalDateTime | DATETIME NULL |
| motivoResolucion | motivo_resolucion | String | VARCHAR(200) NULL |

## ReclamoSoporte → reclamo_soporte

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| clienteId | cliente_id | int | INT NOT NULL |
| adminId | admin_id | Integer | INT NULL |
| incidenciaId | incidencia_id | Integer | INT NULL |
| asunto | asunto | String | VARCHAR(150) NOT NULL |
| descripcion | descripcion | String | TEXT NOT NULL |
| estadoReclamo | estado_reclamo | String | VARCHAR(20) NOT NULL DEFAULT 'ABIERTO' |
| fechaCreacion | fecha_creacion | LocalDateTime | DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP |
| respuesta | respuesta | String | TEXT NULL |

## NotificacionUsuario → notificacion_usuario

| Java | SQL | Tipo Java | Definición SQL |
|---|---|---|---|
| clienteId | cliente_id | int | INT NOT NULL |
| tituloMensaje | titulo_mensaje | String | VARCHAR(100) NOT NULL |
| contenido | contenido | String | TEXT NOT NULL |
| fechaEnvio | fecha_envio | LocalDateTime | DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP |
| leido | leido | boolean | BOOLEAN NOT NULL DEFAULT FALSE |

## HistorialCompra
Vista de solo lectura, con clienteId, totalBoletosComprados (long), ultimaCompraFecha (nullable). Sin id independiente ni INSERT.
