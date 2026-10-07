# Guía de trabajo — Semana 10

EventoPe es una aplicación de venta de entradas nominadas con QR dinámico TOTP y validación de ingreso. Esta guía organiza el desarrollo y la integración del segundo entregable.

## Alcance

- Cliente: cartelera, cuenta, compra simulada, nominación, consulta de entradas y QR dinámico.
- Personal de validación: autenticación y una pantalla para comprobar el QR, la identidad del asistente y el estado de la entrada.
- Diseño adaptable a teléfono y computadora, con prioridad para la experiencia móvil.

La central, los perfiles del personal, la mensajería y los paneles administrativos corresponden a una etapa posterior.

## Estado de implementación

La base común contiene 25 clases del dominio, esquema SQL, configuración JDBC, controles de sesión y formularios, servicios TOTP y de validación, pruebas automatizadas y estilos compartidos. La interfaz completa, la autenticación y la compra se encuentran pendientes de desarrollo e integración. Los resultados y límites de las pruebas se detallan en `docs/06_Verificacion.md`.

## Responsabilidades

| Responsable | Área | Instrucciones |
|---|---|---|
| Daniel | Catálogo y detalle de eventos | [Ficha de Daniel](docs/equipo/Daniel.md) |
| Frank | Cuenta del cliente y autenticación del guardia | [Ficha de Frank](docs/equipo/Frank.md) |
| Ethan | Carrito, compra simulada y nominación | [Ficha de Ethan](docs/equipo/Ethan.md) |
| Glenn | Boletos, QR y pantalla de validación | [Ficha de Glenn](docs/equipo/Glenn.md) |

Las responsabilidades indican trabajo asignado. La finalización de cada tarea requiere implementación, pruebas y revisión de su integración.

## Orden de trabajo

1. Preparar el entorno siguiendo `README.md` y `docs/01_Workbench.md`. Cada integrante utiliza una base local independiente. Si el entorno ya está configurado, no repetir la importación ni regenerar la clave maestra.
2. Leer `docs/03_Contratos.md` y la ficha del área asignada antes de modificar archivos.
3. Desarrollar en ramas siguiendo `docs/02_GitHub.md`; coordinar previamente los cambios en interfaces, modelos o esquema compartidos.
4. Integrar autenticación, catálogo, compra y consulta de boletos en ese orden. El panel de validación puede desarrollarse con los datos ficticios mientras se completa la compra.
5. Ejecutar el recorrido y reunir las evidencias de `docs/07_Demostracion_Semana10.md`.

El diccionario de datos se encuentra en `docs/Diccionario.md`. La actualización del informe se organiza en `docs/04_Texto_para_Word.md` y los ajustes del esquema en `docs/05_Cambios_SQL.md`.
