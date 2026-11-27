package pe.edu.utp.eventope.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.edu.utp.eventope.config.ConexionBD;
import pe.edu.utp.eventope.config.Configuracion;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.service.ServicioNotificaciones;
import pe.edu.utp.eventope.util.Csrf;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bandeja de notificaciones del cliente.
 * GET  /notificaciones            lista (parámetro filtro=noleidas para ver solo pendientes)
 * GET  /notificaciones/contador   JSON {"noLeidas":n} para el indicador de la cabecera
 * POST /notificaciones/leer       marca una como leída (id)
 * POST /notificaciones/leer-todas marca todas como leídas
 * El cliente sale siempre de la sesión: el navegador nunca envía de quién son las notificaciones.
 */
@WebServlet("/notificaciones/*")
public final class NotificacionesServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(NotificacionesServlet.class.getName());
    private ServicioNotificaciones servicio;

    @Override
    public void init() throws ServletException {
        try {
            servicio = new ServicioNotificaciones(new ConexionBD(Configuracion.cargar()));
        } catch (RuntimeException e) {
            throw new ServletException("Error al configurar NotificacionesServlet", e);
        }
    }

    private SesionUsuario cliente(HttpServletRequest req, HttpServletResponse resp, boolean json) throws IOException {
        HttpSession s = req.getSession(false);
        Object o = s == null ? null : s.getAttribute("sesionUsuario");
        if (!(o instanceof SesionUsuario actor) || actor.getTipo() != SesionUsuario.Tipo.CLIENTE) {
            if (json || !"GET".equals(req.getMethod())) resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            else resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        return actor;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Cache-Control", "no-store");
        String ruta = req.getPathInfo() == null ? "" : req.getPathInfo();
        boolean contador = ruta.equals("/contador");
        SesionUsuario actor = cliente(req, resp, contador);
        if (actor == null) return;
        try {
            if (contador) {
                resp.setContentType("application/json; charset=UTF-8");
                resp.getWriter().write("{\"noLeidas\":" + servicio.contarNoLeidas(actor) + "}");
            } else if (ruta.isEmpty() || ruta.equals("/")) {
                boolean soloNoLeidas = "noleidas".equals(req.getParameter("filtro"));
                req.setAttribute("notificaciones", servicio.listar(actor, soloNoLeidas));
                req.setAttribute("soloNoLeidas", soloNoLeidas);
                req.setAttribute("noLeidas", servicio.contarNoLeidas(actor));
                req.setAttribute("csrf", Csrf.token(req.getSession()));
                req.getRequestDispatcher("/WEB-INF/views/notificaciones/bandeja.jsp").forward(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "No se pudieron consultar las notificaciones", e);
            resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Cache-Control", "no-store");
        SesionUsuario actor = cliente(req, resp, false);
        if (actor == null) return;
        String ruta = req.getPathInfo() == null ? "" : req.getPathInfo();
        try {
            if (ruta.equals("/leer")) {
                int id;
                try {
                    id = Integer.parseInt(req.getParameter("id"));
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                // Una notificación ajena responde igual que una inexistente.
                if (!servicio.marcarLeida(actor, id)) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            } else if (ruta.equals("/leer-todas")) {
                servicio.marcarTodasLeidas(actor);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            String filtro = "noleidas".equals(req.getParameter("filtro")) ? "?filtro=noleidas" : "";
            resp.sendRedirect(req.getContextPath() + "/notificaciones" + filtro);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "No se pudo actualizar la notificación", e);
            resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
    }
}
