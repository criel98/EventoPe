<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>EventoPe | Cartelera Oficial de Eventos</title>
  <link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
  <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
</head>
<body>
<%@ include file="/WEB-INF/views/common/cabecera.jspf" %>
<main>
  <!-- Hero Banner Estilo Teleticket -->
  <section class="hero-banner-teleticket">
    <span class="hero-badge-tag">&#127915; Venta Oficial de Entradas</span>
    <h1>Vive la emoción de los eventos en vivo</h1>
    <p>Conciertos de estadio, obras de teatro, festivales al aire libre y noches de comedia. Entradas 100% digitales y seguras con QR dinámico.</p>
  </section>

  <!-- Buscador Integrado -->
  <form method="get" class="search-bar-container">
    <div class="search-field-group main-search">
      <label for="nombre" class="search-label">¿Qué quieres ver?</label>
      <input id="nombre" name="nombre" maxlength="150" value="${fn:escapeXml(param.nombre)}" placeholder="Ej: Rock Fest, Stand-Up, Festival..." class="search-input">
    </div>
    <div class="search-field-group with-divider">
      <label for="artista" class="search-label">Artista / Grupo</label>
      <input id="artista" name="artista" maxlength="150" value="${fn:escapeXml(param.artista)}" placeholder="Filtrar por artista" class="search-input">
    </div>
    <div class="search-field-group with-divider">
      <label for="tipo" class="search-label">Categoría</label>
      <select id="tipo" name="tipo" class="search-select">
        <option value="">Todas las categorías</option>
        <option value="CONCIERTO" ${param.tipo == 'CONCIERTO' ? 'selected' : ''}>Concierto</option>
        <option value="TEATRO" ${param.tipo == 'TEATRO' ? 'selected' : ''}>Teatro & Drama</option>
        <option value="FESTIVAL" ${param.tipo == 'FESTIVAL' ? 'selected' : ''}>Festival</option>
      </select>
    </div>
    <button type="submit" aria-label="Buscar eventos" class="search-button-circular">
      <span style="font-size: 1.25rem;">&#128269;</span>
    </button>
  </form>

  <c:if test="${not empty mensaje}">
    <p class="error"><c:out value="${mensaje}" /></p>
  </c:if>

  <c:choose>
    <c:when test="${empty eventos}">
      <div class="tarjeta" style="text-align: center; padding: 40px 20px;">
        <h3>No se encontraron eventos</h3>
        <p class="nota-movil">No hay eventos publicados que coincidan con tus criterios de búsqueda.</p>
        <div style="margin-top: 16px;">
          <a href="<c:url value='/eventos'/>" class="boton boton-secundario">Ver toda la cartelera</a>
        </div>
      </div>
    </c:when>
    <c:otherwise>
      <div class="rejilla">
        <c:forEach var="evento" items="${eventos}">
          <c:url var="detalleUrl" value="/eventos/detalle">
            <c:param name="id" value="${evento.id}" />
          </c:url>

          <!-- Determinación inteligente del poster según el evento o categoría -->
          <c:choose>
            <c:when test="${evento.id == 10}">
              <c:set var="imgEvento" value="/assets/img/eventos/concierto_rock.jpg"/>
            </c:when>
            <c:when test="${evento.id == 11}">
              <c:set var="imgEvento" value="/assets/img/eventos/teatro_drama.jpg"/>
            </c:when>
            <c:when test="${evento.id == 12}">
              <c:set var="imgEvento" value="/assets/img/eventos/standup_humor.jpg"/>
            </c:when>
            <c:when test="${evento.id == 13}">
              <c:set var="imgEvento" value="/assets/img/eventos/festival_electro.jpg"/>
            </c:when>
            <c:when test="${evento.tipoEvento == 'TEATRO'}">
              <c:set var="imgEvento" value="/assets/img/eventos/teatro_drama.jpg"/>
            </c:when>
            <c:when test="${evento.tipoEvento == 'FESTIVAL'}">
              <c:set var="imgEvento" value="/assets/img/eventos/festival_electro.jpg"/>
            </c:when>
            <c:otherwise>
              <c:set var="imgEvento" value="/assets/img/eventos/concierto_rock.jpg"/>
            </c:otherwise>
          </c:choose>

          <article class="evento-card">
            <!-- Portada de alta calidad -->
            <div class="evento-poster-wrapper">
              <img src="<c:url value='${imgEvento}'/>" alt="${fn:escapeXml(evento.nombre)}" class="evento-poster-img" loading="lazy">
              <span class="evento-tipo-badge ${evento.tipoEvento == 'CONCIERTO' ? 'badge-concierto' : (evento.tipoEvento == 'TEATRO' ? 'badge-teatro' : 'badge-festival')}">
                <c:out value="${evento.tipoEvento}" />
              </span>
            </div>

            <!-- Cuerpo informativo -->
            <div class="evento-info-body">
              <h2 class="evento-titulo"><c:out value="${evento.nombre}" /></h2>

              <c:if test="${not empty evento.artistaNombre}">
                <div class="evento-meta-item">
                  <span>&#127908;</span>
                  <span><c:out value="${evento.artistaNombre}" /></span>
                </div>
              </c:if>

              <div class="evento-meta-item">
                <span>&#128197;</span>
                <span><c:out value="${evento.fecha}" /></span>
              </div>

              <div class="evento-meta-item">
                <span>&#128205;</span>
                <span><c:out value="${evento.localNombre}" /></span>
              </div>

              <div class="evento-precio-tag">
                <span style="font-size: 0.85rem; color: #64748b;">Precio desde:</span>
                <span class="evento-precio-valor">S/. <c:out value="${evento.precioDesde}" /></span>
              </div>

              <div style="margin-top: 14px;">
                <a href="${detalleUrl}" class="boton" style="display: flex; width: 100%;">
                  Comprar entradas &rarr;
                </a>
              </div>
            </div>
          </article>
        </c:forEach>
      </div>
    </c:otherwise>
  </c:choose>

  <%@ include file="/WEB-INF/views/common/nota_movil.jspf" %>
</main>
</body>
</html>
