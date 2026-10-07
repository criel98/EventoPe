# Cambios puntuales para el informe

Plan de actualización del informe académico: conservar los cinco módulos y los veinte RF del proyecto completo, diferenciando el alcance de Semana 10. Los cambios siguientes están pendientes de incorporación al documento. El estado de cada requisito debe sustentarse en implementación y pruebas.

## 1. Añadir al alcance de Semana 10

«Para el segundo entregable se implementará el recorrido del cliente: consulta de eventos, registro e inicio de sesión, compra simulada, nominación de entradas, consulta de boletos y visualización del QR dinámico TOTP. Se incorporará una única pantalla del personal de validación para demostrar el escaneo, la comprobación de identidad y el consumo de la entrada. Esta pantalla requiere autenticación mínima del operador. El perfil del personal, la mensajería con central y los paneles de administración y soporte se mantienen en el alcance final y no forman parte de esta demostración. La interfaz se diseña principalmente para teléfonos y se adapta a computadoras.»

## 2. Sustituir RNF-01

«Seguridad: utilizar HTTPS. Generar credenciales TOTP conforme a RFC 6238 con HMAC-SHA256, ocho dígitos e intervalos UTC de treinta segundos. Cada entrada dispone de una clave secreta aleatoria propia, almacenada cifrada mediante AES-256-GCM; la clave maestra se conserva fuera del código y del repositorio. El navegador recibe únicamente la credencial temporal y su expiración. Las contraseñas se almacenan con PBKDF2-HMAC-SHA256, sal aleatoria y 600000 iteraciones. Las claves, contraseñas, credenciales temporales y DNI no deben aparecer en registros de aplicación.»

El diseño de token aleatorio renovado y almacenamiento de su hash es distinto del mecanismo TOTP adoptado. Un hash no permite recuperar la clave requerida por el cálculo; por eso esa clave se cifra. El hash del contenido del QR sirve para auditoría, no reemplaza la clave.

## 3. Precisar RF-09 y RF-10

- RF-09: «Generar la credencial TOTP de cada entrada nominada y mostrarla como QR, con vigencia hasta el final del intervalo UTC actual de treinta segundos. Renovar la vista mientras esté abierta y ocultar el QR cuando expire. La validación utiliza la hora del servidor y acepta únicamente el intervalo vigente.»
- RF-10: «Registrar la primera emisión y cada intervalo distinto cuya credencial se entregue al cliente, sin duplicar registros por recargas o pestañas simultáneas. No se generan registros de intervalos en los que no se solicitó el QR.»

En OE-04 y RNF-03 reemplazar “token” por “credencial TOTP” cuando se refieran al QR. Conservar la meta de rendimiento como prueba pendiente: las pruebas actuales no acreditan las treinta mediciones de menos de tres segundos.

## 4. Ajustar texto, UML y diagrama de base de datos

Usar `docs/Diccionario.md` y `sql/01_esquema.sql` como referencia exacta, no solo cambiar el dibujo del QR.

- `TicketQR_Dinamico` pasa a `TicketQRDinamico`. Campos: `id`, `entradaId`, `claveTotpCifrada`, `ultimoIntervalo`, `fechaGeneracion`, `fechaExpiracion`. Sustituir el antiguo `tokenHash`/`qrHash` que representaba el token vigente.
- `HistorialRotacionQR`: `id`, `qrId`, `intervalo`, `horaGeneracion`, `hashAnterior`, `hashNuevo`. La combinación QR/intervalo es única.
- `HistorialCompra` representa una vista SQL calculada; no una tabla que se actualiza a mano ni una entidad con id independiente.
- Contraseñas de cliente/personal se llaman `contrasenaHash`. Valores monetarios Java son `BigDecimal`; las relaciones usan ids compatibles con el SQL.
- Revisar en el diccionario la relación entrada–zona–comprador, nominación única por entrada, detalle único por entrada, pago/comprobante único por compra y auditoría de dispositivo/operador/solicitud de acceso.
- Los métodos de negocio que el diagrama asignaba a cada entidad deben explicarse como operaciones de los servicios; los modelos contienen datos. Documentar únicamente los métodos implementados y distinguir las operaciones planificadas.
- Mantener los cinco módulos conceptuales. Las 25 clases del dominio no incluyen DTO, DAO, filtros ni servicios; estos son clases técnicas adicionales.

En el texto de la pantalla del boleto, sustituir la referencia a `tokenHash` por la clave TOTP cifrada y expiración. En la pantalla de acceso, el servicio implementado se llama `ServicioAcceso.validar`, no `ServicioOperacion.validarIngreso`. No presentar resolución de incidencias o ingreso manual como funciones de esta pantalla de Semana 10.

## 5. Sección 3.7 — texto para adaptar al finalizar la integración

«EventoPe utiliza Java, Maven, Servlet, JSP/JSTL, JDBC y MySQL. Las solicitudes llegan a controladores Servlet; estos verifican la sesión y reciben los datos del formulario. Los servicios coordinan las reglas del negocio y sus transacciones. Los DAO ejecutan consultas parametrizadas mediante JDBC; los modelos representan los datos y los DTO transportan resultados específicos hacia las vistas JSP. La lógica de compra, emisión del QR y acceso se mantiene fuera de las páginas. Las vistas se adaptan a teléfono y computadora.»

«La base común ya incorpora servicios de emisión TOTP y validación de accesos, además de configuración, protección de sesión y formularios. Los servicios actúan como punto de entrada a esos casos de uso. Las pantallas y controladores del cliente y del guardia se incorporan durante el trabajo del equipo.»

Actualizar ese segundo párrafo cuando se terminen las pantallas. No afirmar que existe una clase Facade si no se creó; si se describe el patrón, explicar qué servicio concreto encapsula el caso de uso. No mencionar Hibernate/JPA como acceso a datos de esta versión.

## 6. Sección 3.8 y anexos

Usar `07_Demostracion_Semana10.md` para capturas y fragmentos reales. El SQL de datos demo no demuestra que la compra funcione por pantalla. Diferenciar “pruebas automatizadas del servicio” y “pruebas del recorrido en navegador”. Dejar pendientes los resultados aún no medidos. Las veinte pruebas de acceso del objetivo final y las pruebas de compradores concurrentes deben ejecutarse con su protocolo; no sustituirlas por el número de tests de la base.

Agregar al anexo de trazabilidad una columna “Semana 10”: implementado y probado / parcial / posterior. Mantener los códigos RF existentes; no renumerar ni eliminar los que quedaron para la entrega final. Actualizar las figuras UML y ER junto con los párrafos: las figuras y el texto deben describir la misma versión del sistema.
