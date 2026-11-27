package pe.edu.utp.eventope.dao;

import pe.edu.utp.eventope.model.UsuarioCliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public final class UsuarioClienteDAO {

    public Optional<UsuarioCliente> buscarPorEmail(Connection c, String email) throws SQLException {
        String sql = "SELECT id, nombres, apellidos, dni, email, contrasena_hash, telefono, estado " +
                     "FROM usuario_cliente WHERE email = ?";

        try (PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UsuarioCliente cliente = new UsuarioCliente();
                    cliente.setId(rs.getInt("id"));
                    cliente.setNombres(rs.getString("nombres"));
                    cliente.setApellidos(rs.getString("apellidos"));
                    cliente.setDni(rs.getString("dni"));
                    cliente.setEmail(rs.getString("email"));
                    cliente.setContrasenaHash(rs.getString("contrasena_hash")); //
                    cliente.setTelefono(rs.getString("telefono"));
                    cliente.setEstado(rs.getString("estado"));
                    return Optional.of(cliente);
                }
            }
        }
        return Optional.empty();
    }

    public void insertar(Connection c, UsuarioCliente cliente) throws SQLException {
        String sql = "INSERT INTO usuario_cliente (nombres, apellidos, dni, email, contrasena_hash, telefono, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = c.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, cliente.getNombres());
            stmt.setString(2, cliente.getApellidos());
            stmt.setString(3, cliente.getDni());
            stmt.setString(4, cliente.getEmail());
            stmt.setString(5, cliente.getContrasenaHash());
            stmt.setString(6, cliente.getTelefono());
            stmt.setString(7, cliente.getEstado());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getInt(1));
                }
            }
        }
    }
    
    public java.util.Optional<UsuarioCliente> buscarPorId(Connection c, int id) throws SQLException {
        String sql = "SELECT id, nombres, apellidos, dni, email, contrasena_hash, telefono, estado " +
                     "FROM usuario_cliente WHERE id = ?";

        try (PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UsuarioCliente cliente = new UsuarioCliente();
                    cliente.setId(rs.getInt("id"));
                    cliente.setNombres(rs.getString("nombres"));
                    cliente.setApellidos(rs.getString("apellidos"));
                    cliente.setDni(rs.getString("dni"));
                    cliente.setEmail(rs.getString("email"));
                    cliente.setContrasenaHash(rs.getString("contrasena_hash"));
                    cliente.setTelefono(rs.getString("telefono"));
                    cliente.setEstado(rs.getString("estado"));
                    return java.util.Optional.of(cliente);
                }
            }
        }
        return java.util.Optional.empty();
    }
    public void actualizar(Connection c, UsuarioCliente u) throws SQLException {
        try(var p=c.prepareStatement("UPDATE usuario_cliente SET nombres=?,apellidos=?,dni=?,email=?,telefono=? WHERE id=? AND estado='ACTIVO'")) {
            p.setString(1,u.getNombres());p.setString(2,u.getApellidos());p.setString(3,u.getDni());p.setString(4,u.getEmail());p.setString(5,u.getTelefono());p.setInt(6,u.getId());
            p.executeUpdate();
        }
    }
}
