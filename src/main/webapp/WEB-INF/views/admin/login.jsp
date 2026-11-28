<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Administración de cupones | EventoPe</title>
  <link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
</head>
<body>
<%@ include file="/WEB-INF/views/common/cabecera.jspf" %>
<main class="contenedor">
  <section class="tarjeta formulario">
    <h1>Administración de cupones y devoluciones</h1>
    <c:if test="${not empty error}"><p class="error alerta" role="alert"><c:out value="${error}"/></p></c:if>
    <form method="post" action="<c:url value='/admin/ethan/login'/>">
      <input type="hidden" name="csrf" value="<c:out value='${csrf}'/>">
      <label for="username">Usuario administrador</label>
      <input id="username" name="username" type="text" maxlength="50" autocomplete="username"
             value="<c:out value='${username}'/>" required>
      <label for="password">Contraseña</label>
      <input id="password" name="password" type="password" maxlength="128"
             autocomplete="current-password" required>
      <button type="submit">Ingresar</button>
    </form>
    <p><a href="<c:url value='/eventos'/>">Volver a eventos</a></p>
  </section>
</main>
</body>
</html>
