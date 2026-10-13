<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mis entradas | EventoPe</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
    <style>
        .badge-estado {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 0.85rem;
            font-weight: 700;
            text-transform: uppercase;
        }
        .badge-disponible { background: #d4edda; color: #155724; }
        .badge-utilizada { background: #e2e3e5; color: #383d41; }
        .badge-anulada { background: #f8d7da; color: #721c24; }
        .entrada-card {
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }
        .entrada-info p {
            margin: 6px 0;
            color: #334155;
        }
    </style>
</head>
<body>
<%@ include file="/WEB-INF/views/common/cabecera.jspf" %>
<main>
    <h1>Mis entradas</h1>
    <p class="nota-movil">Consulta aquí tus boletos digitales para acceder a tus eventos.</p>

    <c:if test="${not empty error}">
        <p class="error" role="alert"><c:out value="${error}"/></p>
    </c:if>

    <c:choose>
        <c:when test="${empty entradas}">
            <section class="tarjeta" style="text-align: center; padding: 40px 20px;">
                <h2>No tienes entradas emitidas</h2>
                <p>Aún no has adquirido boletos o no hay entradas registradas en tu cuenta.</p>
                <div style="margin-top: 20px;">
                    <a href="<c:url value='/eventos'/>" class="boton">Explorar eventos disponibles</a>
                </div>
            </section>
        </c:when>
        <c:otherwise>
            <div class="rejilla">
                <c:forEach var="e" items="${entradas}">
                    <article class="tarjeta entrada-card">
                        <div>
                            <div style="display: flex; justify-content: space-between; align-items: flex-start; gap: 8px;">
                                <h2 style="font-size: 1.25rem; margin: 0 0 8px 0; color: #0f172a;">
                                    <c:out value="${e.eventoNombre}"/>
                                </h2>
                                <c:choose>
                                    <c:when test="${e.estadoUso == 'DISPONIBLE'}">
                                        <span class="badge-estado badge-disponible">Válida</span>
                                    </c:when>
                                    <c:when test="${e.estadoUso == 'UTILIZADA'}">
                                        <span class="badge-estado badge-utilizada">Utilizada</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge-estado badge-anulada"><c:out value="${e.estadoUso}"/></span>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div class="entrada-info">
                                <p><strong>Zona:</strong> <c:out value="${e.zona}"/></p>
                                <p><strong>Fecha:</strong> <c:out value="${e.eventoFecha}"/></p>
                                <p><strong>Lugar:</strong> <c:out value="${e.localNombre}"/> — <c:out value="${e.ciudad}"/></p>
                                <hr style="border: 0; border-top: 1px solid #f1f5f9; margin: 12px 0;">
                                <p><strong>Titular:</strong> <c:out value="${e.asistenteNombre}"/></p>
                                <p><strong>DNI:</strong> <code><c:out value="${e.asistenteDniEnmascarado}"/></code></p>
                                <p style="font-size: 0.85rem; color: #64748b;">Boleto #<c:out value="${e.id}"/> &bull; Código: <code><c:out value="${e.codigoEntrada}"/></code></p>
                            </div>
                        </div>

                        <div style="margin-top: 16px;">
                            <c:choose>
                                <c:when test="${e.estadoUso == 'DISPONIBLE'}">
                                    <a href="<c:url value='/entradas/boleto?id=${e.id}'/>" class="boton" style="display: block;">
                                        Ver boleto digital y QR
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <button type="button" disabled style="width: 100%; margin: 0;">
                                        <c:out value="${e.estadoUso == 'UTILIZADA' ? 'Boleto ya utilizado' : 'No disponible'}"/>
                                    </button>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </article>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>

    <%@ include file="/WEB-INF/views/common/nota_movil.jspf" %>
</main>
</body>
</html>
