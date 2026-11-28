<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Cupones y devoluciones | EventoPe</title>
  <link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
  <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
</head>
<body>
<%@ include file="/WEB-INF/views/common/cabecera.jspf" %>
<main class="contenedor">
  <h1>Administración de Ethan</h1>
  <c:if test="${not empty mensajeEthan}"><p class="exito alerta"><c:out value="${mensajeEthan}"/></p></c:if>
  <c:if test="${not empty error}"><p class="error alerta"><c:out value="${error}"/></p></c:if>

  <section class="tarjeta">
    <h2>Crear o actualizar cupón</h2>
    <form method="post" action="<c:url value='/admin/ethan'/>" class="formulario">
      <input type="hidden" name="csrf" value="${csrf}">
      <input type="hidden" name="accion" value="cupon">
      <label>Código <input name="codigo" maxlength="20" pattern="[A-Za-z0-9_-]+" required></label>
      <label>Porcentaje <input name="porcentaje" type="number" min="0" max="100" step="0.01" required></label>
      <label>Expiración <input name="expiracion" type="datetime-local" required></label>
      <label><input name="activo" type="checkbox" checked> Activo</label>
      <button type="submit">Guardar cupón</button>
    </form>
  </section>

  <section class="tarjeta">
    <h2>Política de devolución</h2>
    <form method="post" action="<c:url value='/admin/ethan'/>" class="formulario">
      <input type="hidden" name="csrf" value="${csrf}">
      <input type="hidden" name="accion" value="politica">
      <label>ID de evento <input name="evento" type="number" min="1" required></label>
      <label>Días mínimos previos <input name="dias" type="number" min="0" required></label>
      <label>Penalización (%) <input name="penalizacion" type="number" min="0" max="100" step="0.01" required></label>
      <label><input name="activa" type="checkbox" checked> Activa</label>
      <button type="submit">Guardar política</button>
    </form>
  </section>

  <section class="tarjeta">
    <h2>Cupones registrados</h2>
    <table>
      <thead><tr><th>Código</th><th>Descuento</th><th>Vence</th><th>Estado</th></tr></thead>
      <tbody>
      <c:forEach var="c" items="${cupones}">
        <tr>
          <td><code><c:out value="${c.codigo}"/></code></td>
          <td><c:out value="${c.porcentaje}"/>%</td>
          <td><c:out value="${c.expiracion}"/></td>
          <td><c:out value="${c.estado}"/></td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <p>Para actualizar un cupón, vuelve a guardar su mismo código con el nuevo porcentaje, vencimiento y estado.</p>
  </section>

  <section class="tarjeta">
    <h2>Políticas registradas</h2>
    <table>
      <thead><tr><th>Evento</th><th>ID</th><th>Plazo mínimo</th><th>Penalización</th><th>Estado</th></tr></thead>
      <tbody>
      <c:forEach var="p" items="${politicas}">
        <tr>
          <td><c:out value="${p.evento}"/></td>
          <td><c:out value="${p.eventoId}"/></td>
          <td><c:out value="${p.dias}"/> días</td>
          <td><c:out value="${p.penalizacion}"/>%</td>
          <td><c:out value="${p.activa ? 'Activa' : 'Inactiva'}"/></td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <p>Para actualizar una política, guarda de nuevo el mismo ID de evento.</p>
  </section>
</main>
</body>
</html>
