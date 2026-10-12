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
  <!-- Aquí agregamos tu nuevo archivo de estilos -->
  <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
</head>
<body>
<%@ include file="/WEB-INF/views/common/cabecera.jspf" %>
<main>
  <h1>Cartelera de eventos</h1>

  <!-- Formulario actualizado con el diseño en formato píldora -->
  <form method="get" class="search-bar-container">

    <!-- Campo 1: Búsqueda general -->
    <div class="search-field-group main-search">
      <label for="nombre" class="search-label">Busca tu próximo evento</label>
      <input id="nombre" name="nombre" value="${fn:escapeXml(param.nombre)}" placeholder="Buscar por evento o artistas" class="search-input">
    </div>

    <!-- Campo 2: Artista (con separador vertical) -->
    <div class="search-field-group with-divider">
      <label for="artista" class="search-label">Artista</label>
      <input id="artista" name="artista" value="${fn:escapeXml(param.artista)}" placeholder="Filtro por artista" class="search-input">
    </div>

    <!-- Campo 3: Tipo (con separador vertical) -->
    <div class="search-field-group with-divider">
      <label for="tipo" class="search-label">Categorías</label>
      <select id="tipo" name="tipo" class="search-select">
        <option value="">Todas</option>
        <option value="CONCIERTO" ${param.tipo == 'CONCIERTO' ? 'selected' : ''}>Concierto</option>
        <option value="TEATRO" ${param.tipo == 'TEATRO' ? 'selected' : ''}>Teatro</option>
        <option value="FESTIVAL" ${param.tipo == 'FESTIVAL' ? 'selected' : ''}>Festival</option>
      </select>
    </div>

    <!-- Botón de Búsqueda Circular -->
    <button type="submit" aria-label="Buscar" class="search-button-circular">
      <span style="font-size: 1.2rem;">&#128269;</span>
    </button>

  </form>

  <!-- Lógica de resultados sin modificar -->
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
