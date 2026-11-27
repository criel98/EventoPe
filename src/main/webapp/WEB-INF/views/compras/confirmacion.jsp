<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Compra confirmada | EventoPe</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
</head>
<body>
<%@ include file="/WEB-INF/views/common/cabecera.jspf" %>
<main class="contenedor">
    <section class="tarjeta">
        <h1>Compra confirmada</h1>
        <p class="exito"><strong>La compra se guardó correctamente.</strong></p>
        <dl>
            <dt>Evento</dt>
            <dd><strong><c:out value="${compra.evento}"/></strong></dd>
            <dt>Código de compra</dt>
            <dd><code><c:out value="${compra.codigo}"/></code></dd>
            <dt>Comprobante académico</dt>
            <dd><code><c:out value="${compra.comprobante}"/></code></dd>
            <dt>Entradas</dt>
            <dd><c:out value="${compra.cantidad}"/></dd>
            <dt>Total</dt>
            <dd><strong>S/ <c:out value="${compra.total}"/></strong></dd>
            <dt>Pago</dt>
            <dd>Simulado — aprobado para fines académicos</dd>
        </dl>
        <p>Guarda el código de compra para tu referencia.</p>
        <div style="display:flex; gap:12px; margin-top:16px; flex-wrap:wrap;">
            <a class="boton" href="<c:url value='/entradas'/>">Ver mis entradas</a>
            <a class="boton" style="background:#4c5870;" href="<c:url value='/eventos'/>">Volver a eventos</a>
        </div>
    </section>
</main>
</body>
</html>
