<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html><html lang="es"><head><meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Acceso del personal de validación | EventoPe</title><link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
</head><body><%@ include file="/WEB-INF/views/common/cabecera.jspf" %>
<main><section class="tarjeta formulario"><h1>Acceso del personal de validación</h1>
<c:if test="${not empty error}"><p class="error" role="alert"><c:out value="${error}"/></p></c:if>
<p>Acceso exclusivo del personal autorizado para validar entradas.</p><form method="post" action="<c:url value='/personal/login'/>"><input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>">
<label for="username">Usuario</label><input id="username" name="username" type="text" autocomplete="username" maxlength="50" value="${fn:escapeXml(identificador)}" required>
<label for="password">Contraseña</label><input id="password" name="password" type="password" autocomplete="current-password" maxlength="128" required>
<button type="submit">Ingresar</button></form>
<c:if test="${mostrarCuentaDemo}"><aside class="cuenta-demo" aria-label="Cuenta de demostración">
<strong>Cuenta de prueba — presentación académica</strong>
<p>Usuario: <code>guardia.demo</code></p><p>Contraseña: <code>GuardiaDemo2026!</code></p>
<p>Úsala únicamente con los datos de demostración. Este recordatorio es temporal.</p>
</aside></c:if></section><%@ include file="/WEB-INF/views/common/nota_movil.jspf" %></main></body></html>
