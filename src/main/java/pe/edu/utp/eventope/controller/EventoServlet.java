package pe.edu.utp.eventope.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.eventope.config.ConexionBD;
import pe.edu.utp.eventope.config.Configuracion;
import pe.edu.utp.eventope.service.ServicioCatalogo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/eventos")
public final class EventoServlet extends HttpServlet {
    private ServicioCatalogo servicio;

    @Override
    public void init() throws ServletException {
        try {
            servicio = new ServicioCatalogo(new ConexionBD(Configuracion.cargar()));
        } catch (RuntimeException e) {
            throw new ServletException("Falta configuración del catálogo", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nombre = req.getParameter("nombre");
        String artista = req.getParameter("artista");
        String tipo = req.getParameter("tipo");

        try {
            List<?> eventos = servicio.buscar(nombre, artista, tipo);
            req.setAttribute("eventos", eventos);
            req.setAttribute("nombre", nombre);
            req.setAttribute("artista", artista);
            req.setAttribute("tipo", tipo);
            if (eventos.isEmpty()) {
                req.setAttribute("mensaje", "No hay eventos con esos filtros en este momento.");
            }
        } catch (SQLException e) {
            throw new ServletException("No se pudo consultar el catálogo", e);
        }

        req.getRequestDispatcher("/WEB-INF/views/eventos/lista.jsp").forward(req, resp);
    }
}
