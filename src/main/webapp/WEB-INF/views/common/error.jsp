<%@ page contentType="text/html; charset=UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="es"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Solicitud no disponible | EventoPe</title><link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>"></head><body><main><section class="tarjeta">
<h1>No se pudo completar la solicitud</h1>
<c:choose><c:when test="${requestScope['jakarta.servlet.error.status_code'] == 404}"><p>La página o el evento solicitado no está disponible.</p></c:when><c:when test="${requestScope['jakarta.servlet.error.status_code'] == 403}"><p>No tienes permiso o el formulario ha vencido. Vuelve a abrir la página para intentarlo nuevamente.</p></c:when><c:otherwise><p>Inténtalo nuevamente. Si el problema continúa, vuelve más tarde.</p></c:otherwise></c:choose>
<p><a href="<c:url value='/eventos'/>">Volver a la cartelera</a> · <a href="<c:url value='/login'/>">Iniciar sesión</a></p></section></main></body></html>
