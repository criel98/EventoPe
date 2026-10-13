package pe.edu.utp.eventope.service;

import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.dao.CompraDAO;
import pe.edu.utp.eventope.dto.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HexFormat;

public final class ServicioCompra {
    private final ProveedorConexion conexiones;
    private final CompraDAO dao = new CompraDAO();

    public ServicioCompra(ProveedorConexion conexiones) {
        this.conexiones = conexiones;
    }

    public CompraConfirmada confirmar(int clienteId, CarritoCompra carrito) throws SQLException {
        if (clienteId <= 0 || carrito == null) {
            throw new IllegalArgumentException("Sesión o carrito inválido.");
        }
        if (carrito.getCantidadTotal() < 1 || carrito.getCantidadTotal() > 4) {
            throw new IllegalArgumentException("La compra debe tener de una a cuatro entradas.");
        }
        if (carrito.getNominaciones().size() != carrito.getCantidadTotal()) {
            throw new IllegalArgumentException("Debes identificar a cada asistente.");
        }
        carrito.asegurarClave();
        String hash = hashCanonico(carrito);
        try (Connection c = conexiones.abrir()) {
            c.setAutoCommit(false);
            try {
                CompraConfirmada r = dao.guardar(c, clienteId, carrito, hash);
                c.commit();
                return r;
            } catch (Exception e) {
                try {
                    c.rollback();
                } catch (SQLException rollback) {
                    e.addSuppressed(rollback);
                }
                if (e instanceof SQLException se) throw se;
                if (e instanceof RuntimeException re) throw re;
                throw new SQLException("No se pudo completar la compra.", e);
            }
        }
    }

    private String hashCanonico(CarritoCompra c) {
        StringBuilder s = new StringBuilder().append(c.getEventoId()).append('|');
        for (LineaCarrito l : c.getLineas()) {
            s.append(l.getCategoriaId()).append(':').append(l.getCantidad()).append('|');
        }
        for (NominacionAsistente n : c.getNominaciones()) {
            s.append(n.getDni()).append(':').append(n.getNombres()).append(':').append(n.getApellidos()).append('|');
        }
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(s.toString().getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
