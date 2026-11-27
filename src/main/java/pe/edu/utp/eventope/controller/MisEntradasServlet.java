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
import pe.edu.utp.eventope.dto.EntradaResumenDTO;
import pe.edu.utp.eventope.dto.SesionUsuario;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/entradas")
public final class MisEntradasServlet extends HttpServlet {
    private ProveedorConexion db;
    private final MisEntradasDAO dao = new MisEntradasDAO();

    @Override
    public void init() throws ServletException {
        try {
            db = new ConexionBD(Configuracion.cargar());
        } catch (RuntimeException e) {
            throw new ServletException("Error al configurar MisEntradasServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object o = req.getSession().getAttribute("sesionUsuario");
        if (!(o instanceof SesionUsuario actor) || actor.getTipo() != SesionUsuario.Tipo.CLIENTE) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try (Connection c = db.abrir()) {
            List<EntradaResumenDTO> entradas = dao.listarPorComprador(c, actor.getId());
            req.setAttribute("entradas", entradas);
            req.getRequestDispatcher("/WEB-INF/views/entradas/lista.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("No se pudieron consultar las entradas", e);
        }
    }
}
