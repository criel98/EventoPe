package pe.edu.utp.eventope.service;

import pe.edu.utp.eventope.dto.SesionUsuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Comprueba en la base de datos que quien actúa sigue siendo personal ACTIVO con el rol exigido.
 * La sesión HTTP puede quedar desactualizada (cuenta desactivada o rol cambiado después de iniciar sesión),
 * por eso no se confía solo en ella para acciones administrativas.
 */
final class AutorizacionPersonal {
    private AutorizacionPersonal() {}

    static void exigir(Connection c, SesionUsuario actor, SesionUsuario.Rol... permitidos) throws SQLException {
        if (actor == null || actor.getTipo() != SesionUsuario.Tipo.PERSONAL || !Arrays.asList(permitidos).contains(actor.getRol())) {
            throw new SecurityException("No tienes permiso para esta acción");
        }
        try (PreparedStatement p = c.prepareStatement("SELECT estado, nivel_acceso FROM administrador_plataforma WHERE id = ?")) {
            p.setInt(1, actor.getId());
            try (ResultSet r = p.executeQuery()) {
                if (!r.next() || !"ACTIVO".equals(r.getString(1)) || !actor.getRol().name().equals(r.getString(2))) {
                    throw new SecurityException("La cuenta no está habilitada para esta acción");
                }
            }
        }
    }
}
