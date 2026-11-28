package pe.edu.utp.eventope.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.service.ServicioAutenticacion;
import pe.edu.utp.eventope.util.Csrf;

import java.io.IOException;
import java.util.Arrays;

@WebServlet("/admin/ethan/login")
public final class EthanAdminLoginServlet extends HttpServlet {
    private final ServicioAutenticacion servicio = new ServicioAutenticacion();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object actor = req.getSession().getAttribute("sesionUsuario");
        if (actor instanceof SesionUsuario usuario && usuario.getRol() == SesionUsuario.Rol.ADMIN) {
            resp.sendRedirect(req.getContextPath() + "/admin/ethan");
            return;
        }
        req.setAttribute("csrf", Csrf.token(req.getSession()));
        resp.setHeader("Cache-Control", "no-store");
        req.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!Csrf.valido(req.getSession(false), req.getParameter("csrf"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Token de seguridad inválido");
            return;
        }
        String username = req.getParameter("username");
        String raw = req.getParameter("password");
        char[] password = raw == null ? new char[0] : raw.toCharArray();
        req.setAttribute("username", username);
        try {
            var actor = servicio.autenticarAdministrador(username, password);
            if (actor.isPresent()) {
                var previous = req.getSession(false);
                if (previous != null) previous.invalidate();
                var session = req.getSession(true);
                session.setAttribute("sesionUsuario", actor.get());
                Csrf.token(session);
                resp.sendRedirect(req.getContextPath() + "/admin/ethan");
                return;
            }
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            req.setAttribute("error", "Usuario o contraseña incorrectos.");
        } catch (IllegalStateException e) {
            getServletContext().log("No se pudo autenticar al administrador de Ethan", e);
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            req.setAttribute("error", "No se pudo iniciar sesión. Inténtalo nuevamente.");
        } finally {
            Arrays.fill(password, '\0');
        }
        req.setAttribute("csrf", Csrf.token(req.getSession()));
        resp.setHeader("Cache-Control", "no-store");
        req.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(req, resp);
    }
}
