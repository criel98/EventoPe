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
          <input type="radio" name="categoriaId" value="${categoria.id}" ${categoria.aforoDisponible > 0 ? '' : 'disabled'}>
          <strong><c:out value="${categoria.nombreZona}" /></strong>
          - S/. <c:out value="${categoria.precioBase}" />
          <c:if test="${categoria.aforoDisponible <= 0}"> - Agotado</c:if>
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
</body>
</html>
