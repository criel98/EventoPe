<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html><html lang="es"><head><meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Iniciar sesión | EventoPe</title><link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
</head><body><%@ include file="/WEB-INF/views/common/cabecera.jspf" %>
<main><section class="tarjeta formulario"><h1>Iniciar sesión</h1>
<c:if test="${not empty error}"><p class="error" role="alert"><c:out value="${error}"/></p></c:if>
<c:if test="${param.registro == 'ok'}"><p class="exito" role="status">Cuenta creada. Ya puedes iniciar sesión.</p></c:if><form method="post" action="<c:url value='/login'/>"><input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>">
<label for="email">Correo electrónico</label><input id="email" name="email" type="email" autocomplete="username" maxlength="100" value="${fn:escapeXml(identificador)}" required>
<label for="password">Contraseña</label><input id="password" name="password" type="password" autocomplete="current-password" maxlength="128" required>
<button type="submit">Ingresar</button></form><p>¿Todavía no tienes cuenta? <a href="<c:url value='/registro'/>">Crear cuenta</a></p>
</section><%@ include file="/WEB-INF/views/common/nota_movil.jspf" %></main></body></html>
