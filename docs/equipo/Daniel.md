# Daniel — catálogo y detalle del evento

Resultado esperado: el cliente abre la cartelera, filtra, revisa un evento y elige zona/cantidad para enviarlas al carrito. No construir administración de eventos: para esta entrega los eventos los carga el SQL demo.

## Antes de comenzar

Lee `../03_Contratos.md`. Importa la base en tu propia MySQL y abre la carpeta que contiene `pom.xml`. Los modelos y el estilo común ya existen. Directorio Java: `src/main/java/pe/edu/utp/eventope/`; vistas: `src/main/webapp/WEB-INF/views/`.

## Tareas en orden

1. Crear `dao/EventoDAO.java` y `dao/CategoriaAsientoDAO.java`. Consultar eventos PUBLICADO con local/artista y categorías del evento; filtros por nombre, artista y tipo mediante parámetros JDBC. Crear `dto/EventoResumenDTO.java` y `dto/EventoDetalleDTO.java` si la vista necesita datos unidos. No concatenar filtros del usuario en SQL. Evento borrador/inexistente no debe obtenerse cambiando el id en la dirección.
2. Crear `service/ServicioCatalogo.java`: validar id/filtros y coordinar consultas. Una búsqueda vacía devuelve mensaje amigable. El cupo mostrado es informativo: la compra vuelve a verificarlo.
3. Crear `controller/EventoServlet.java` para GET `/eventos` y `controller/DetalleEventoServlet.java` para GET `/eventos/detalle`. Pasar datos por atributos y hacer forward a `eventos/lista.jsp` y `eventos/detalle.jsp`. Usar JSTL y escapar texto con `c:out`; las JSP no contienen SQL.
4. Formulario de detalle: eventoId, categoriaId, cantidad de 1 a 4 y CSRF; destino POST `/carrito/agregar` de Ethan. Pedir `Csrf.token(session)` antes de renderizar un formulario. Mostrar agotado sin permitir selección. Usar `assets/css/app.css` y `WEB-INF/fragments/` existentes.

## Dependencias y entrega pequeña

Puedes empezar inmediatamente con el SQL demo. Entrega primero DAO/servicio, después cartelera, después detalle. Acordar con Ethan el formulario ya fijado en contratos; no inventar otra estructura de carrito. Frank proporciona sesión para las páginas de cuenta/compra; el catálogo puede ser público.

## Pruebas y checklist

- [ ] El SQL demo muestra solo el evento PUBLICADO; BORRADOR permanece oculto incluso por URL directa.
- [ ] Filtros individual/combinados/vacíos y búsqueda sin resultados funcionan.
- [ ] Un filtro con comillas no rompe SQL ni devuelve datos fuera del filtro.
- [ ] Id inválido/inexistente da respuesta controlada; no muestra stack trace.
- [ ] Precios y zonas pertenecen al evento consultado; agotados se distinguen.
- [ ] Con ancho de 360 px se lee sin desplazamiento horizontal; comprobar también escritorio.
- [ ] Agregar al carrito conserva los ids acordados; Ethan confirma la integración.
- [ ] Crear `src/test/java/pe/edu/utp/eventope/CatalogoMySqlTest.java` para visibilidad y filtros sobre una base exclusiva de prueba.
- [ ] Entregar capturas de cartelera/detalle y anotar navegador, fecha y datos de prueba para 3.8.

Cambios propios: DAO/DTO/servicio/controladores/vistas de catálogo y pruebas. Para CSS compartido coordina con el integrador; no reemplaces el archivo de todos.
