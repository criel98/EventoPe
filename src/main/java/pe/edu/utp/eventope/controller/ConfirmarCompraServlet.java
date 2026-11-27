package pe.edu.utp.eventope.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.eventope.config.ConexionBD;
import pe.edu.utp.eventope.config.Configuracion;
import pe.edu.utp.eventope.dto.CarritoCompra;
import pe.edu.utp.eventope.dto.CompraConfirmada;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.service.ServicioCompra;
import pe.edu.utp.eventope.util.Csrf;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/compras/confirmar")
public final class ConfirmarCompraServlet extends HttpServlet {
    private ServicioCompra servicio;

    @Override
    public void init() throws ServletException {
        try {
            servicio = new ServicioCompra(new ConexionBD(Configuracion.cargar()));
        } catch (RuntimeException e) {
            throw new ServletException("No se pudo configurar la compra", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (!Csrf.valido(req.getSession(false), req.getParameter("csrf"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Token de seguridad inválido");
            return;
        }

        Object a = req.getSession().getAttribute("sesionUsuario");
        Object o = req.getSession().getAttribute("carritoCompra");
        if (!(a instanceof SesionUsuario actor) || actor.getTipo() != SesionUsuario.Tipo.CLIENTE) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!(o instanceof CarritoCompra c) || c.getNominaciones().size() != c.getCantidadTotal()) {
            req.getSession().setAttribute("errorCompra", "Completa los datos de cada asistente antes de confirmar.");
            resp.sendRedirect(req.getContextPath() + "/compras/revision");
            return;
        }

        try {
            CompraConfirmada r = servicio.confirmar(actor.getId(), c);
            req.getSession().removeAttribute("carritoCompra");
            req.getSession().setAttribute("ultimaCompraId", r.getId());
            resp.sendRedirect(req.getContextPath() + "/compras/confirmacion?id=" + r.getId());
        } catch (IllegalArgumentException | SQLException e) {
            String msg = e.getMessage() == null ? "No se pudo completar la compra." : e.getMessage();
            req.setAttribute("error", msg);
            req.setAttribute("carrito", c);
            req.setAttribute("csrf", Csrf.token(req.getSession()));
            req.getRequestDispatcher("/WEB-INF/views/compras/revision.jsp").forward(req, resp);
        }
    }
}
