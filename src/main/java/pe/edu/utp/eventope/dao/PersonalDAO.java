package pe.edu.utp.eventope.dao;

import pe.edu.utp.eventope.model.AdministradorPlataforma;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class PersonalDAO {

    private static final String SQL_BUSCAR_USERNAME =
            "SELECT id, username, contrasena_hash, nivel_acceso, area_soporte, estado " +
            "FROM administrador_plataforma WHERE username = ?";

    private static final String SQL_BUSCAR_ID =
            "SELECT id, username, contrasena_hash, nivel_acceso, area_soporte, estado " +
            "FROM administrador_plataforma WHERE id = ?";

    private static final String SQL_LISTAR =
            "SELECT id, username, contrasena_hash, nivel_acceso, area_soporte, estado " +
            "FROM administrador_plataforma ORDER BY id ASC";

    private static final String SQL_INSERTAR =
            "INSERT INTO administrador_plataforma (username, contrasena_hash, nivel_acceso, area_soporte, estado) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE administrador_plataforma " +
            "SET nivel_acceso = ?, area_soporte = ?, estado = ? " +
            "WHERE id = ?";

    private static final String SQL_ACTUALIZAR_CONTRASENA =
            "UPDATE administrador_plataforma " +
            "SET contrasena_hash = ? " +
            "WHERE id = ?";

    public Optional<AdministradorPlataforma> buscarPorUsername(Connection c, String username) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SQL_BUSCAR_USERNAME)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapear(rs));
            }
        }
    }

    public Optional<AdministradorPlataforma> buscarPorId(Connection c, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SQL_BUSCAR_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapear(rs));
            }
        }
    }

    public List<AdministradorPlataforma> listarTodos(Connection c) throws SQLException {
        List<AdministradorPlataforma> lista = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public int insertar(Connection c, AdministradorPlataforma personal) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, personal.getUsername());
            ps.setString(2, personal.getContrasenaHash());
            ps.setString(3, personal.getNivelAcceso());
            ps.setString(4, personal.getAreaSoporte());
            ps.setString(5, personal.getEstado());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se pudo insertar el registro de personal.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int idGenerado = keys.getInt(1);
                    personal.setId(idGenerado);
                    return idGenerado;
                }
            }
            return 0;
        }
    }

    public boolean actualizarConfiguracion(Connection c, int id, String nivelAcceso, String areaSoporte, String estado)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SQL_ACTUALIZAR)) {
            ps.setString(1, nivelAcceso);
            ps.setString(2, areaSoporte);
            ps.setString(3, estado);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizarContrasenaHash(Connection c, int id, String nuevoHash) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SQL_ACTUALIZAR_CONTRASENA)) {
            ps.setString(1, nuevoHash);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    private AdministradorPlataforma mapear(ResultSet rs) throws SQLException {
        return new AdministradorPlataforma(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("contrasena_hash"),
                rs.getString("nivel_acceso"),
                rs.getString("area_soporte"),
                rs.getString("estado")
        );
    }
}
