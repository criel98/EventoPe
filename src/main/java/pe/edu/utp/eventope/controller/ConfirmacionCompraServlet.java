package pe.edu.utp.eventope.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.eventope.config.ConexionBD;
import pe.edu.utp.eventope.config.Configuracion;
import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.dto.CompraConfirmada;
import pe.edu.utp.eventope.dto.SesionUsuario;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/compras/confirmacion")
public final class ConfirmacionCompraServlet extends HttpServlet {
    private ProveedorConexion db;

    @Override
    public void init() throws ServletException {
        try {
            db = new ConexionBD(Configuracion.cargar());
        } catch (RuntimeException e) {
            throw new ServletException("No se pudo configurar la confirmación", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object a = req.getSession().getAttribute("sesionUsuario");
        if (!(a instanceof SesionUsuario actor) || actor.getTipo() != SesionUsuario.Tipo.CLIENTE) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(req.getParameter("id"));
            if (id <= 0) throw new NumberFormatException();
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String sql = "SELECT t.id, t.codigo_unico, t.total, e.nombre, cp.serie_correlativo, "
                + "(SELECT COUNT(*) FROM detalle_transaccion d WHERE d.transaccion_id = t.id) AS cantidad "
                + "FROM transaccion_compra t "
                + "JOIN evento_cultural e ON e.id = t.evento_id "
                + "JOIN comprobante_pago cp ON cp.transaccion_id = t.id "
                + "WHERE t.id = ? AND t.cliente_id = ? AND t.estado = 'CONFIRMADA'";

        try (Connection c = db.abrir();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            p.setInt(2, actor.getId());
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                CompraConfirmada compra = new CompraConfirmada(
                        r.getInt("id"),
                        r.getString("codigo_unico"),
                        r.getString("serie_correlativo"),
                        r.getString("nombre"),
                        r.getBigDecimal("total"),
                        r.getInt("cantidad"));
                req.setAttribute("compra", compra);
                req.getRequestDispatcher("/WEB-INF/views/compras/confirmacion.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException("No se pudo consultar el comprobante", e);
        }
    }
}
