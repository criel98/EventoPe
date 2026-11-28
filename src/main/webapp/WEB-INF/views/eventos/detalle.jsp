<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>EventoPe | <c:out value="${evento.nombre}" /></title>
  <link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
</head>
<body>
<%@ include file="/WEB-INF/views/common/cabecera.jspf" %>
<main>
  <c:url var="apiEventoUrl" value="/api/eventos/${evento.id}"/>
  <a href="<c:url value='/eventos'/>">← Volver a la cartelera</a>
  <article class="tarjeta">
    <h1><c:out value="${evento.nombre}" /></h1>
    <p><strong>Tipo:</strong> <c:out value="${evento.tipoEvento}" /></p>
    <p><strong>Fecha:</strong> <c:out value="${evento.fecha}" /></p>
    <p><strong>Local:</strong> <c:out value="${evento.localNombre}" /> - <c:out value="${evento.ciudad}" /></p>
    <p><strong>Artista:</strong> <c:out value="${evento.artistaNombre}" /></p>
    <p><strong>Aforo:</strong> <c:out value="${evento.aforo}" /></p>
    <p><c:out value="${evento.descripcion}" /></p>
  </article>

  <section class="tarjeta">
    <h2>Selecciona tu zona</h2>
    <form method="post" action="<c:url value='/carrito/agregar'/>" class="formulario">
      <input type="hidden" name="eventoId" value="${evento.id}">
      <input type="hidden" name="csrf" value="${csrf}">
      <c:forEach var="categoria" items="${evento.categorias}">
        <label>
          <input id="zona-${categoria.id}" type="radio" name="categoriaId" value="${categoria.id}" ${categoria.aforoDisponible > 0 ? '' : 'disabled'}>
          <strong><c:out value="${categoria.nombreZona}" /></strong>
          - S/. <span id="precio-${categoria.id}"><c:out value="${categoria.precioBase}" /></span>
          <span id="disponibilidad-${categoria.id}"><c:choose><c:when test="${categoria.aforoDisponible > 0}">Cupos disponibles: <c:out value="${categoria.aforoDisponible}" /></c:when><c:otherwise>Agotado</c:otherwise></c:choose></span>
        </label>
      </c:forEach>
      <label for="cantidad">Cantidad</label>
      <select id="cantidad" name="cantidad">
        <option value="1">1</option>
        <option value="2">2</option>
        <option value="3">3</option>
        <option value="4">4</option>
      </select>
      <button type="submit">Agregar al carrito</button>
    </form>
  </section>
</main>
  <p id="actualizacion-zonas" role="status" aria-live="polite"></p>
  <script>
    fetch('<c:out value="${apiEventoUrl}"/>', {headers: {Accept: 'application/json'}})
      .then(response => {
        if (!response.ok) throw new Error('No fue posible consultar la disponibilidad actual.');
        return response.json();
      })
      .then(evento => evento.zonas.forEach(zona => {
        const radio = document.getElementById('zona-' + zona.id);
        const disponibilidad = document.getElementById('disponibilidad-' + zona.id);
        const precio = document.getElementById('precio-' + zona.id);
        if (!radio || !disponibilidad || !precio) return;
        radio.disabled = zona.disponibilidad <= 0;
        disponibilidad.textContent = zona.disponibilidad > 0
          ? `Cupos disponibles: ${zona.disponibilidad}`
          : 'Agotado';
        precio.textContent = zona.precio;
      }))
      .catch(() => {
        const estado = document.getElementById('actualizacion-zonas');
        if (estado) estado.textContent = 'No se pudo actualizar la disponibilidad; se muestran los últimos datos cargados.';
      });
  </script>
</body>
</html>
