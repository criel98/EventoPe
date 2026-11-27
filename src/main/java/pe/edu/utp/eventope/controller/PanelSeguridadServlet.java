package pe.edu.utp.eventope.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.eventope.config.ConexionBD;
import pe.edu.utp.eventope.config.Configuracion;
import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.dao.MisEntradasDAO;
import pe.edu.utp.eventope.dto.DispositivoOperadorDTO;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.util.Csrf;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@WebServlet("/seguridad/panel")
public final class PanelSeguridadServlet extends HttpServlet {
    private ProveedorConexion db;
    private final MisEntradasDAO dao = new MisEntradasDAO();

    @Override
    public void init() throws ServletException {
        try {
            db = new ConexionBD(Configuracion.cargar());
        } catch (RuntimeException e) {
            throw new ServletException("Error al configurar PanelSeguridadServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object o = req.getSession().getAttribute("sesionUsuario");
        if (!(o instanceof SesionUsuario actor) || actor.getTipo() != SesionUsuario.Tipo.PERSONAL || actor.getRol() != SesionUsuario.Rol.VALIDACION) {
            resp.sendRedirect(req.getContextPath() + "/personal/login");
            return;
        }

        try (Connection c = db.abrir()) {
            Optional<DispositivoOperadorDTO> dispositivoOpt = dao.dispositivoActivoPorOperador(c, actor.getId());
            if (dispositivoOpt.isEmpty()) {
                req.setAttribute("error", "No tienes ningún dispositivo o puerta asignada para validación.");
            } else {
                req.setAttribute("dispositivo", dispositivoOpt.get());
            }
            req.setAttribute("solicitudId", UUID.randomUUID().toString());
            req.setAttribute("csrf", Csrf.token(req.getSession()));
            req.getRequestDispatcher("/WEB-INF/views/seguridad/panel.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Error al consultar la asignación de seguridad", e);
        }
    }
}
