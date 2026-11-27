<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Boleto Digital | EventoPe</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
    <style>
        .boleto-contenedor {
            max-width: 440px;
            margin: 10px auto;
            background: #ffffff;
            border-radius: 24px;
            box-shadow: 0 16px 36px -4px rgba(15, 23, 42, 0.12);
            border: 1px solid #e2e8f0;
            overflow: hidden;
            position: relative;
        }
        .boleto-cabecera {
            background: linear-gradient(135deg, #1e1b4b 0%, #312e81 50%, #4338ca 100%);
            color: #ffffff;
            padding: 26px 20px;
            text-align: center;
            position: relative;
        }
        .boleto-cabecera h1 {
            font-size: 1.45rem;
            margin: 4px 0 6px 0;
            color: #ffffff;
            font-weight: 800;
            letter-spacing: -0.3px;
        }
        .ticket-notch-divider {
            position: relative;
            height: 24px;
            background: #ffffff;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .ticket-notch-divider::before,
        .ticket-notch-divider::after {
            content: "";
            position: absolute;
            top: 50%;
            transform: translateY(-50%);
            width: 22px;
            height: 22px;
            background: #f8fafc;
            border-radius: 50%;
            box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.06);
        }
        .ticket-notch-divider::before {
            left: -11px;
            border-right: 1px solid #e2e8f0;
        }
        .ticket-notch-divider::after {
            right: -11px;
            border-left: 1px solid #e2e8f0;
        }
        .ticket-perforated-line {
            width: 86%;
            border-top: 2px dashed #cbd5e1;
        }
        .boleto-cuerpo {
            padding: 20px 22px 26px;
        }
        .qr-zona {
            background: #f8fafc;
            border: 2px dashed #cbd5e1;
            border-radius: 20px;
            padding: 22px;
            text-align: center;
            margin: 16px 0;
            min-height: 310px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
        }
        #qr-codigo {
            display: inline-block;
            margin: 8px auto;
            padding: 12px;
            background: #ffffff;
            border-radius: 16px;
            box-shadow: 0 4px 14px rgba(0, 0, 0, 0.06);
            border: 1px solid #e2e8f0;
        }
        #qr-codigo img, #qr-codigo canvas {
            display: block;
            margin: auto;
            max-width: 210px;
            max-height: 210px;
        }
        .temporizador {
            font-size: 0.95rem;
            font-weight: 750;
            color: #4f46e5;
            margin: 10px 0 4px;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 6px;
        }
        .barra-tiempo {
            width: 100%;
            height: 6px;
            background: #e2e8f0;
            border-radius: 9999px;
            overflow: hidden;
            margin-top: 6px;
        }
        .barra-progreso {
            height: 100%;
            background: linear-gradient(90deg, #4f46e5, #7c3aed);
            width: 100%;
            transition: width 1s linear;
        }
        .datos-asistente {
            background: #f1f5f9;
            border-radius: 14px;
            padding: 16px;
            margin: 16px 0;
            font-size: 0.94rem;
            border: 1px solid #e2e8f0;
        }
        .datos-asistente p {
            margin: 4px 0;
            color: #334155;
        }
    </style>
</head>
<body>
<%@ include file="/WEB-INF/views/common/cabecera.jspf" %>
<main>
    <div style="margin-bottom: 16px;">
        <a href="<c:url value='/entradas'/>" class="boton-secundario" style="text-decoration: none; padding: 6px 14px; font-size: 0.88rem; border-radius: var(--radius-full); display: inline-flex; align-items: center; gap: 6px;">
            &larr; Volver a mis entradas
        </a>
    </div>

    <c:choose>
        <c:when test="${empty boleto}">
            <div class="tarjeta" style="text-align: center;">
                <h2>Boleto no disponible</h2>
                <p>No se encontró la información del boleto solicitado o no tienes permiso para acceder a él.</p>
                <a href="<c:url value='/entradas'/>" class="boton">Regresar a Mis Entradas</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="boleto-contenedor">
                <!-- Cabecera del Boleto Digital -->
                <div class="boleto-cabecera">
                    <span style="font-size: 0.76rem; text-transform: uppercase; letter-spacing: 1.5px; opacity: 0.9; font-weight: 800; background: rgba(255,255,255,0.18); padding: 4px 10px; border-radius: 9999px;">
                        &#127915; Boleto Digital Oficial
                    </span>
                    <h1><c:out value="${boleto.eventoNombre}"/></h1>
                    <p style="margin: 0; font-size: 0.95rem; opacity: 0.95; font-weight: 600;">
                        Zona: <strong><c:out value="${boleto.zona}"/></strong> &bull; Pase #<c:out value="${boleto.id}"/>
                    </p>
                </div>

                <!-- Efecto troquelado / muesca de ticket real -->
                <div class="ticket-notch-divider">
                    <div class="ticket-perforated-line"></div>
                </div>

                <!-- Cuerpo del Boleto con QR Dinámico -->
                <div class="boleto-cuerpo">
                    <div style="text-align: center; color: #475569; font-size: 0.92rem; margin-bottom: 8px;">
                        <p style="margin: 0;"><strong>&#128197; Fecha:</strong> <c:out value="${boleto.eventoFecha}"/></p>
                        <p style="margin: 4px 0 0 0;"><strong>&#128205; Lugar:</strong> <c:out value="${boleto.localNombre}"/> (<c:out value="${boleto.ciudad}"/>)</p>
                        <c:if test="${not empty boleto.localDireccion}">
                            <p style="margin: 2px 0 0 0; font-size: 0.85rem; color: #64748b;"><c:out value="${boleto.localDireccion}"/></p>
                        </c:if>
                    </div>

                    <!-- Contenedor QR dinámico -->
                    <div id="qr-contenedor"
                         class="qr-zona"
                         data-entrada-id="${boleto.id}"
                         data-endpoint="<c:url value='/entradas/qr'/>"
                         data-csrf="${csrf}"
                         data-intervalo="30">

                        <div id="qr-codigo" aria-label="Código QR dinámico de acceso"></div>

                        <div class="temporizador">
                            <span id="qr-icono-reloj">&#9201;</span>
                            <span id="qr-contador">Generando código seguro...</span>
                        </div>
                        <div class="barra-tiempo">
                            <div id="qr-barra" class="barra-progreso"></div>
                        </div>

                        <p id="qr-alerta" class="error" role="alert" style="display: none; margin: 8px 0 0;"></p>
                        <button type="button" id="btn-recargar-qr" class="boton" style="display: none; padding: 6px 14px; font-size: 0.85rem; margin-top: 10px;">
                            Actualizar código
                        </button>
                    </div>

                    <!-- Datos del Asistente Nominado -->
                    <div class="datos-asistente">
                        <p><strong>Titular registrado:</strong> <c:out value="${boleto.asistenteNombreCompleto}"/></p>
                        <p><strong>DNI:</strong> <code><c:out value="${boleto.asistenteDniEnmascarado}"/></code></p>
                        <p><strong>Comprobante de compra:</strong> <code><c:out value="${boleto.comprobante}"/></code></p>
                        <p style="font-size: 0.8rem; color: #64748b; margin-top: 6px;">
                            &#9888; Deberás presentar tu documento nacional de identidad en físico junto con este pase digital al ingresar.
                        </p>
                    </div>

                    <p class="nota-movil" style="text-align: center; margin: 12px 0 0; font-size: 0.82rem;">
                        &#128274; Código protegido con algoritmo criptográfico TOTP que rota periódicamente. Las capturas de pantalla fijas expirarán.
                    </p>
                </div>
            </div>
        </c:otherwise>
    </c:choose>

    <%@ include file="/WEB-INF/views/common/nota_movil.jspf" %>
</main>

<script src="<c:url value='/assets/vendor/qrcode.min.js'/>"></script>
<script src="<c:url value='/assets/js/boleto.js'/>"></script>
</body>
</html>
