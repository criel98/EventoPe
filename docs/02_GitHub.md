# Flujo de trabajo en GitHub

El repositorio compartido conserva el código, la documentación técnica y el historial de contribuciones. Cada integrante debe trabajar sobre la misma revisión de la base común.

## Preparación

1. Abrir el repositorio cuya raíz contiene `pom.xml` y `src`.
2. Guardar o revisar los cambios locales antes de actualizar desde la rama compartida.
3. Crear una rama descriptiva para la tarea asignada, por ejemplo `feature/catalogo`, `feature/cuentas`, `feature/compras` o `feature/boletos-validacion`.
4. Consultar los contratos y la ficha del área correspondiente antes de modificar archivos compartidos.

## Entregas e integración

1. Realizar cambios pequeños y coherentes. Describir en cada commit el cambio efectivamente realizado.
2. Ejecutar `mvn clean verify` y las pruebas específicas de la funcionalidad. Documentar si las pruebas de integración requieren una base de pruebas separada.
3. Revisar el diff y los archivos nuevos antes de confirmar el commit.
4. Publicar la rama y solicitar revisión al equipo. La revisión debe incluir alcance, pruebas y dependencias de otras áreas.
5. Integrar después de resolver conflictos y comprobar el recorrido afectado. Los demás integrantes actualizan sus ramas a partir de la versión integrada.

## Contenido del repositorio

Se incluyen código fuente, POM, SQL, configuración de ejemplo, documentación técnica, fichas de tareas y pruebas. Se excluyen `target/`, archivos de configuración con credenciales locales, claves maestras y archivos temporales. La carpeta `.git` no se reemplaza ni se copia entre proyectos.

El historial debe reflejar las contribuciones reales. Las tareas asignadas no equivalen a funcionalidades terminadas; su estado se actualiza después de la revisión y de las pruebas correspondientes.
