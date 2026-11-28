<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Administrar eventos | EventoPe</title>
<%@ include file="/WEB-INF/views/common/bootstrap.jspf" %></head>
<body class="bg-light">
<%@ include file="/WEB-INF/views/common/navbar.jspf" %>
<main class="container pb-5">
  <h1 class="h3 mb-3">Gestión de eventos</h1>
  <%@ include file="/WEB-INF/views/common/alertas.jspf" %>
  <section class="card card-body mb-4">
    <h2 class="h5"><c:choose><c:when test="${not empty eventoEditar}">Editar evento</c:when><c:otherwise>Crear evento (borrador)</c:otherwise></c:choose></h2>
    <form method="post" action="<c:url value='/admin/eventos'/>" class="row">
      <input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>"><input type="hidden" name="accion" value="guardar">
      <input type="hidden" name="id" value="${eventoEditar.id}">
      <div class="col-md-6"><c:set var="campoNombre" value="nombre"/><c:set var="campoEtiqueta" value="Nombre"/><c:set var="campoTipo" value="text"/><c:set var="campoValor" value="${eventoEditar.nombre}"/><c:set var="campoMin" value=""/><c:set var="campoStep" value=""/><%@ include file="/WEB-INF/views/common/campo.jspf" %></div>
      <div class="col-md-6"><c:set var="campoNombre" value="fecha"/><c:set var="campoEtiqueta" value="Fecha y hora"/><c:set var="campoTipo" value="datetime-local"/><c:set var="campoValor" value="${fechaEdicion}"/><%@ include file="/WEB-INF/views/common/campo.jspf" %></div>
      <div class="col-md-4 mb-3"><label class="form-label" for="tipoEvento">Categoría</label><select class="form-select" id="tipoEvento" name="tipoEvento" required>
        <option value="CONCIERTO" ${eventoEditar.tipoEvento == 'CONCIERTO' ? 'selected' : ''}>Concierto</option><option value="TEATRO" ${eventoEditar.tipoEvento == 'TEATRO' ? 'selected' : ''}>Teatro</option><option value="FESTIVAL" ${eventoEditar.tipoEvento == 'FESTIVAL' ? 'selected' : ''}>Festival</option></select></div>
      <div class="col-md-4"><c:set var="campoNombre" value="aforo"/><c:set var="campoEtiqueta" value="Aforo del evento"/><c:set var="campoTipo" value="number"/><c:set var="campoValor" value="${eventoEditar.aforo}"/><c:set var="campoMin" value="1"/><c:set var="campoStep" value="1"/><%@ include file="/WEB-INF/views/common/campo.jspf" %></div>
      <div class="col-md-4 mb-3"><label class="form-label" for="localId">Local</label><select class="form-select" id="localId" name="localId" required><c:forEach var="local" items="${locales}"><option value="${local.id}" ${eventoEditar.localId == local.id ? 'selected' : ''}><c:out value="${local.nombreLocal}"/> · capacidad <c:out value="${local.capacidadMax}"/></option></c:forEach></select></div>
      <div class="col-md-6 mb-3"><label class="form-label" for="organizadorId">Organizador</label><select class="form-select" id="organizadorId" name="organizadorId" required><c:forEach var="organizador" items="${organizadores}"><option value="${organizador.id}" ${eventoEditar.organizadorId == organizador.id ? 'selected' : ''}><c:out value="${organizador.razonSocial}"/></option></c:forEach></select></div>
      <div class="col-md-6 mb-3"><label class="form-label" for="artistaId">Artista (opcional)</label><select class="form-select" id="artistaId" name="artistaId"><option value="0">Sin artista</option><c:forEach var="artista" items="${artistas}"><option value="${artista.id}" ${eventoEditar.artistaId == artista.id ? 'selected' : ''}><c:out value="${artista.nombreArtistico}"/></option></c:forEach></select></div>
      <div class="col-12 mb-3"><label class="form-label" for="descripcion">Descripción</label><textarea class="form-control" id="descripcion" name="descripcion" rows="3"><c:out value="${eventoEditar.descripcion}"/></textarea></div>
      <div class="col-12"><button class="btn btn-primary" type="submit">Guardar borrador</button><c:if test="${not empty eventoEditar}"><a class="btn btn-outline-secondary ms-2" href="<c:url value='/admin/eventos'/>">Cancelar edición</a></c:if></div>
    </form>
  </section>
  <section class="card card-body"><h2 class="h5">Eventos existentes</h2><div class="table-responsive"><table class="table table-striped align-middle"><thead><tr><th>Evento</th><th>Fecha</th><th>Estado</th><th>Gestión</th></tr></thead><tbody>
  <c:forEach var="evento" items="${eventos}"><tr><td><c:out value="${evento.nombre}"/></td><td><c:out value="${evento.fecha}"/></td><td><span class="badge text-bg-secondary"><c:out value="${evento.estado}"/></span></td><td class="d-flex flex-wrap gap-1">
    <a class="btn btn-sm btn-outline-primary" href="<c:url value='/admin/eventos'><c:param name='editar' value='${evento.id}'/></c:url>">Editar</a>
    <a class="btn btn-sm btn-outline-secondary" href="<c:url value='/admin/eventos/zonas'><c:param name='eventoId' value='${evento.id}'/></c:url>">Zonas/precios</a>
    <c:if test="${evento.estado == 'BORRADOR'}"><form method="post" action="<c:url value='/admin/eventos'/>"><input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>"><input type="hidden" name="accion" value="publicar"><input type="hidden" name="id" value="${evento.id}"><button class="btn btn-sm btn-success" type="submit">Publicar</button></form></c:if>
    <c:if test="${evento.estado == 'PUBLICADO'}"><form method="post" action="<c:url value='/admin/eventos'/>"><input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>"><input type="hidden" name="accion" value="desactivar"><input type="hidden" name="id" value="${evento.id}"><button class="btn btn-sm btn-warning" type="submit">Desactivar</button></form></c:if>
    <form method="post" action="<c:url value='/admin/eventos'/>"><input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>"><input type="hidden" name="accion" value="eliminar"><input type="hidden" name="id" value="${evento.id}"><button class="btn btn-sm btn-outline-danger" type="submit">Borrar</button></form>
  </td></tr></c:forEach>
  </tbody></table></div></section>
</main></body></html>
