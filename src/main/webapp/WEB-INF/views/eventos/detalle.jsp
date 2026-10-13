<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>EventoPe | <c:out value="${evento.nombre}" /></title>
  <link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
  <style>
    .detalle-hero {
      position: relative;
      border-radius: var(--radius-lg);
      overflow: hidden;
      margin-bottom: 24px;
      box-shadow: var(--shadow-lg);
      background: #0f172a;
    }
    .detalle-hero-img {
      width: 100%;
      max-height: 420px;
      object-fit: cover;
      display: block;
      opacity: 0.85;
    }
    .detalle-hero-overlay {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      padding: 32px 24px;
      background: linear-gradient(180deg, rgba(15, 23, 42, 0) 0%, rgba(15, 23, 42, 0.95) 100%);
      color: #ffffff;
    }
    .detalle-chips {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
      margin: 14px 0 0 0;
    }
    .chip {
      background: rgba(255, 255, 255, 0.16);
      backdrop-filter: blur(8px);
      padding: 6px 14px;
      border-radius: var(--radius-full);
      font-size: 0.88rem;
      font-weight: 600;
      display: inline-flex;
      align-items: center;
      gap: 6px;
      color: #f8fafc;
      border: 1px solid rgba(255, 255, 255, 0.2);
    }
    .zona-opcion {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 16px 18px;
      margin-bottom: 12px;
      border: 2px solid var(--border-color);
      border-radius: var(--radius-md);
      cursor: pointer;
      background: #ffffff;
      transition: var(--transition);
    }
    .zona-opcion:hover {
      border-color: #a5b4fc;
      background: #faf5ff;
    }
    .zona-opcion:has(input:checked) {
      border-color: var(--primary);
      background: var(--primary-light);
      box-shadow: 0 0 0 2px var(--primary-glow);
    }
    .zona-opcion:has(input:disabled) {
      opacity: 0.55;
      cursor: not-allowed;
      background: #f1f5f9;
    }
    .zona-precio {
      font-size: 1.25rem;
      font-weight: 800;
      color: var(--primary);
    }
    @media(max-width: 600px) {
      .detalle-hero-overlay {
        padding: 20px 16px;
      }
      .detalle-hero-overlay h1 {
        font-size: 1.45rem;
      }
    }
  </style>
</head>
<body>
<%@ include file="/WEB-INF/views/common/cabecera.jspf" %>
<main>
  <div style="margin-bottom: 16px;">
    <a href="<c:url value='/eventos'/>" class="boton-secundario" style="text-decoration:none; padding: 8px 16px; font-size: 0.9rem; border-radius: var(--radius-full); display: inline-flex; align-items: center; gap: 6px;">
      &larr; Volver a la cartelera
    </a>
  </div>

  <!-- Imagen inteligente de acuerdo al ID o categoría -->
  <c:choose>
    <c:when test="${evento.id == 10}">
      <c:set var="imgDetalle" value="/assets/img/eventos/concierto_rock.jpg"/>
    </c:when>
    <c:when test="${evento.id == 11}">
      <c:set var="imgDetalle" value="/assets/img/eventos/teatro_drama.jpg"/>
    </c:when>
    <c:when test="${evento.id == 12}">
      <c:set var="imgDetalle" value="/assets/img/eventos/standup_humor.jpg"/>
    </c:when>
    <c:when test="${evento.id == 13}">
      <c:set var="imgDetalle" value="/assets/img/eventos/festival_electro.jpg"/>
    </c:when>
    <c:when test="${evento.tipoEvento == 'TEATRO'}">
      <c:set var="imgDetalle" value="/assets/img/eventos/teatro_drama.jpg"/>
    </c:when>
    <c:when test="${evento.tipoEvento == 'FESTIVAL'}">
      <c:set var="imgDetalle" value="/assets/img/eventos/festival_electro.jpg"/>
    </c:when>
    <c:otherwise>
      <c:set var="imgDetalle" value="/assets/img/eventos/concierto_rock.jpg"/>
    </c:otherwise>
  </c:choose>

  <!-- Banner Hero del Evento -->
  <div class="detalle-hero">
    <img src="<c:url value='${imgDetalle}'/>" alt="${fn:escapeXml(evento.nombre)}" class="detalle-hero-img">
    <div class="detalle-hero-overlay">
      <span class="evento-tipo-badge ${evento.tipoEvento == 'CONCIERTO' ? 'badge-concierto' : (evento.tipoEvento == 'TEATRO' ? 'badge-teatro' : 'badge-festival')}" style="position: static; margin-bottom: 8px; display: inline-block;">
        <c:out value="${evento.tipoEvento}" />
      </span>
      <h1 style="color: #ffffff; margin: 6px 0;"><c:out value="${evento.nombre}" /></h1>

      <div class="detalle-chips">
        <span class="chip">&#128197; <c:out value="${evento.fecha}" /></span>
        <span class="chip">&#128205; <c:out value="${evento.localNombre}" /> (<c:out value="${evento.ciudad}" />)</span>
        <c:if test="${not empty evento.artistaNombre}">
          <span class="chip">&#127908; <c:out value="${evento.artistaNombre}" /></span>
        </c:if>
        <span class="chip">&#128101; Aforo: <c:out value="${evento.aforo}" /></span>
      </div>
    </div>
  </div>

  <div style="display: grid; grid-template-columns: 1fr; gap: 24px;">
    <!-- Descripción y detalles -->
    <article class="tarjeta">
      <h2>Sobre este evento</h2>
      <p style="font-size: 1.05rem; line-height: 1.6; color: #334155;">
        <c:out value="${evento.descripcion}" />
      </p>
      <div style="background: #f8fafc; border-radius: 12px; padding: 14px 18px; margin-top: 18px; font-size: 0.9rem; color: #64748b; border: 1px solid #e2e8f0;">
        <p style="margin: 0;"><strong>Ingreso 100% digital:</strong> Las entradas son intransferibles una vez nominadas. Deberás presentar tu boleto con código QR dinámico desde tu teléfono móvil junto con tu DNI físico en la puerta.</p>
      </div>
    </article>

    <!-- Selección de Zonas y Compra -->
    <section class="tarjeta" style="border: 2px solid #e2e8f0;">
      <h2>Selecciona tu zona</h2>
      <p style="font-size: 0.92rem; color: #64748b; margin-bottom: 20px;">
        Máximo 4 entradas por cliente. La reserva se confirma en el proceso de pago.
      </p>

      <form method="post" action="<c:url value='/carrito/agregar'/>">
        <c:if test="${not hayCupo}">
          <p class="error" role="alert">No hay zonas con cupo disponible para este evento.</p>
        </c:if>

        <input type="hidden" name="eventoId" value="${evento.id}">
        <input type="hidden" name="csrf" value="${csrf}">

        <div style="margin-bottom: 20px;">
          <c:forEach var="categoria" items="${evento.categorias}">
            <label class="zona-opcion">
              <div style="display: flex; align-items: center; gap: 12px;">
                <input type="radio" required name="categoriaId" value="${categoria.id}" ${categoria.aforoDisponible > 0 ? '' : 'disabled'}>
                <div>
                  <div style="font-weight: 800; font-size: 1.05rem; color: #0f172a;">
                    <c:out value="${categoria.nombreZona}" />
                  </div>
                  <div style="font-size: 0.85rem; color: #64748b;">
                    <c:choose>
                      <c:when test="${categoria.aforoDisponible > 0}">
                        Cupos disponibles: <c:out value="${categoria.aforoDisponible}" />
                      </c:when>
                      <c:otherwise>
                        <span style="color: #ef4444; font-weight: 700;">Agotado</span>
                      </c:otherwise>
                    </c:choose>
                  </div>
                </div>
              </div>
              <div class="zona-precio">
                S/. <c:out value="${categoria.precioBase}" />
              </div>
            </label>
          </c:forEach>
        </div>

        <div style="display: flex; align-items: flex-end; gap: 16px; flex-wrap: wrap;">
          <div style="flex: 1; min-width: 140px;">
            <label for="cantidad">Cantidad de boletos</label>
            <select id="cantidad" name="cantidad">
              <option value="1">1 entrada</option>
              <option value="2">2 entradas</option>
              <option value="3">3 entradas</option>
              <option value="4">4 entradas</option>
            </select>
          </div>

          <div style="flex: 2; min-width: 200px;">
            <button type="submit" ${hayCupo ? '' : 'disabled'} style="width: 100%; min-height: 48px; font-size: 1.05rem;">
              Continuar con la compra &rarr;
            </button>
          </div>
        </div>
      </form>
    </section>
  </div>

  <%@ include file="/WEB-INF/views/common/nota_movil.jspf" %>
</main>
</body>
</html>
