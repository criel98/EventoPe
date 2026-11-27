<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mis notificaciones | EventoPe</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
    <style>
        .barra-bandeja { display: flex; flex-wrap: wrap; gap: 8px; align-items: center; justify-content: space-between; margin-bottom: 16px; }
        .filtros a { display: inline-block; padding: 8px 14px; border-radius: 999px; text-decoration: none; font-weight: 600; font-size: 0.9rem; color: #334155; background: #e2e8f0; min-height: 44px; line-height: 28px; }
        .filtros a.activo { background: #4f46e5; color: #ffffff; }
        .lista-notificaciones { list-style: none; padding: 0; margin: 0; display: grid; gap: 12px; }
        .notificacion { background: #ffffff; border: 1px solid #e2e8f0; border-left: 6px solid #94a3b8; border-radius: 12px; padding: 14px 16px; }
        .notificacion.no-leida { background: #f8fafc; border-color: #c7d2fe; box-shadow: 0 1px 6px rgba(79, 70, 229, 0.12); }
        .notificacion.compra { border-left-color: #4f46e5; }
        .notificacion.validacion { border-left-color: #166438; }
        .notificacion.seguridad { border-left-color: #b42318; }
        .notificacion h2 { font-size: 1.05rem; margin: 6px 0; }
        .notificacion p { margin: 6px 0; color: #334155; }
        .etiqueta-tipo { display: inline-block; font-size: 0.75rem; font-weight: 700; text-transform: uppercase; padding: 2px 8px; border-radius: 999px; background: #e2e8f0; color: #334155; }
        .etiqueta-tipo.compra { background: #e0e7ff; color: #3730a3; }
        .etiqueta-tipo.validacion { background: #d1fae5; color: #065f46; }
        .etiqueta-tipo.seguridad { background: #fee2e2; color: #991b1b; }
        .pie-notificacion { display: flex; flex-wrap: wrap; gap: 8px; align-items: center; justify-content: space-between; margin-top: 8px; font-size: 0.85rem; color: #64748b; }
        .pie-notificacion form { margin: 0; }
    </style>
</head>
<body>
<%@ include file="/WEB-INF/views/common/cabecera.jspf" %>
<main>
    <h1>Mis notificaciones</h1>
    <p class="nota-movil">Avisos sobre tus compras, el uso de tus entradas en la puerta y la seguridad de tu cuenta. Solo tú puedes verlos.</p>

    <div class="barra-bandeja">
        <nav class="filtros" aria-label="Filtro de notificaciones">
            <a href="<c:url value='/notificaciones'/>" class="${soloNoLeidas ? '' : 'activo'}">Todas</a>
            <a href="<c:url value='/notificaciones?filtro=noleidas'/>" class="${soloNoLeidas ? 'activo' : ''}">Sin leer (<c:out value="${noLeidas}"/>)</a>
        </nav>
        <c:if test="${noLeidas > 0}">
            <form method="post" action="<c:url value='/notificaciones/leer-todas'/>" style="margin:0;">
                <input type="hidden" name="csrf" value="<c:out value='${csrf}'/>">
                <c:if test="${soloNoLeidas}"><input type="hidden" name="filtro" value="noleidas"></c:if>
                <button type="submit" class="boton-secundario">Marcar todas como leídas</button>
            </form>
        </c:if>
    </div>

    <c:choose>
        <c:when test="${empty notificaciones}">
            <section class="tarjeta" style="text-align:center; padding: 32px 20px;">
                <h2>${soloNoLeidas ? 'No tienes notificaciones sin leer' : 'Aún no tienes notificaciones'}</h2>
                <p>Cuando compres entradas o se use una de ellas en la puerta, te avisaremos aquí.</p>
            </section>
        </c:when>
        <c:otherwise>
            <ul class="lista-notificaciones">
                <c:forEach var="n" items="${notificaciones}">
                    <li class="notificacion ${n.tipoClase} ${n.leido ? '' : 'no-leida'}">
                        <span class="etiqueta-tipo ${n.tipoClase}"><c:out value="${n.tipoEtiqueta}"/></span>
                        <c:if test="${not n.leido}"><span class="etiqueta-tipo" style="background:#4f46e5;color:#fff;">Nueva</span></c:if>
                        <h2><c:out value="${n.titulo}"/></h2>
                        <p><c:out value="${n.contenido}"/></p>
                        <div class="pie-notificacion">
                            <span><c:out value="${n.fecha}"/> (hora de Lima)</span>
                            <c:if test="${not n.leido}">
                                <form method="post" action="<c:url value='/notificaciones/leer'/>">
                                    <input type="hidden" name="csrf" value="<c:out value='${csrf}'/>">
                                    <input type="hidden" name="id" value="<c:out value='${n.id}'/>">
                                    <c:if test="${soloNoLeidas}"><input type="hidden" name="filtro" value="noleidas"></c:if>
                                    <button type="submit" class="boton-secundario">Marcar como leída</button>
                                </form>
                            </c:if>
                        </div>
                    </li>
                </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>

    <%@ include file="/WEB-INF/views/common/nota_movil.jspf" %>
</main>
</body>
</html>
