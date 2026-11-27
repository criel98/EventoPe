package pe.edu.utp.eventope.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.eventope.dto.CarritoCompra;
import pe.edu.utp.eventope.dto.NominacionAsistente;
import pe.edu.utp.eventope.util.Csrf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/compras/revision", "/compras/nominacion"})
public final class RevisionCompraServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object o = req.getSession().getAttribute("carritoCompra");
        if (!(o instanceof CarritoCompra c)) {
            resp.sendRedirect(req.getContextPath() + "/carrito");
            return;
        }
        c.asegurarClave();
        req.setAttribute("carrito", c);
        req.setAttribute("csrf", Csrf.token(req.getSession()));
        req.getRequestDispatcher("/WEB-INF/views/compras/revision.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (!Csrf.valido(req.getSession(false), req.getParameter("csrf"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Token de seguridad inválido");
            return;
        }

        Object o = req.getSession().getAttribute("carritoCompra");
        if (!(o instanceof CarritoCompra c)) {
            resp.sendRedirect(req.getContextPath() + "/carrito");
            return;
        }

        try {
            List<NominacionAsistente> lista = new ArrayList<>();
            for (int i = 0; i < c.getCantidadTotal(); i++) {
                lista.add(new NominacionAsistente(
                        req.getParameter("dni_" + i),
                        req.getParameter("nombres_" + i),
                        req.getParameter("apellidos_" + i)));
            }
            c.setNominaciones(lista);
            c.asegurarClave();
            resp.sendRedirect(req.getContextPath() + "/compras/revision");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("carrito", c);
            req.setAttribute("csrf", Csrf.token(req.getSession()));
            req.getRequestDispatcher("/WEB-INF/views/compras/revision.jsp").forward(req, resp);
        }
    }
}
