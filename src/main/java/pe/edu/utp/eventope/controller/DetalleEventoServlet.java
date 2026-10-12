package pe.edu.utp.eventope.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.eventope.config.ConexionBD;
import pe.edu.utp.eventope.config.Configuracion;
import pe.edu.utp.eventope.dto.EventoDetalleDTO;
import pe.edu.utp.eventope.service.ServicioCatalogo;
import pe.edu.utp.eventope.util.Csrf;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/eventos/detalle")
public final class DetalleEventoServlet extends HttpServlet {
    private ServicioCatalogo servicio;

    @Override
    public void init() throws ServletException {
        try {
            servicio = new ServicioCatalogo(new ConexionBD(Configuracion.cargar()));
        } catch (RuntimeException e) {
            throw new ServletException("Falta configuración del detalle del evento", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String valor = req.getParameter("id");
        if (valor == null || valor.isBlank()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Evento no encontrado");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Evento no encontrado");
            return;
        }

        try {
            EventoDetalleDTO evento = servicio.detalle(id);
            req.setAttribute("evento", evento);
            req.setAttribute("csrf", Csrf.token(req.getSession()));
            req.getRequestDispatcher("/WEB-INF/views/eventos/detalle.jsp").forward(req, resp);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Evento no encontrado");
        } catch (SQLException e) {
            throw new ServletException("No se pudo consultar el detalle del evento", e);
        }
    }
}
