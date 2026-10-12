package pe.edu.utp.eventope.dao;

import pe.edu.utp.eventope.model.AdministradorPlataforma;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public final class PersonalDAO {

    public Optional<AdministradorPlataforma> buscarPorUsername(Connection c, String username) throws SQLException {
        String sql = "SELECT id, username, contrasena_hash, nivel_acceso, area_soporte, estado " +
                     "FROM administrador_plataforma WHERE username = ?";

        try (PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    AdministradorPlataforma admin = new AdministradorPlataforma();
                    admin.setId(rs.getInt("id"));
                    admin.setUsername(rs.getString("username"));
                    admin.setContrasenaHash(rs.getString("contrasena_hash"));
                    admin.setNivelAcceso(rs.getString("nivel_acceso"));
                    admin.setAreaSoporte(rs.getString("area_soporte"));
                    admin.setEstado(rs.getString("estado"));
                    return Optional.of(admin);
                }
            }
        }
        return Optional.empty();
    }
}
