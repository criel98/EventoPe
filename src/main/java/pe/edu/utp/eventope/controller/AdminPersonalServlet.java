package pe.edu.utp.eventope.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.model.AdministradorPlataforma;
import pe.edu.utp.eventope.service.ServicioPersonal;
import pe.edu.utp.eventope.util.Csrf;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebServlet("/admin/personal")
public final class AdminPersonalServlet extends HttpServlet {

    private final ServicioPersonal servicioPersonal = new ServicioPersonal();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SesionUsuario actor = obtenerActorAdmin(req, resp);
        if (actor == null) {
            return;
        }

        HttpSession session = req.getSession(true);
        Object mensajeExito = session.getAttribute("flashExitoPersonal");
        Object mensajeError = session.getAttribute("flashErrorPersonal");
        session.removeAttribute("flashExitoPersonal");
        session.removeAttribute("flashErrorPersonal");

        if (mensajeExito != null) {
            req.setAttribute("mensajeExito", mensajeExito.toString());
        }
        if (mensajeError != null) {
            req.setAttribute("mensajeError", mensajeError.toString());
        }

        try {
            List<AdministradorPlataforma> listaPersonal = servicioPersonal.listarPersonal(actor);
            req.setAttribute("listaPersonal", listaPersonal);
            req.setAttribute("csrf", Csrf.token(session));
            resp.setHeader("Cache-Control", "no-store");
            req.getRequestDispatcher("/WEB-INF/views/admin/personal.jsp").forward(req, resp);
        } catch (SecurityException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (IllegalStateException e) {
            resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SesionUsuario actor = obtenerActorAdmin(req, resp);
        if (actor == null) {
            return;
        }

        HttpSession session = req.getSession(true);
        String accion = req.getParameter("accion");

        try {
            if ("crear".equals(accion)) {
                String username = req.getParameter("username");
                String nivelAcceso = req.getParameter("nivelAcceso");
                String areaSoporte = req.getParameter("areaSoporte");
                String estado = req.getParameter("estado");
                String rawPassword = req.getParameter("password");
                char[] clave = rawPassword == null ? new char[0] : rawPassword.toCharArray();
                try {
                    servicioPersonal.crearPersonal(actor, username, clave, nivelAcceso, areaSoporte, estado);
                    session.setAttribute("flashExitoPersonal", "Miembro de personal registrado correctamente.");
                } finally {
                    Arrays.fill(clave, '\0');
                }
            } else if ("actualizar".equals(accion)) {
                int idPersonal = parsearId(req.getParameter("id"));
                String nivelAcceso = req.getParameter("nivelAcceso");
                String areaSoporte = req.getParameter("areaSoporte");
                String estado = req.getParameter("estado");
                servicioPersonal.actualizarPersonal(actor, idPersonal, nivelAcceso, areaSoporte, estado);
                session.setAttribute("flashExitoPersonal", "Datos del personal actualizados correctamente.");
            } else if ("clave".equals(accion)) {
                int idPersonal = parsearId(req.getParameter("id"));
                String rawPassword = req.getParameter("nuevaPassword");
                char[] clave = rawPassword == null ? new char[0] : rawPassword.toCharArray();
                try {
                    servicioPersonal.cambiarContrasena(actor, idPersonal, clave);
                    session.setAttribute("flashExitoPersonal", "Contraseña actualizada correctamente.");
                } finally {
                    Arrays.fill(clave, '\0');
                }
            } else {
                session.setAttribute("flashErrorPersonal", "Acción no reconocida.");
            }
        } catch (IllegalArgumentException e) {
            session.setAttribute("flashErrorPersonal", e.getMessage());
        } catch (SecurityException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        } catch (IllegalStateException e) {
            session.setAttribute("flashErrorPersonal", e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/personal");
    }

    private SesionUsuario obtenerActorAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        Object val = session == null ? null : session.getAttribute("sesionUsuario");
        if (!(val instanceof SesionUsuario actor)) {
            if ("GET".equals(req.getMethod())) {
                resp.sendRedirect(req.getContextPath() + "/personal/login");
            } else {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Inicia sesión para continuar");
            }
            return null;
        }
        if (actor.getTipo() != SesionUsuario.Tipo.PERSONAL || actor.getRol() != SesionUsuario.Rol.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo un administrador puede acceder a esta sección.");
            return null;
        }
        return actor;
    }

    private int parsearId(String rawId) {
        if (rawId == null || rawId.isBlank()) {
            throw new IllegalArgumentException("Identificador de personal obligatorio.");
        }
        try {
            return Integer.parseInt(rawId.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Identificador de personal inválido.");
        }
    }
}
