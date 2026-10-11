<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>EventoPe | Cartelera</title>
  <link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/cabecera.jspf" />
<main>
  <h1>Cartelera de eventos</h1>

  <form method="get" class="tarjeta">
    <label for="nombre">Buscar por nombre o artista</label>
    <input id="nombre" name="nombre" value="${fn:escapeXml(param.nombre)}" placeholder="Ej. Rock, Juan, Festival">
    <label for="artista">Artista</label>
    <input id="artista" name="artista" value="${fn:escapeXml(param.artista)}" placeholder="Filtro por artista">
    <label for="tipo">Tipo</label>
    <select id="tipo" name="tipo">
      <option value="">Todos</option>
      <option value="CONCIERTO" ${param.tipo == 'CONCIERTO' ? 'selected' : ''}>Concierto</option>
      <option value="TEATRO" ${param.tipo == 'TEATRO' ? 'selected' : ''}>Teatro</option>
      <option value="FESTIVAL" ${param.tipo == 'FESTIVAL' ? 'selected' : ''}>Festival</option>
    </select>
    <button type="submit">Buscar</button>
  </form>

  <c:if test="${not empty mensaje}">
    <p class="error"><c:out value="${mensaje}" /></p>
  </c:if>

  <c:choose>
    <c:when test="${empty eventos}">
      <p class="nota-movil">No se encontraron eventos publicados con esos criterios.</p>
    </c:when>
    <c:otherwise>
      <div class="rejilla">
        <c:forEach var="evento" items="${eventos}">
          <c:url var="detalleUrl" value="/eventos/detalle">
            <c:param name="id" value="${evento.id}" />
          </c:url>
          <article class="tarjeta">
            <h2><c:out value="${evento.nombre}" /></h2>
            <p><strong>Tipo:</strong> <c:out value="${evento.tipoEvento}" /></p>
            <p><strong>Artista:</strong> <c:out value="${evento.artistaNombre}" /></p>
            <p><strong>Fecha:</strong> <c:out value="${evento.fecha}" /></p>
            <p><strong>Local:</strong> <c:out value="${evento.localNombre}" /></p>
            <p><strong>Desde:</strong> <c:out value="${evento.precioDesde}" /></p>
            <p><c:out value="${evento.descripcion}" /></p>
            <a href="${detalleUrl}">Ver detalle</a>
          </article>
        </c:forEach>
      </div>
    </c:otherwise>
  </c:choose>
</main>
</body>
</html>
