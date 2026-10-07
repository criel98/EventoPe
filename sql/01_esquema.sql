-- COPIA AJUSTADA. No modifica ni migra una base existente. MySQL 8.0.16+.

-- Ejecutar dentro de un esquema NUEVO seleccionado en Workbench.

-- Sin DROP, sin IF NOT EXISTS: un esquema ya ocupado produce error visible.

SET NAMES utf8mb4;

SET time_zone = '+00:00';

CREATE TABLE organizador_evento (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  ruc CHAR(11) NOT NULL UNIQUE,
  razon_social VARCHAR(150) NOT NULL,
  email_contacto VARCHAR(100) NOT NULL,
  telefono VARCHAR(20) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE local_concierto (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre_local VARCHAR(100) NOT NULL,
  direccion VARCHAR(200) NOT NULL,
  capacidad_max INT NOT NULL,
  ciudad VARCHAR(50) NOT NULL,
  CHECK (capacidad_max > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE artista (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre_artistico VARCHAR(100) NOT NULL,
  genero_musical VARCHAR(50) NULL,
  pais_origen VARCHAR(50) NULL,
  red_social VARCHAR(150) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE usuario_cliente (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombres VARCHAR(100) NOT NULL,
  apellidos VARCHAR(100) NOT NULL,
  dni CHAR(8) NOT NULL UNIQUE,
  email VARCHAR(100) NOT NULL UNIQUE,
  contrasena_hash VARCHAR(255) NOT NULL,
  telefono VARCHAR(20) NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
  CHECK (estado IN ('ACTIVO','INACTIVO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE administrador_plataforma (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  contrasena_hash VARCHAR(255) NOT NULL,
  nivel_acceso VARCHAR(20) NOT NULL,
  area_soporte VARCHAR(50) NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
  CHECK (nivel_acceso IN ('ADMIN','VALIDACION','SOPORTE')),
  CHECK (estado IN ('ACTIVO','INACTIVO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE cupon_descuento (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  codigo_cupon VARCHAR(20) NOT NULL UNIQUE,
  porcentaje_descuento DECIMAL(5,2) NOT NULL,
  fecha_expiracion DATETIME NOT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
  CHECK (porcentaje_descuento BETWEEN 0 AND 100),
  CHECK (estado IN ('ACTIVO','INACTIVO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE evento_cultural (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  descripcion TEXT NULL,
  tipo_evento VARCHAR(20) NOT NULL,
  fecha DATETIME NOT NULL,
  aforo INT NOT NULL,
  local_id INT NOT NULL,
  organizador_id INT NOT NULL,
  artista_id INT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'BORRADOR',
  CHECK (aforo > 0),
  CHECK (estado IN ('BORRADOR','PUBLICADO','INACTIVO')),
  FOREIGN KEY (local_id) REFERENCES local_concierto(id) ON DELETE RESTRICT,
  FOREIGN KEY (organizador_id) REFERENCES organizador_evento(id) ON DELETE RESTRICT,
  FOREIGN KEY (artista_id) REFERENCES artista(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE perfil_usuario (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  cliente_id INT NOT NULL UNIQUE,
  fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  estado_verificacion BOOLEAN NOT NULL DEFAULT FALSE,
  pais VARCHAR(50) NULL,
  fecha_nacimiento DATE NULL,
  FOREIGN KEY (cliente_id) REFERENCES usuario_cliente(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE preferencias_musicales (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  cliente_id INT NOT NULL UNIQUE,
  genero_favorito VARCHAR(50) NULL,
  artista_favorito VARCHAR(100) NULL,
  FOREIGN KEY (cliente_id) REFERENCES usuario_cliente(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE categoria_asiento (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  evento_id INT NOT NULL,
  nombre_zona VARCHAR(50) NOT NULL,
  precio_base DECIMAL(10,2) NOT NULL,
  aforo_total INT NOT NULL,
  aforo_disponible INT NOT NULL,
  CHECK (precio_base >= 0),
  CHECK (aforo_total > 0),
  CHECK (aforo_disponible BETWEEN 0 AND aforo_total),
  FOREIGN KEY (evento_id) REFERENCES evento_cultural(id) ON DELETE RESTRICT,
  UNIQUE (evento_id, nombre_zona)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE politica_devolucion (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  evento_id INT NOT NULL UNIQUE,
  dias_minimos_previos INT NOT NULL,
  penalizacion_porcentaje DECIMAL(5,2) NOT NULL,
  estado_activo BOOLEAN NOT NULL DEFAULT TRUE,
  CHECK (dias_minimos_previos >= 0),
  CHECK (penalizacion_porcentaje BETWEEN 0 AND 100),
  FOREIGN KEY (evento_id) REFERENCES evento_cultural(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE transaccion_compra (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  codigo_unico VARCHAR(50) NOT NULL UNIQUE,
  solicitud_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  cliente_id INT NOT NULL,
  evento_id INT NOT NULL,
  cupon_id INT NULL,
  fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  subtotal DECIMAL(10,2) NOT NULL,
  descuento DECIMAL(10,2) NOT NULL DEFAULT 0,
  igv DECIMAL(10,2) NOT NULL DEFAULT 0,
  total DECIMAL(10,2) NOT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADA',
  importe_reembolso DECIMAL(10,2) NULL,
  fecha_reembolso DATETIME NULL,
  CHECK (subtotal >= 0),
  CHECK (descuento BETWEEN 0 AND subtotal),
  CHECK (igv >= 0),
  CHECK (total = subtotal - descuento),
  CHECK (estado IN ('CONFIRMADA','DEVUELTA')),
  FOREIGN KEY (cliente_id) REFERENCES usuario_cliente(id) ON DELETE RESTRICT,
  FOREIGN KEY (evento_id) REFERENCES evento_cultural(id) ON DELETE RESTRICT,
  FOREIGN KEY (cupon_id) REFERENCES cupon_descuento(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE entrada_digital (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  codigo_entrada VARCHAR(50) CHARACTER SET ascii COLLATE ascii_bin NOT NULL UNIQUE,
  evento_id INT NOT NULL,
  categoria_id INT NOT NULL,
  comprador_id INT NOT NULL,
  estado_uso VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
  fecha_emision DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CHECK (estado_uso IN ('DISPONIBLE','UTILIZADA','ANULADA')),
  FOREIGN KEY (evento_id) REFERENCES evento_cultural(id) ON DELETE RESTRICT,
  FOREIGN KEY (categoria_id) REFERENCES categoria_asiento(id) ON DELETE RESTRICT,
  FOREIGN KEY (comprador_id) REFERENCES usuario_cliente(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE detalle_transaccion (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  transaccion_id INT NOT NULL,
  entrada_id INT NOT NULL UNIQUE,
  precio_aplicado DECIMAL(10,2) NOT NULL,
  descuento_aplicado DECIMAL(10,2) NOT NULL DEFAULT 0,
  CHECK (precio_aplicado >= 0),
  CHECK (descuento_aplicado BETWEEN 0 AND precio_aplicado),
  FOREIGN KEY (transaccion_id) REFERENCES transaccion_compra(id) ON DELETE RESTRICT,
  FOREIGN KEY (entrada_id) REFERENCES entrada_digital(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE metodo_pago (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  transaccion_id INT NOT NULL UNIQUE,
  nombre_metodo VARCHAR(50) NOT NULL,
  nro_operacion VARCHAR(50) NOT NULL UNIQUE,
  estado_transaccion VARCHAR(20) NOT NULL DEFAULT 'APROBADO',
  CHECK (estado_transaccion = 'APROBADO'),
  FOREIGN KEY (transaccion_id) REFERENCES transaccion_compra(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE comprobante_pago (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  transaccion_id INT NOT NULL UNIQUE,
  serie_correlativo VARCHAR(20) NOT NULL UNIQUE,
  tipo_comprobante VARCHAR(20) NOT NULL DEFAULT 'ACADEMICO',
  fecha_emision DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (transaccion_id) REFERENCES transaccion_compra(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE boleto_nominado (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  entrada_id INT NOT NULL UNIQUE,
  dni_asistente CHAR(8) NOT NULL,
  nombres_asistente VARCHAR(100) NOT NULL,
  apellidos_asistente VARCHAR(100) NOT NULL,
  FOREIGN KEY (entrada_id) REFERENCES entrada_digital(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE ticket_qr_dinamico (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  entrada_id INT NOT NULL UNIQUE,
  clave_totp_cifrada VARCHAR(255) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  ultimo_intervalo BIGINT NULL,
  fecha_generacion DATETIME NULL,
  fecha_expiracion DATETIME NULL,
  FOREIGN KEY (entrada_id) REFERENCES entrada_digital(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE historial_rotacion_qr (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  qr_id INT NOT NULL,
  intervalo BIGINT NOT NULL,
  hora_generacion DATETIME NOT NULL,
  hash_anterior CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NULL,
  hash_nuevo CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  FOREIGN KEY (qr_id) REFERENCES ticket_qr_dinamico(id) ON DELETE RESTRICT,
  UNIQUE (qr_id, intervalo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE dispositivo_validacion (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  operador_id INT NOT NULL,
  evento_id INT NOT NULL,
  nombre_puerta VARCHAR(50) NOT NULL,
  modelo_celular VARCHAR(50) NULL,
  token_acceso_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL UNIQUE,
  estado_dispositivo VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
  CHECK (estado_dispositivo IN ('ACTIVO','REVOCADO')),
  FOREIGN KEY (operador_id) REFERENCES administrador_plataforma(id) ON DELETE RESTRICT,
  FOREIGN KEY (evento_id) REFERENCES evento_cultural(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE control_acceso_puerta (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  solicitud_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL UNIQUE,
  solicitud_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  evento_id INT NOT NULL,
  dispositivo_id INT NOT NULL,
  operador_id INT NOT NULL,
  entrada_id INT NULL,
  hora_ingreso DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  estado_acceso VARCHAR(20) NOT NULL,
  motivo VARCHAR(100) NOT NULL,
  autorizado_por_id INT NULL,
  CHECK (estado_acceso IN ('PERMITIDO','DENEGADO')),
  FOREIGN KEY (evento_id) REFERENCES evento_cultural(id) ON DELETE RESTRICT,
  FOREIGN KEY (dispositivo_id) REFERENCES dispositivo_validacion(id) ON DELETE RESTRICT,
  FOREIGN KEY (operador_id) REFERENCES administrador_plataforma(id) ON DELETE RESTRICT,
  FOREIGN KEY (entrada_id) REFERENCES entrada_digital(id) ON DELETE RESTRICT,
  FOREIGN KEY (autorizado_por_id) REFERENCES administrador_plataforma(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE incidencia_acceso (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  control_id INT NOT NULL,
  tipo_incidencia VARCHAR(50) NOT NULL,
  descripcion TEXT NULL,
  severidad VARCHAR(20) NOT NULL,
  estado_resolucion VARCHAR(20) NOT NULL DEFAULT 'ABIERTA',
  resuelta_por_id INT NULL,
  fecha_resolucion DATETIME NULL,
  motivo_resolucion VARCHAR(200) NULL,
  FOREIGN KEY (control_id) REFERENCES control_acceso_puerta(id) ON DELETE RESTRICT,
  FOREIGN KEY (resuelta_por_id) REFERENCES administrador_plataforma(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE reclamo_soporte (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  cliente_id INT NOT NULL,
  admin_id INT NULL,
  incidencia_id INT NULL,
  asunto VARCHAR(150) NOT NULL,
  descripcion TEXT NOT NULL,
  estado_reclamo VARCHAR(20) NOT NULL DEFAULT 'ABIERTO',
  fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  respuesta TEXT NULL,
  FOREIGN KEY (cliente_id) REFERENCES usuario_cliente(id) ON DELETE RESTRICT,
  FOREIGN KEY (admin_id) REFERENCES administrador_plataforma(id) ON DELETE RESTRICT,
  FOREIGN KEY (incidencia_id) REFERENCES incidencia_acceso(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE notificacion_usuario (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  cliente_id INT NOT NULL,
  titulo_mensaje VARCHAR(100) NOT NULL,
  contenido TEXT NOT NULL,
  fecha_envio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  leido BOOLEAN NOT NULL DEFAULT FALSE,
  FOREIGN KEY (cliente_id) REFERENCES usuario_cliente(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE VIEW historial_compra AS
SELECT u.id AS cliente_id, COUNT(d.id) AS total_boletos_comprados,
       MAX(t.fecha_hora) AS ultima_compra_fecha
FROM usuario_cliente u
LEFT JOIN transaccion_compra t ON t.cliente_id=u.id AND t.estado IN ('CONFIRMADA','DEVUELTA')
LEFT JOIN detalle_transaccion d ON d.transaccion_id=t.id
GROUP BY u.id;

CREATE INDEX ix_entrada_comprador_evento ON entrada_digital(comprador_id, evento_id, estado_uso);

CREATE INDEX ix_compra_cliente_fecha ON transaccion_compra(cliente_id, fecha_hora);

CREATE INDEX ix_control_entrada_fecha ON control_acceso_puerta(entrada_id, hora_ingreso);
