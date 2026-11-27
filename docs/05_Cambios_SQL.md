# Qué cambió en la base y por qué

El diseño previo se conserva en `referencias/SQL_original_sin_modificar.txt`. El esquema vigente se define en `sql/01_esquema.sql` y se instala en una base vacía según `01_Workbench.md`. No es un script de migración ni debe ejecutarse sobre un esquema ya poblado.

| Ajuste | Motivo |
|---|---|
| 24 tablas y una vista `historial_compra` | El historial se obtiene de compras/detalles; evita que una segunda tabla quede desactualizada. Siguen existiendo 25 clases del dominio. |
| Nombres y relaciones consistentes en las 25 clases | El diccionario establece una correspondencia única entre los modelos Java y las columnas SQL. |
| Campos `contrasena_hash` y estados de cuentas | Autenticación comprobable y contraseñas protegidas; no guardar contraseñas en texto plano. |
| Entrada relacionada con evento, zona y comprador; nominación única | Permite consultar propiedad, aforo y asistente antes de mostrar/validar el QR. |
| Importes decimales, cantidades y cupos comprobados | Evita redondeos binarios y cupos negativos. La compra todavía necesita su transacción Java. |
| Código único de compra y hash de solicitud | Permite a Ethan evitar una segunda compra por reintento de la misma confirmación. |
| Comprobante académico y pago simulado | La demostración no procesa tarjetas ni emite un comprobante fiscal. El comprobante se obtiene de la transacción; no se usa el antiguo RUC del comprobante como mecanismo de facturación. |
| Clave TOTP cifrada e intervalo en `ticket_qr_dinamico` | Necesario para TOTP real; reemplaza el esquema de token aleatorio almacenado solo como hash. |
| Intervalo único en historial de QR | Una recarga o segunda pestaña no produce falsas rotaciones. |
| Dispositivo ligado a operador/evento y credencial con hash | Un visitante no puede validar entradas escogiendo un id de guardia. |
| Solicitud y hash de acceso, operador y motivo | Evita consumo doble por reintento y deja evidencia verificable del intento. |
| Entrada opcional en control de acceso | Un QR desconocido también puede registrar una denegación sin inventar una entrada. |
| Claves foráneas y restricciones únicas | Evita duplicar nominaciones, pagos, comprobantes o detalles de la misma entrada. |
| Borrado restringido | Protege el historial. Usar estados en los flujos normales; no borrar ventas y accesos por cascada. |
| Tablas futuras conservadas | Cupones, devoluciones, reclamos y notificaciones no desaparecen del proyecto, pero no requieren nuevas pantallas para esta demo. |

El SQL por sí solo no puede garantizar todas las reglas: propiedad del boleto, coherencia evento/zona, límite de cuatro entradas, descuento de stock y consumo único se verifican también en servicios transaccionales. Las dos últimas áreas tienen responsables distintos: compra pendiente de Ethan; consumo de entrada ya preparado y probado.

## Cómo aplicarlo

1. Seguir `01_Workbench.md` y crear el esquema nuevo.
2. Ejecutar `01_esquema.sql` una vez; luego `02_datos_demo.sql` una vez.
3. Ejecutar `03_comprobar.sql` y comprobar las filas indicadas en la guía.
4. Conservar el SQL original como referencia, no ejecutarlo encima del nuevo.
5. Si una importación falla a mitad, detenerse y leer el primer error. Comprobar el esquema seleccionado y evitar ejecutar borrados sin verificar su alcance.

Para revisar cada columna exacta está `Diccionario.md`; el archivo ejecutable es la referencia definitiva. Nadie debe cambiar una tabla por su cuenta después de repartir el trabajo: acordar el cambio y actualizar SQL, modelo, contratos y pruebas juntos.
