package pe.edu.utp.eventope.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.eventope.dto.CarritoCompra;
import pe.edu.utp.eventope.util.Csrf;

import java.io.IOException;

@WebServlet(urlPatterns = {"/carrito", "/compras/carrito"})
public final class CarritoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object o = req.getSession().getAttribute("carritoCompra");
        req.setAttribute("carrito", o instanceof CarritoCompra ? o : null);
        req.setAttribute("csrf", Csrf.token(req.getSession()));
        req.getRequestDispatcher("/WEB-INF/views/compras/carrito.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (!Csrf.valido(req.getSession(false), req.getParameter("csrf"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Token de seguridad inválido");
            return;
        }

        if ("vaciar".equals(req.getParameter("accion"))) {
            req.getSession().removeAttribute("carritoCompra");
            req.getSession().removeAttribute("eventoPendiente");
            req.getSession().removeAttribute("nombreEventoPendiente");
            resp.sendRedirect(req.getContextPath() + "/carrito");
            return;
        }

        try {
            Object o = req.getSession().getAttribute("carritoCompra");
            if (!(o instanceof CarritoCompra)) {
                throw new IllegalArgumentException("El carrito está vacío.");
            }
            resp.sendRedirect(req.getContextPath() + "/compras/revision");
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("errorCompra", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/carrito");
        }
    }
}
