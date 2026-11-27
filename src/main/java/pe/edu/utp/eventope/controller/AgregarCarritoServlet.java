package pe.edu.utp.eventope.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.edu.utp.eventope.config.Configuracion;
import pe.edu.utp.eventope.config.ConexionBD;
import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.dto.CarritoCompra;
import pe.edu.utp.eventope.dto.LineaCarrito;
import pe.edu.utp.eventope.util.Csrf;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@WebServlet(urlPatterns = {"/carrito/agregar", "/compras/carrito/agregar"})
public final class AgregarCarritoServlet extends HttpServlet {
    private ProveedorConexion db;

    @Override
    public void init() throws ServletException {
        try {
            db = new ConexionBD(Configuracion.cargar());
        } catch (RuntimeException e) {
            throw new ServletException("Configura la conexión de EventoPe", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (!Csrf.valido(req.getSession(false), req.getParameter("csrf"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Token de seguridad inválido");
            return;
        }

        try {
            int eventoId = leerEnteroPositivo(req, "eventoId");
            int categoriaId = leerEnteroPositivo(req, "categoriaId");
            int cantidad = leerEnteroPositivo(req, "cantidad");
            if (cantidad > 4) {
                throw new IllegalArgumentException("Selecciona una cantidad de 1 a 4.");
            }

            HttpSession sesion = req.getSession(false);
            if (sesion == null) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "La sesión de compra ha expirado");
                return;
            }

            synchronized (sesion) {
                Object valorCarrito = sesion.getAttribute("carritoCompra");
                if (valorCarrito != null && !(valorCarrito instanceof CarritoCompra)) {
                    throw new IllegalArgumentException("El contenido del carrito no es válido.");
                }
                CarritoCompra carritoActual = (CarritoCompra) valorCarrito;
                boolean reemplazoSolicitado = "si".equals(req.getParameter("reemplazar"));
                boolean mismoEvento = carritoActual != null && carritoActual.getEventoId() == eventoId;

                if (reemplazoSolicitado
                        && (carritoActual == null || mismoEvento
                        || !coincideConPendiente(sesion, eventoId, categoriaId, cantidad))) {
                    throw new IllegalArgumentException(
                            "La confirmación para reemplazar el carrito ya no es válida. Inténtalo nuevamente.");
                }

                int cantidadExistente = 0;
                if (mismoEvento) {
                    int cantidadTotal = carritoActual.getCantidadTotal();
                    if (cantidadTotal < 1 || cantidadTotal > 4) {
                        throw new IllegalArgumentException("El contenido del carrito no es válido.");
                    }
                    if (cantidadTotal + cantidad > 4) {
                        throw new IllegalArgumentException("Máximo cuatro entradas por compra.");
                    }
                    for (LineaCarrito linea : carritoActual.getLineas()) {
                        if (linea.getCategoriaId() == categoriaId) {
                            cantidadExistente += linea.getCantidad();
                        }
                    }
                }

                String nombreEvento;
                String nombreZona;
                BigDecimal precio;
                int aforoDisponible;
                String sql = "SELECT e.nombre, e.estado, c.nombre_zona, c.precio_base, c.aforo_disponible "
                        + "FROM evento_cultural e "
                        + "JOIN categoria_asiento c ON c.evento_id = e.id "
                        + "WHERE e.id = ? AND c.id = ?";
                try (Connection conexion = db.abrir();
                     PreparedStatement consulta = conexion.prepareStatement(sql)) {
                    consulta.setInt(1, eventoId);
                    consulta.setInt(2, categoriaId);
                    try (ResultSet resultado = consulta.executeQuery()) {
                        if (!resultado.next() || !"PUBLICADO".equals(resultado.getString("estado"))) {
                            throw new IllegalArgumentException("El evento o la zona no está disponible.");
                        }
                        nombreEvento = resultado.getString("nombre");
                        nombreZona = resultado.getString("nombre_zona");
                        precio = resultado.getBigDecimal("precio_base");
                        aforoDisponible = resultado.getInt("aforo_disponible");
                    }
                }

                if (precio == null || precio.signum() < 0) {
                    throw new SQLException("El precio persistido de la categoría no es válido.");
                }
                if (aforoDisponible < cantidadExistente + cantidad) {
                    throw new IllegalArgumentException("No hay suficientes entradas disponibles.");
                }

                if (carritoActual != null && !mismoEvento && !reemplazoSolicitado) {
                    sesion.setAttribute("eventoPendiente", new int[]{eventoId, categoriaId, cantidad});
                    sesion.setAttribute("nombreEventoPendiente", nombreEvento);
                    sesion.setAttribute(
                            "errorCompra",
                            "Tu carrito pertenece a otro evento. Confirma si deseas reemplazarlo.");
                    resp.sendRedirect(req.getContextPath() + "/carrito");
                    return;
                }

                LineaCarrito nuevaLinea = new LineaCarrito(
                        categoriaId, nombreZona, cantidad, precio.toPlainString());
                CarritoCompra carrito;
                if (mismoEvento) {
                    carrito = carritoActual;
                    carrito.agregar(nuevaLinea);
                } else {
                    carrito = new CarritoCompra(
                            eventoId, nombreEvento, List.of(nuevaLinea));
                }

                sesion.setAttribute("carritoCompra", carrito);
                sesion.removeAttribute("eventoPendiente");
                sesion.removeAttribute("nombreEventoPendiente");
                sesion.removeAttribute("errorCompra");
            }

            resp.sendRedirect(req.getContextPath() + "/carrito");
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute(
                    "errorCompra",
                    e.getMessage() == null ? "Datos de compra inválidos." : e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/eventos");
        } catch (SQLException e) {
            throw new ServletException("No se pudo validar la zona del evento", e);
        }
    }

    private static int leerEnteroPositivo(HttpServletRequest req, String parametro) {
        String valor = req.getParameter(parametro);
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Falta el campo " + parametro + ".");
        }
        try {
            int numero = Integer.parseInt(valor);
            if (numero <= 0) {
                throw new IllegalArgumentException("El campo " + parametro + " no es válido.");
            }
            return numero;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo " + parametro + " no es válido.", e);
        }
    }

    private static boolean coincideConPendiente(
            HttpSession sesion, int eventoId, int categoriaId, int cantidad) {
        Object valorPendiente = sesion.getAttribute("eventoPendiente");
        if (!(valorPendiente instanceof int[] pendiente) || pendiente.length != 3) {
            return false;
        }
        return pendiente[0] == eventoId
                && pendiente[1] == categoriaId
                && pendiente[2] == cantidad;
    }
}
