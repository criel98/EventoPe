<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Panel de Validación de Acceso | EventoPe</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
    <style>
        .panel-seguridad {
            max-width: 680px;
            margin: 0 auto;
        }
        .info-asignacion {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 12px;
            background: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            padding: 16px;
            margin-bottom: 20px;
        }
        .info-item {
            font-size: 0.9rem;
        }
        .info-item strong {
            display: block;
            color: #64748b;
            font-size: 0.8rem;
            text-transform: uppercase;
        }
        .camara-zona {
            position: relative;
            background: #0f172a;
            border-radius: 16px;
            overflow: hidden;
            margin: 16px 0;
            text-align: center;
            min-height: 280px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        #camara-video {
            width: 100%;
            max-height: 380px;
            object-fit: cover;
            display: block;
        }
        .camara-overlay {
            position: absolute;
            inset: 0;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            pointer-events: none;
        }
        .visor-mira {
            width: 200px;
            height: 200px;
            border: 2px dashed rgba(255, 255, 255, 0.75);
            border-radius: 12px;
            box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.4);
        }
        .manual-box {
            background: #f8fafc;
            border: 1px solid #cbd5e1;
            border-radius: 12px;
            padding: 16px;
            margin-top: 16px;
        }
    </style>
</head>
<body>
<%@ include file="/WEB-INF/views/common/cabecera.jspf" %>
<main>
    <div class="panel-seguridad">
        <h1>Control de Acceso</h1>
        <p class="nota-movil">Escaneo y validación en tiempo real de boletos para el evento asignado.</p>

        <c:if test="${not empty error}">
            <div class="tarjeta" style="border-left: 6px solid #971b1b; background: #fdeeed;">
                <h3 style="color: #971b1b; margin-top: 0;">Dispositivo no asignado</h3>
                <p><c:out value="${error}"/></p>
                <p style="font-size: 0.9rem; color: #555;">Contacta al administrador para vincular este usuario con una puerta activa.</p>
            </div>
        </c:if>

        <c:if test="${not empty dispositivo}">
            <!-- Datos de asignación del punto de control -->
            <div class="info-asignacion">
                <div class="info-item">
                    <strong>Evento</strong>
                    <span><c:out value="${dispositivo.eventoNombre}"/></span>
                </div>
                <div class="info-item">
                    <strong>Punto de acceso</strong>
                    <span><c:out value="${dispositivo.nombrePuerta}"/></span>
                </div>
                <div class="info-item">
                    <strong>Dispositivo</strong>
                    <span><c:out value="${dispositivo.modeloCelular}"/> (#<c:out value="${dispositivo.dispositivoId}"/>)</span>
                </div>
                <div class="info-item">
                    <strong>Operador</strong>
                    <span>Operador #<c:out value="${dispositivo.operadorId}"/></span>
                </div>
            </div>

            <!-- Formulario oculto con datos de contexto y CSRF -->
            <form id="form-validar" action="<c:url value='/seguridad/validar'/>" method="post" style="display:none;">
                <input type="hidden" id="seguridad-csrf" name="csrf" value="<c:out value='${csrf}'/>">
                <input type="hidden" id="seguridad-dispositivo-id" name="dispositivoId" value="<c:out value='${dispositivo.dispositivoId}'/>">
                <input type="hidden" id="seguridad-solicitud-id" name="solicitudId" value="<c:out value='${solicitudId}'/>">
            </form>

            <!-- Contenedor dinámico de resultado del escaneo -->
            <div id="resultado-contenedor" style="display: none; margin-bottom: 20px;"></div>

            <!-- Área de cámara -->
            <section class="tarjeta" style="padding: 16px;">
                <h2 style="font-size: 1.15rem; margin-top: 0;">Escaneo con Cámara</h2>
                <div class="camara-zona">
                    <video id="camara-video" playsinline></video>
                    <canvas id="camara-canvas" style="display: none;"></canvas>
                    <div class="camara-overlay">
                        <div class="visor-mira"></div>
                    </div>
                </div>

                <p id="camara-estado" class="nota-movil" style="text-align: center; margin: 8px 0;">
                    Presiona el botón para encender la cámara del dispositivo.
                </p>

                <button type="button" id="btn-camara" class="boton" style="display: block; margin: 8px 0;">
                    Activar cámara
                </button>
            </section>

            <!-- Campo DNI y Modo de prueba manual -->
            <section class="tarjeta">
                <h2 style="font-size: 1.15rem; margin-top: 0;">Verificación de Identidad</h2>
                <label for="input-dni">DNI presentado físicamente (obligatorio, 8 dígitos)</label>
                <input id="input-dni" type="text" maxlength="8" pattern="[0-9]{8}" inputmode="numeric" placeholder="Ej: 12345678">

                <details class="manual-box">
                    <summary style="font-weight: 600; cursor: pointer; color: #284bd7;">
                        Modo manual / Prueba de contenido de código
                    </summary>
                    <div style="margin-top: 12px;">
                        <p class="nota-movil">Pega el contenido completo de un QR vigente para una prueba sin cámara. Se aplican los mismos controles de identidad y uso único.</p>
                        <label for="input-manual-qr">Contenido del QR</label>
                        <input id="input-manual-qr" type="text" placeholder="Pega el contenido del código aquí">
                        <button type="button" id="btn-validar-manual" class="boton" style="margin-top: 8px;">
                            Validar entrada manualmente
                        </button>
                    </div>
                </details>
            </section>
        </c:if>

        <%@ include file="/WEB-INF/views/common/nota_movil.jspf" %>
    </div>
</main>

<script src="<c:url value='/assets/vendor/jsqr.min.js'/>"></script>
<script src="<c:url value='/assets/js/seguridad.js'/>"></script>
</body>
</html>
