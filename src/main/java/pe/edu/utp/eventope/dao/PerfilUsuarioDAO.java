package pe.edu.utp.eventope.dao;

import pe.edu.utp.eventope.model.PerfilUsuario;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class PerfilUsuarioDAO {

    public void insertar(Connection conn, PerfilUsuario perfil) throws SQLException {
        String sql = "INSERT INTO perfil_usuario (cliente_id, fecha_registro, estado_verificacion, pais, fecha_nacimiento) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, perfil.getClienteId());
            stmt.setTimestamp(2, Timestamp.valueOf(perfil.getFechaRegistro() != null ? perfil.getFechaRegistro() : LocalDateTime.now()));
            stmt.setBoolean(3, perfil.isEstadoVerificacion());
            stmt.setString(4, perfil.getPais());
            stmt.setDate(5, perfil.getFechaNacimiento() != null ? Date.valueOf(perfil.getFechaNacimiento()) : null);
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    perfil.setId(rs.getInt(1));
                }
            }
        }
    }

    public Optional<PerfilUsuario> buscarPorClienteId(Connection conn, int clienteId) throws SQLException {
        String sql = "SELECT id, cliente_id, fecha_registro, estado_verificacion, pais, fecha_nacimiento FROM perfil_usuario WHERE cliente_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    PerfilUsuario perfil = new PerfilUsuario();
                    perfil.setId(rs.getInt("id"));
                    perfil.setClienteId(rs.getInt("cliente_id"));
                    Timestamp ts = rs.getTimestamp("fecha_registro");
                    if (ts != null) {
                        perfil.setFechaRegistro(ts.toLocalDateTime());
                    }
                    perfil.setEstadoVerificacion(rs.getBoolean("estado_verificacion"));
                    perfil.setPais(rs.getString("pais"));
                    Date fNac = rs.getDate("fecha_nacimiento");
                    if (fNac != null) {
                        perfil.setFechaNacimiento(fNac.toLocalDate());
                    }
                    return Optional.of(perfil);
                }
            }
        }
        return Optional.empty();
    }
}
