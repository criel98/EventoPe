<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title><c:out value="${titulo}"/> | EventoPe</title>
<%@ include file="/WEB-INF/views/common/bootstrap.jspf" %></head><body class="bg-light">
<%@ include file="/WEB-INF/views/common/navbar.jspf" %>
<main class="container pb-5"><h1 class="h3"><c:out value="${titulo}"/></h1><%@ include file="/WEB-INF/views/common/alertas.jspf" %>
<div class="row g-4"><section class="col-lg-5"><div class="card card-body"><h2 class="h5">Agregar / editar</h2>
<form method="post" action="<c:url value='/admin/eventos/${seccion}'/>"><input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>"><input type="hidden" name="accion" value="guardar"><input type="hidden" name="id" value="${param.editar}">
<c:choose>
<c:when test="${seccion == 'organizadores'}">
  <c:set var="campoNombre" value="ruc"/><c:set var="campoEtiqueta" value="RUC (11 dígitos)"/><c:set var="campoTipo" value="text"/><c:set var="campoValor" value="${elemento.ruc}"/><c:set var="campoMin" value=""/><c:set var="campoStep" value=""/><%@ include file="/WEB-INF/views/common/campo.jspf" %>
  <c:set var="campoNombre" value="razonSocial"/><c:set var="campoEtiqueta" value="Razón social"/><c:set var="campoTipo" value="text"/><c:set var="campoValor" value="${elemento.razonSocial}"/><%@ include file="/WEB-INF/views/common/campo.jspf" %>
  <c:set var="campoNombre" value="emailContacto"/><c:set var="campoEtiqueta" value="Correo de contacto"/><c:set var="campoTipo" value="email"/><c:set var="campoValor" value="${elemento.emailContacto}"/><%@ include file="/WEB-INF/views/common/campo.jspf" %>
  <c:set var="campoNombre" value="telefono"/><c:set var="campoEtiqueta" value="Teléfono"/><c:set var="campoTipo" value="tel"/><c:set var="campoValor" value="${elemento.telefono}"/><%@ include file="/WEB-INF/views/common/campo.jspf" %>
</c:when>
<c:when test="${seccion == 'artistas'}">
  <c:set var="campoNombre" value="nombreArtistico"/><c:set var="campoEtiqueta" value="Nombre artístico"/><c:set var="campoTipo" value="text"/><c:set var="campoValor" value="${elemento.nombreArtistico}"/><c:set var="campoMin" value=""/><c:set var="campoStep" value=""/><%@ include file="/WEB-INF/views/common/campo.jspf" %>
  <c:set var="campoNombre" value="generoMusical"/><c:set var="campoEtiqueta" value="Género musical"/><c:set var="campoTipo" value="text"/><c:set var="campoValor" value="${elemento.generoMusical}"/><%@ include file="/WEB-INF/views/common/campo.jspf" %>
  <c:set var="campoNombre" value="paisOrigen"/><c:set var="campoEtiqueta" value="País de origen"/><c:set var="campoTipo" value="text"/><c:set var="campoValor" value="${elemento.paisOrigen}"/><%@ include file="/WEB-INF/views/common/campo.jspf" %>
  <c:set var="campoNombre" value="redSocial"/><c:set var="campoEtiqueta" value="Red social"/><c:set var="campoTipo" value="url"/><c:set var="campoValor" value="${elemento.redSocial}"/><%@ include file="/WEB-INF/views/common/campo.jspf" %>
</c:when>
<c:otherwise>
  <c:set var="campoNombre" value="nombreLocal"/><c:set var="campoEtiqueta" value="Nombre del local"/><c:set var="campoTipo" value="text"/><c:set var="campoValor" value="${elemento.nombreLocal}"/><c:set var="campoMin" value=""/><c:set var="campoStep" value=""/><%@ include file="/WEB-INF/views/common/campo.jspf" %>
  <c:set var="campoNombre" value="direccion"/><c:set var="campoEtiqueta" value="Dirección"/><c:set var="campoTipo" value="text"/><c:set var="campoValor" value="${elemento.direccion}"/><%@ include file="/WEB-INF/views/common/campo.jspf" %>
  <c:set var="campoNombre" value="capacidadMax"/><c:set var="campoEtiqueta" value="Capacidad máxima"/><c:set var="campoTipo" value="number"/><c:set var="campoValor" value="${elemento.capacidadMax}"/><c:set var="campoMin" value="1"/><c:set var="campoStep" value="1"/><%@ include file="/WEB-INF/views/common/campo.jspf" %>
  <c:set var="campoNombre" value="ciudad"/><c:set var="campoEtiqueta" value="Ciudad"/><c:set var="campoTipo" value="text"/><c:set var="campoValor" value="${elemento.ciudad}"/><%@ include file="/WEB-INF/views/common/campo.jspf" %>
</c:otherwise></c:choose><button class="btn btn-primary" type="submit">Guardar</button></form></div></section>
<section class="col-lg-7"><div class="card card-body"><h2 class="h5">Registros existentes</h2><div class="table-responsive"><table class="table table-striped"><thead><tr><th>Detalle</th><th>Acciones</th></tr></thead><tbody>
<c:forEach var="item" items="${elementos}"><tr><td><c:choose><c:when test="${seccion == 'organizadores'}"><c:out value="${item.razonSocial}"/> · <c:out value="${item.ruc}"/></c:when><c:when test="${seccion == 'artistas'}"><c:out value="${item.nombreArtistico}"/></c:when><c:otherwise><c:out value="${item.nombreLocal}"/> · <c:out value="${item.ciudad}"/></c:otherwise></c:choose></td><td class="d-flex gap-2"><a class="btn btn-sm btn-outline-primary" href="<c:url value='/admin/eventos/${seccion}'><c:param name='editar' value='${item.id}'/></c:url>">Editar</a><form method="post" action="<c:url value='/admin/eventos/${seccion}'/>"><input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>"><input type="hidden" name="accion" value="eliminar"><input type="hidden" name="id" value="${item.id}"><button class="btn btn-sm btn-outline-danger" type="submit">Borrar</button></form></td></tr></c:forEach>
</tbody></table></div></div></section></div></main></body></html>
