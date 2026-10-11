# Configuración de MySQL Workbench

Requisitos: MySQL Server disponible, MySQL Workbench y una cuenta con permisos para crear el esquema de desarrollo. Cada integrante utiliza una base local independiente con el mismo esquema y datos ficticios.

## Crear y comprobar la conexión

1. Abrir MySQL Workbench y seleccionar una conexión local existente.
2. Si no existe una conexión, pulsar **+** junto a MySQL Connections. Usar un nombre descriptivo, por ejemplo `EventoPe local`, host `127.0.0.1`, el puerto configurado en MySQL y el usuario correspondiente. El puerto predeterminado suele ser `3306`; si la instalación utiliza `3307`, configurar ese valor tanto en Workbench como en la aplicación.
3. Pulsar **Test Connection**. Ante un error, comprobar que el servidor esté iniciado y revisar host, puerto y credenciales. Registrar únicamente el mensaje técnico necesario para resolver el problema, sin incluir contraseñas.

## Preparar un entorno nuevo

Estos pasos se ejecutan una sola vez sobre un esquema vacío. Un entorno ya importado no necesita repetirlos.

1. Abrir una consulta y ejecutar:

```sql
CREATE DATABASE eventope_semana10 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Actualizar **SCHEMAS** y seleccionar el nuevo esquema con doble clic. Debe aparecer en negrita. Si ya existe un esquema de desarrollo, comprobar su contenido antes de utilizarlo; no borrarlo para repetir la instalación.
3. Abrir `sql/01_esquema.sql` mediante **File → Open SQL Script** y ejecutar el archivo completo con el esquema correcto seleccionado.
4. Revisar **Action Output**. Ante un error, detener la importación y resolver la causa antes de continuar; no ejecutar repetidamente el archivo sobre tablas creadas parcialmente.
5. Ejecutar `sql/02_datos_demo.sql` una sola vez.
6. Ejecutar `sql/03_comprobar.sql`. Resultado esperado: 24 tablas, una vista, dos clientes, un guardia, un evento publicado y una entrada de demostración.

## Configurar la aplicación

Copiar `config/eventope.properties.example` como `config/eventope.local.properties` y completar las propiedades según `README.md`. Si el archivo local ya contiene valores válidos, conservarlos.

El host, puerto y nombre del esquema de `db.url` deben coincidir con la conexión utilizada. Por ejemplo, para un esquema llamado `eventope` en el puerto `3307`:

```properties
db.url=jdbc:mysql://127.0.0.1:3307/eventope?connectionTimeZone=UTC&forceConnectionTimeZoneToSession=true
```

El archivo local contiene credenciales y está excluido de Git. Cada entorno mantiene su propia configuración y clave maestra; no se comparten contraseñas en el repositorio.

`referencias/SQL_original_sin_modificar.txt` conserva el diseño previo como referencia documental. El script ejecutable vigente es `sql/01_esquema.sql`; no constituye una migración sobre bases existentes.
