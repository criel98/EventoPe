<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Incidencia #<c:out value="${incidencia.id}"/> | EventoPe</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
    <link rel="stylesheet" href="<c:url value='/assets/css/incidencias.css'/>">
</head>
<body>
<%@ include file="/WEB-INF/views/common/cabecera.jspf" %>
<main>
    <p><a href="<c:url value='/admin/incidencias'/>">&larr; Volver a incidencias</a></p>
    <h1>Incidencia #<c:out value="${incidencia.id}"/></h1>

    <c:if test="${not empty flashExito}"><p class="exito" role="status"><c:out value="${flashExito}"/></p></c:if>
    <c:if test="${not empty flashError}"><p class="error" role="alert"><c:out value="${flashError}"/></p></c:if>

    <section class="tarjeta">
        <div class="inc-cabecera">
            <span class="chip sev-${incidencia.severidad}"><c:out value="${incidencia.severidad}"/></span>
            <span class="chip estado-${incidencia.estado}"><c:out value="${incidencia.estado}"/></span>
        </div>
        <h2><c:out value="${incidencia.tipoTexto}"/></h2>
        <dl class="datos">
            <dt>Descripción</dt><dd><c:out value="${incidencia.descripcion}"/></dd>
            <dt>Evento</dt><dd><c:out value="${incidencia.evento}"/></dd>
            <dt>Puerta / guardia</dt><dd><c:out value="${incidencia.puerta}"/> / <c:out value="${incidencia.guardia}"/></dd>
            <dt>Fecha del intento</dt><dd><c:out value="${incidencia.fechaIntento}"/> (hora de Lima)</dd>
            <dt>Resultado del control</dt><dd>Control #<c:out value="${incidencia.controlId}"/>: <c:out value="${incidencia.estadoControl}"/></dd>
            <c:if test="${not empty incidencia.entradaId}">
                <dt>Entrada</dt><dd>#<c:out value="${incidencia.entradaId}"/> · estado actual: <c:out value="${incidencia.entradaEstado}"/></dd>
                <dt>Asistente nominado</dt><dd><c:out value="${incidencia.asistente}"/> · DNI <code><c:out value="${incidencia.dniEnmascarado}"/></code></dd>
            </c:if>
            <c:if test="${empty incidencia.entradaId}">
                <dt>Entrada</dt><dd>Sin entrada asociada (el código no corresponde a ninguna entrada).</dd>
            </c:if>
        </dl>
    </section>

    <c:choose>
        <c:when test="${not incidencia.abierta}">
            <section class="tarjeta registro-cierre">
                <h2>Resolución registrada</h2>
                <dl class="datos">
                    <dt>Quién</dt><dd><c:out value="${incidencia.resueltaPor}"/></dd>
                    <dt>Cuándo</dt><dd><c:out value="${incidencia.fechaResolucion}"/> (hora de Lima)</dd>
                    <dt>Por qué</dt><dd><c:out value="${incidencia.motivoResolucion}"/></dd>
                </dl>
            </section>
        </c:when>
        <c:otherwise>
            <section class="tarjeta">
                <h2>Resolver incidencia</h2>
                <p class="nota-movil">Cierra la incidencia sin autorizar ningún ingreso. El motivo es obligatorio y queda guardado con tu nombre y la fecha.</p>
                <form method="post" action="<c:url value='/admin/incidencias/resolver'/>" class="formulario">
                    <input type="hidden" name="csrf" value="<c:out value='${csrf}'/>">
                    <input type="hidden" name="id" value="<c:out value='${incidencia.id}'/>">
                    <label for="decision">Decisión</label>
                    <select id="decision" name="decision" required>
                        <option value="RESUELTA">Resuelta (atendida)</option>
                        <option value="DESCARTADA">Descartada (falsa alarma)</option>
                    </select>
                    <label for="motivo">Motivo (10 a 200 caracteres)</label>
                    <textarea id="motivo" name="motivo" rows="3" minlength="10" maxlength="200" required></textarea>
                    <button type="submit">Registrar resolución</button>
                </form>
            </section>

            <c:if test="${esAdmin}">
                <section class="tarjeta ingreso-manual">
                    <h2>Ingreso manual (solo administrador)</h2>
                    <c:choose>
                        <c:when test="${incidencia.ingresoManualPosible}">
                            <p class="aviso-manual"><strong>Atención:</strong> la entrada quedará marcada como utilizada y no podrá volver a ingresar nadie con ella. Comprueba la identidad del asistente en persona antes de autorizar.
                            <c:if test="${incidencia.tipo == 'IDENTIDAD_NO_COINCIDE'}"> El DNI presentado <strong>no coincidía</strong> con el nominado: autoriza solo si verificaste el documento físico.</c:if></p>
                            <form method="post" action="<c:url value='/admin/incidencias/ingreso-manual'/>" class="formulario" onsubmit="return confirm('¿Autorizar el ingreso manual? La entrada quedará utilizada.');">
                                <input type="hidden" name="csrf" value="<c:out value='${csrf}'/>">
                                <input type="hidden" name="id" value="<c:out value='${incidencia.id}'/>">
                                <label for="justificacion">Justificación (15 a 170 caracteres)</label>
                                <textarea id="justificacion" name="justificacion" rows="3" minlength="15" maxlength="170" required></textarea>
                                <button type="submit" class="peligro">Autorizar ingreso manual</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <p class="error" role="note"><strong>No disponible:</strong> <c:out value="${incidencia.razonSinIngresoManual}"/></p>
                        </c:otherwise>
                    </c:choose>
                </section>
            </c:if>
            <c:if test="${not esAdmin}">
                <section class="tarjeta"><p class="nota-movil">El ingreso manual solo puede autorizarlo un administrador.</p></section>
            </c:if>
        </c:otherwise>
    </c:choose>
    <%@ include file="/WEB-INF/views/common/nota_movil.jspf" %>
</main>
</body>
</html>
