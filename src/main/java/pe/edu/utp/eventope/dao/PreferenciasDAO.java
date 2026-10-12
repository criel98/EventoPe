package pe.edu.utp.eventope.dao;

import pe.edu.utp.eventope.model.PreferenciasMusicales;

import java.sql.*;
import java.util.Optional;

public class PreferenciasDAO {

    public void insertar(Connection conn, PreferenciasMusicales preferencias) throws SQLException {
        String sql = "INSERT INTO preferencias_musicales (cliente_id, genero_favorito, artista_favorito) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, preferencias.getClienteId());
            stmt.setString(2, preferencias.getGeneroFavorito());
            stmt.setString(3, preferencias.getArtistaFavorito());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    preferencias.setId(rs.getInt(1));
                }
            }
        }
    }

    public Optional<PreferenciasMusicales> buscarPorClienteId(Connection conn, int clienteId) throws SQLException {
        String sql = "SELECT id, cliente_id, genero_favorito, artista_favorito FROM preferencias_musicales WHERE cliente_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    PreferenciasMusicales prefs = new PreferenciasMusicales();
                    prefs.setId(rs.getInt("id"));
                    prefs.setClienteId(rs.getInt("cliente_id"));
                    prefs.setGeneroFavorito(rs.getString("genero_favorito"));
                    prefs.setArtistaFavorito(rs.getString("artista_favorito"));
                    return Optional.of(prefs);
                }
            }
        }
        return Optional.empty();
    }
} 
