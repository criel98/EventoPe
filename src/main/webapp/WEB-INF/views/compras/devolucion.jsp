<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Devolución | EventoPe</title>
  <link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
  <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
</head>
<body>
<%@ include file="/WEB-INF/views/common/cabecera.jspf" %>
<main class="contenedor">
  <h1>Devolver compra completa</h1>
  <c:if test="${not empty error}"><p class="error alerta" role="alert"><c:out value="${error}"/></p></c:if>
  <c:choose>
    <c:when test="${not empty resultado}">
      <section class="tarjeta exito">
        <h2><c:out value="${resultado.yaDevuelta ? 'La compra ya estaba devuelta' : 'Devolución registrada'}"/></h2>
        <p>Código: <code><c:out value="${resultado.codigoCompra}"/></code></p>
        <p>Reembolso: S/ <strong><c:out value="${resultado.importeReembolso}"/></strong></p>
        <p>Todas las entradas disponibles de la compra fueron anuladas y el cupo fue recuperado una sola vez.</p>
      </section>
    </c:when>
    <c:otherwise>
      <form method="post" action="<c:url value='/compras/devolver'/>" class="formulario">
        <input type="hidden" name="csrf" value="${csrf}">
        <label for="transaccionId">ID de la compra</label>
        <input id="transaccionId" name="transaccionId" type="number" min="1" required value="<c:out value='${transaccionId}'/>">
        <p>Solo se puede devolver una compra propia, confirmada y con todas sus entradas disponibles.</p>
        <button type="submit">Confirmar devolución completa</button>
      </form>
    </c:otherwise>
  </c:choose>
  <p><a href="<c:url value='/entradas'/>">Volver a mis entradas</a></p>
</main>
</body>
</html>
