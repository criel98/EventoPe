<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Administración de Personal | EventoPe</title>
  <link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
</head>
<body>
  <%@ include file="/WEB-INF/views/common/cabecera.jspf" %>
  <main>
    <section class="tarjeta">
      <h1>Administración de Personal (RF-20)</h1>

      <c:if test="${not empty mensajeExito}">
        <p class="exito" role="status"><c:out value="${mensajeExito}"/></p>
      </c:if>
      <c:if test="${not empty mensajeError}">
        <p class="error" role="alert"><c:out value="${mensajeError}"/></p>
      </c:if>

      <h2>Registrar nuevo personal</h2>
      <form method="post" action="<c:url value='/admin/personal'/>" class="formulario">
        <input type="hidden" name="csrf" value="<c:out value='${csrf}'/>">
        <input type="hidden" name="accion" value="crear">

        <label for="nuevo-username">Usuario</label>
        <input id="nuevo-username" name="username" type="text" maxlength="50" required>

        <label for="nuevo-password">Contraseña (mín. 8 caracteres)</label>
        <input id="nuevo-password" name="password" type="password" minlength="8" maxlength="128" required>

        <label for="nuevo-rol">Nivel de acceso</label>
        <select id="nuevo-rol" name="nivelAcceso" required>
          <option value="SOPORTE">SOPORTE</option>
          <option value="VALIDACION">VALIDACION</option>
          <option value="ADMIN">ADMIN</option>
        </select>

        <label for="nuevo-area">Área de soporte (opcional)</label>
        <input id="nuevo-area" name="areaSoporte" type="text" maxlength="50">

        <label for="nuevo-estado">Estado</label>
        <select id="nuevo-estado" name="estado" required>
          <option value="ACTIVO">ACTIVO</option>
          <option value="INACTIVO">INACTIVO</option>
        </select>

        <button type="submit">Registrar personal</button>
      </form>
    </section>

    <section class="tarjeta">
      <h2>Personal registrado</h2>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Usuario</th>
            <th>Rol / Área / Estado</th>
            <th>Cambiar contraseña</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="p" items="${listaPersonal}">
            <tr>
              <td><c:out value="${p.id}"/></td>
              <td><c:out value="${p.username}"/></td>
              <td>
                <form method="post" action="<c:url value='/admin/personal'/>">
                  <input type="hidden" name="csrf" value="<c:out value='${csrf}'/>">
                  <input type="hidden" name="accion" value="actualizar">
                  <input type="hidden" name="id" value="<c:out value='${p.id}'/>">

                  <select name="nivelAcceso" aria-label="Nivel de acceso">
                    <option value="ADMIN" ${p.nivelAcceso == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                    <option value="SOPORTE" ${p.nivelAcceso == 'SOPORTE' ? 'selected' : ''}>SOPORTE</option>
                    <option value="VALIDACION" ${p.nivelAcceso == 'VALIDACION' ? 'selected' : ''}>VALIDACION</option>
                  </select>

                  <input type="text" name="areaSoporte" maxlength="50"
                         placeholder="Área de soporte"
                         value="${fn:escapeXml(p.areaSoporte)}">

                  <select name="estado" aria-label="Estado">
                    <option value="ACTIVO" ${p.estado == 'ACTIVO' ? 'selected' : ''}>ACTIVO</option>
                    <option value="INACTIVO" ${p.estado == 'INACTIVO' ? 'selected' : ''}>INACTIVO</option>
                  </select>

                  <button type="submit">Guardar</button>
                </form>
              </td>
              <td>
                <form method="post" action="<c:url value='/admin/personal'/>">
                  <input type="hidden" name="csrf" value="<c:out value='${csrf}'/>">
                  <input type="hidden" name="accion" value="clave">
                  <input type="hidden" name="id" value="<c:out value='${p.id}'/>">

                  <input type="password" name="nuevaPassword" minlength="8" maxlength="128"
                         placeholder="Nueva contraseña" required>
                  <button type="submit">Actualizar clave</button>
                </form>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </section>

    <%@ include file="/WEB-INF/views/common/nota_movil.jspf" %>
  </main>
</body>
</html>
