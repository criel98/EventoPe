<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Incidencias de acceso | EventoPe</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
    <link rel="stylesheet" href="<c:url value='/assets/css/incidencias.css'/>">
</head>
<body>
<%@ include file="/WEB-INF/views/common/cabecera.jspf" %>
<main>
    <h1>Incidencias de acceso</h1>
    <p class="nota-movil">Intentos de ingreso denegados en las puertas. Cada resolución guarda quién la hizo, cuándo y por qué.</p>

    <c:if test="${not empty flashExito}"><p class="exito" role="status"><c:out value="${flashExito}"/></p></c:if>
    <c:if test="${not empty flashError}"><p class="error" role="alert"><c:out value="${flashError}"/></p></c:if>

    <form method="get" action="<c:url value='/admin/incidencias'/>" class="filtros-inc">
        <div>
            <label for="f-estado">Estado</label>
            <select id="f-estado" name="estado">
                <option value="ABIERTA" ${filtroEstado == 'ABIERTA' ? 'selected' : ''}>Abiertas</option>
                <option value="RESUELTA" ${filtroEstado == 'RESUELTA' ? 'selected' : ''}>Resueltas</option>
                <option value="DESCARTADA" ${filtroEstado == 'DESCARTADA' ? 'selected' : ''}>Descartadas</option>
                <option value="TODAS" ${filtroEstado == 'TODAS' ? 'selected' : ''}>Todas</option>
            </select>
        </div>
        <div>
            <label for="f-sev">Severidad</label>
            <select id="f-sev" name="severidad">
                <option value="" ${filtroSeveridad == '' ? 'selected' : ''}>Todas</option>
                <option value="ALTA" ${filtroSeveridad == 'ALTA' ? 'selected' : ''}>Alta</option>
                <option value="MEDIA" ${filtroSeveridad == 'MEDIA' ? 'selected' : ''}>Media</option>
                <option value="BAJA" ${filtroSeveridad == 'BAJA' ? 'selected' : ''}>Baja</option>
            </select>
        </div>
        <button type="submit">Filtrar</button>
    </form>

    <p class="resumen-inc"><strong><c:out value="${pagina.total}"/></strong> incidencia(s) · página <c:out value="${pagina.pagina}"/> de <c:out value="${pagina.totalPaginas}"/></p>

    <c:choose>
        <c:when test="${empty pagina.incidencias}">
            <section class="tarjeta" style="text-align:center; padding:32px 20px;">
                <h2>No hay incidencias con este filtro</h2>
                <p>Cuando un guardia deniegue un ingreso, aparecerá aquí.</p>
            </section>
        </c:when>
        <c:otherwise>
            <ul class="lista-inc">
                <c:forEach var="i" items="${pagina.incidencias}">
                    <li class="inc sev-${i.severidad}">
                        <div class="inc-cabecera">
                            <span class="chip sev-${i.severidad}"><c:out value="${i.severidad}"/></span>
                            <span class="chip estado-${i.estado}"><c:out value="${i.estado}"/></span>
                            <span class="inc-num">#<c:out value="${i.id}"/></span>
                        </div>
                        <h2><c:out value="${i.tipoTexto}"/></h2>
                        <p class="inc-meta"><c:out value="${i.evento}"/> · puerta <c:out value="${i.puerta}"/> · guardia <c:out value="${i.guardia}"/></p>
                        <p class="inc-meta"><c:out value="${i.fechaIntento}"/> (hora de Lima)</p>
                        <c:if test="${not i.abierta}">
                            <p class="inc-meta">Cerrada por <strong><c:out value="${i.resueltaPor}"/></strong> el <c:out value="${i.fechaResolucion}"/></p>
                        </c:if>
                        <a class="boton" href="<c:url value='/admin/incidencias/detalle?id=${i.id}'/>">Ver detalle</a>
                    </li>
                </c:forEach>
            </ul>
            <nav class="paginacion" aria-label="Paginación">
                <c:if test="${pagina.pagina > 1}">
                    <a class="boton-secundario" href="<c:url value='/admin/incidencias'><c:param name='estado' value='${filtroEstado}'/><c:param name='severidad' value='${filtroSeveridad}'/><c:param name='pagina' value='${pagina.pagina - 1}'/></c:url>">&larr; Anterior</a>
                </c:if>
                <c:if test="${pagina.pagina < pagina.totalPaginas}">
                    <a class="boton-secundario" href="<c:url value='/admin/incidencias'><c:param name='estado' value='${filtroEstado}'/><c:param name='severidad' value='${filtroSeveridad}'/><c:param name='pagina' value='${pagina.pagina + 1}'/></c:url>">Siguiente &rarr;</a>
                </c:if>
            </nav>
        </c:otherwise>
    </c:choose>
    <%@ include file="/WEB-INF/views/common/nota_movil.jspf" %>
</main>
</body>
</html>
