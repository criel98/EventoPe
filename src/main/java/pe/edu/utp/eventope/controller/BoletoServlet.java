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
import pe.edu.utp.eventope.dto.BoletoDetalleDTO;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.util.Csrf;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/entradas/boleto")
public final class BoletoServlet extends HttpServlet {
    private ProveedorConexion db;
    private final MisEntradasDAO dao = new MisEntradasDAO();

    @Override
    public void init() throws ServletException {
        try {
            db = new ConexionBD(Configuracion.cargar());
        } catch (RuntimeException e) {
            throw new ServletException("Error al configurar BoletoServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object o = req.getSession().getAttribute("sesionUsuario");
        if (!(o instanceof SesionUsuario actor) || actor.getTipo() != SesionUsuario.Tipo.CLIENTE) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String paramId = req.getParameter("id");
        int entradaId;
        try {
            entradaId = Integer.parseInt(paramId);
            if (entradaId <= 0) throw new NumberFormatException();
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Boleto no encontrado");
            return;
        }

        try (Connection c = db.abrir()) {
            Optional<BoletoDetalleDTO> opt = dao.detalleBoleto(c, actor.getId(), entradaId);
            if (opt.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Boleto no encontrado o no pertenece a tu cuenta");
                return;
            }
            req.setAttribute("boleto", opt.get());
            req.setAttribute("csrf", Csrf.token(req.getSession()));
            req.getRequestDispatcher("/WEB-INF/views/entradas/boleto.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Error al consultar el boleto", e);
        }
    }
}
