package pe.edu.utp.eventope.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.edu.utp.eventope.config.ConexionBD;
import pe.edu.utp.eventope.config.Configuracion;
import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.service.ServicioIncidencias;
import pe.edu.utp.eventope.service.ServicioIngresoManual;
import pe.edu.utp.eventope.util.Csrf;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Clock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Panel de incidencias de acceso (ADMIN y SOPORTE) e ingreso manual (solo ADMIN).
 * GET  /admin/incidencias                    lista con filtros (estado, severidad, pagina)
 * GET  /admin/incidencias/detalle?id=        detalle, resolución e ingreso manual
 * POST /admin/incidencias/resolver           cierra la incidencia: id, decision, motivo
 * POST /admin/incidencias/ingreso-manual     autoriza un ingreso: id, justificacion (solo ADMIN)
 * La identidad y el rol salen siempre de la sesión; el servicio los vuelve a comprobar en la base de datos.
 */
@WebServlet("/admin/incidencias/*")
public final class IncidenciasServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(IncidenciasServlet.class.getName());
    private ServicioIncidencias incidencias;
    private ServicioIngresoManual ingresoManual;

    @Override
    public void init() throws ServletException {
        try {
            ProveedorConexion db = new ConexionBD(Configuracion.cargar());
            incidencias = new ServicioIncidencias(db, Clock.systemUTC());
            ingresoManual = new ServicioIngresoManual(db, Clock.systemUTC());
        } catch (RuntimeException e) {
            throw new ServletException("Error al configurar IncidenciasServlet", e);
        }
    }

    /** @return el actor, o null si ya se respondió (sin sesión → login; rol incorrecto → 403). */
    private SesionUsuario actor(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        Object o = s == null ? null : s.getAttribute("sesionUsuario");
        if (!(o instanceof SesionUsuario actor)) {
            if ("GET".equals(req.getMethod())) resp.sendRedirect(req.getContextPath() + "/personal/login");
            else resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        boolean autorizado = actor.getTipo() == SesionUsuario.Tipo.PERSONAL
                && (actor.getRol() == SesionUsuario.Rol.ADMIN || actor.getRol() == SesionUsuario.Rol.SOPORTE);
        if (!autorizado) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        return actor;
    }

    private static int entero(String valor, int porDefecto) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Cache-Control", "no-store");
        SesionUsuario actor = actor(req, resp);
        if (actor == null) return;
        String ruta = req.getPathInfo() == null ? "" : req.getPathInfo();
        try {
            req.setAttribute("csrf", Csrf.token(req.getSession()));
            moverMensajes(req);
            if (ruta.isEmpty() || ruta.equals("/")) {
                String estado = req.getParameter("estado") == null ? "ABIERTA" : req.getParameter("estado");
                String severidad = req.getParameter("severidad");
                var pagina = incidencias.listar(actor, estado, severidad, entero(req.getParameter("pagina"), 1));
                req.setAttribute("pagina", pagina);
                req.setAttribute("filtroEstado", estado);
                req.setAttribute("filtroSeveridad", severidad == null ? "" : severidad);
                req.getRequestDispatcher("/WEB-INF/views/admin/incidencias.jsp").forward(req, resp);
            } else if (ruta.equals("/detalle")) {
                int id = entero(req.getParameter("id"), -1);
                var detalle = id > 0 ? incidencias.detalle(actor, id) : java.util.Optional.<pe.edu.utp.eventope.dto.IncidenciaDTO>empty();
                if (detalle.isEmpty()) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                req.setAttribute("incidencia", detalle.get());
                req.setAttribute("esAdmin", actor.getRol() == SesionUsuario.Rol.ADMIN);
                req.getRequestDispatcher("/WEB-INF/views/admin/incidencia-detalle.jsp").forward(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SecurityException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "No se pudo consultar incidencias", e);
            resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Cache-Control", "no-store");
        SesionUsuario actor = actor(req, resp);
        if (actor == null) return;
        String ruta = req.getPathInfo() == null ? "" : req.getPathInfo();
        int id = entero(req.getParameter("id"), -1);
        if (id <= 0 || !(ruta.equals("/resolver") || ruta.equals("/ingreso-manual"))) {
            resp.sendError(id <= 0 ? HttpServletResponse.SC_BAD_REQUEST : HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        HttpSession sesion = req.getSession();
        try {
            if (ruta.equals("/resolver")) {
                incidencias.resolver(actor, id, req.getParameter("decision"), req.getParameter("motivo"));
                sesion.setAttribute("flashExito", "RESUELTA".equals(req.getParameter("decision"))
                        ? "Incidencia #" + id + " resuelta. Quedó registrado quién, cuándo y por qué."
                        : "Incidencia #" + id + " descartada. Quedó registrado quién, cuándo y por qué.");
            } else {
                var r = ingresoManual.autorizar(actor, id, req.getParameter("justificacion"));
                sesion.setAttribute("flashExito", "Ingreso manual autorizado para la incidencia #" + id
                        + " (control #" + r.getControlId() + "). La entrada quedó utilizada y la incidencia, resuelta.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            sesion.setAttribute("flashError", e.getMessage());
        } catch (SecurityException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "No se pudo completar la acción sobre la incidencia " + id, e);
            sesion.setAttribute("flashError", "No se pudo completar la acción. No se guardó ningún cambio; inténtalo de nuevo.");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/incidencias/detalle?id=" + id);
    }

    /** Mensajes de un solo uso entre el POST y la página siguiente (patrón POST/redirect/GET). */
    private static void moverMensajes(HttpServletRequest req) {
        HttpSession s = req.getSession();
        for (String clave : new String[] {"flashExito", "flashError"}) {
            Object v = s.getAttribute(clave);
            if (v != null) {
                req.setAttribute(clave, v);
                s.removeAttribute(clave);
            }
        }
    }
}
