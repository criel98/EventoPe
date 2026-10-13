<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Selección de entradas | EventoPe</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
</head>
<body>
<%@ include file="/WEB-INF/views/common/cabecera.jspf" %>
<main class="contenedor">
    <h1>Revisa tu selección</h1>

    <c:if test="${not empty errorCompra}">
        <div class="tarjeta alerta" role="alert">
            <p><c:out value="${errorCompra}"/></p>
            <c:remove var="errorCompra" scope="session"/>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${empty carrito}">
            <section class="tarjeta">
                <p>Aún no has seleccionado entradas.</p>
                <a class="boton" href="<c:url value='/eventos'/>">Explorar eventos</a>
            </section>
        </c:when>
        <c:otherwise>
            <section class="tarjeta">
                <h2><c:out value="${carrito.eventoNombre}"/></h2>
                <p>Las entradas no quedan reservadas hasta completar la compra.</p>
                <ul>
                    <c:forEach var="linea" items="${carrito.lineas}">
                        <li>
                            <strong><c:out value="${linea.zona}"/></strong> —
                            <c:out value="${linea.cantidad}"/> entrada(s), S/ <c:out value="${linea.precioUnitario}"/> por entrada
                        </li>
                    </c:forEach>
                </ul>
                <p><strong>Importe de la selección: S/ <c:out value="${carrito.subtotal}"/></strong></p>
                <p><strong>Total de entradas: <c:out value="${carrito.cantidadTotal}"/> de 4</strong></p>
                <div style="display:flex; gap:12px; align-items:center; flex-wrap:wrap; margin-top:16px;">
                    <a class="boton" href="<c:url value='/compras/revision'/>">Identificar asistentes</a>
                    <form method="post" action="<c:url value='/carrito'/>" style="display:inline; margin:0;">
                        <input type="hidden" name="csrf" value="${csrf}">
                        <input type="hidden" name="accion" value="vaciar">
                        <button type="submit" style="background:#6c757d; width:auto;">Cancelar selección</button>
                    </form>
                </div>
            </section>
        </c:otherwise>
    </c:choose>

    <c:if test="${not empty sessionScope.eventoPendiente}">
        <section class="tarjeta">
            <h2>¿Cambiar de evento?</h2>
            <p>El nuevo evento es <strong><c:out value="${sessionScope.nombreEventoPendiente}"/></strong>. Se perderá el contenido actual.</p>
            <form method="post" action="<c:url value='/carrito/agregar'/>" class="formulario">
                <input type="hidden" name="csrf" value="${csrf}">
                <input type="hidden" name="eventoId" value="${sessionScope.eventoPendiente[0]}">
                <input type="hidden" name="categoriaId" value="${sessionScope.eventoPendiente[1]}">
                <input type="hidden" name="cantidad" value="${sessionScope.eventoPendiente[2]}">
                <input type="hidden" name="reemplazar" value="si">
                <button type="submit">Cambiar selección</button>
            </form>
        </section>
    </c:if>
</main>
</body>
</html>
