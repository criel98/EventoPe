<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html><html lang="es"><head><meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Mi cuenta | EventoPe</title><link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
</head><body><%@ include file="/WEB-INF/views/common/cabecera.jspf" %>
<main><section class="tarjeta formulario"><h1>Mi cuenta</h1>
<c:if test="${not empty error}"><p class="error" role="alert"><c:out value="${error}"/></p></c:if>
<c:if test="${param.guardado == 'ok'}"><p class="exito" role="status">Los cambios se guardaron.</p></c:if>
<c:if test="${not empty datos}"><form method="post" action="<c:url value='/cuenta/perfil'/>"><input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>"><label for="nombres">Nombres</label><input id="nombres" name="nombres" type="text" value="${fn:escapeXml(datos.nombres)}" aria-describedby="error-nombres" maxlength="100" required>
<c:if test="${not empty errores.nombres}"><p id="error-nombres" class="error" role="alert"><c:out value="${errores.nombres}"/></p></c:if>
<label for="apellidos">Apellidos</label><input id="apellidos" name="apellidos" type="text" value="${fn:escapeXml(datos.apellidos)}" aria-describedby="error-apellidos" maxlength="100" required>
<c:if test="${not empty errores.apellidos}"><p id="error-apellidos" class="error" role="alert"><c:out value="${errores.apellidos}"/></p></c:if>
<label for="dni">DNI</label><input id="dni" name="dni" type="text" value="${fn:escapeXml(datos.dni)}" aria-describedby="error-dni" maxlength="8" required inputmode="numeric" pattern="[0-9]{8}">
<c:if test="${not empty errores.dni}"><p id="error-dni" class="error" role="alert"><c:out value="${errores.dni}"/></p></c:if>
<label for="email">Correo electrónico</label><input id="email" name="email" type="email" value="${fn:escapeXml(datos.email)}" aria-describedby="error-email" maxlength="100" required>
<c:if test="${not empty errores.email}"><p id="error-email" class="error" role="alert"><c:out value="${errores.email}"/></p></c:if>
<label for="telefono">Teléfono (opcional)</label><input id="telefono" name="telefono" type="tel" value="${fn:escapeXml(datos.telefono)}" aria-describedby="error-telefono" maxlength="20">
<c:if test="${not empty errores.telefono}"><p id="error-telefono" class="error" role="alert"><c:out value="${errores.telefono}"/></p></c:if>
<label for="pais">País (opcional)</label><input id="pais" name="pais" type="text" value="${fn:escapeXml(datos.pais)}" aria-describedby="error-pais" maxlength="50">
<c:if test="${not empty errores.pais}"><p id="error-pais" class="error" role="alert"><c:out value="${errores.pais}"/></p></c:if>
<label for="fechaNacimiento">Fecha de nacimiento (opcional)</label><input id="fechaNacimiento" name="fechaNacimiento" type="date" value="${fn:escapeXml(datos.fechaNacimiento)}" aria-describedby="error-fechaNacimiento">
<c:if test="${not empty errores.fechaNacimiento}"><p id="error-fechaNacimiento" class="error" role="alert"><c:out value="${errores.fechaNacimiento}"/></p></c:if>
<label for="generoMusical">Género musical favorito (opcional)</label><input id="generoMusical" name="generoMusical" type="text" value="${fn:escapeXml(datos.generoMusical)}" aria-describedby="error-generoMusical" maxlength="50">
<c:if test="${not empty errores.generoMusical}"><p id="error-generoMusical" class="error" role="alert"><c:out value="${errores.generoMusical}"/></p></c:if>
<label for="artistaFavorito">Artista favorito (opcional)</label><input id="artistaFavorito" name="artistaFavorito" type="text" value="${fn:escapeXml(datos.artistaFavorito)}" aria-describedby="error-artistaFavorito" maxlength="100">
<c:if test="${not empty errores.artistaFavorito}"><p id="error-artistaFavorito" class="error" role="alert"><c:out value="${errores.artistaFavorito}"/></p></c:if>
<button type="submit">Guardar cambios</button></form></c:if>
</section><%@ include file="/WEB-INF/views/common/nota_movil.jspf" %></main></body></html>
