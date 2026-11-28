package pe.edu.utp.eventope.dao;

import pe.edu.utp.eventope.model.OrganizadorEvento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class OrganizadorDAO {
    public List<OrganizadorEvento> listar(Connection c) throws SQLException {
        List<OrganizadorEvento> result = new ArrayList<>();
        try (PreparedStatement p = c.prepareStatement("SELECT id,ruc,razon_social,email_contacto,telefono FROM organizador_evento ORDER BY razon_social");
             ResultSet r = p.executeQuery()) {
            while (r.next()) result.add(mapear(r));
        }
        return result;
    }

    public Optional<OrganizadorEvento> buscar(Connection c, int id) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("SELECT id,ruc,razon_social,email_contacto,telefono FROM organizador_evento WHERE id=?")) {
            p.setInt(1, id);
            try (ResultSet r = p.executeQuery()) { return r.next() ? Optional.of(mapear(r)) : Optional.empty(); }
        }
    }

    public void insertar(Connection c, OrganizadorEvento o) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("INSERT INTO organizador_evento(ruc,razon_social,email_contacto,telefono) VALUES(?,?,?,?)")) {
            p.setString(1, o.getRuc()); p.setString(2, o.getRazonSocial()); p.setString(3, o.getEmailContacto()); p.setString(4, o.getTelefono()); p.executeUpdate();
        }
    }

    public void actualizar(Connection c, OrganizadorEvento o) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("UPDATE organizador_evento SET ruc=?,razon_social=?,email_contacto=?,telefono=? WHERE id=?")) {
            p.setString(1, o.getRuc()); p.setString(2, o.getRazonSocial()); p.setString(3, o.getEmailContacto()); p.setString(4, o.getTelefono()); p.setInt(5, o.getId());
            if (p.executeUpdate() != 1) throw new SQLException("Organizador no encontrado.");
        }
    }

    public void eliminar(Connection c, int id) throws SQLException {
        exigirSinReferencias(c, "SELECT COUNT(*) FROM evento_cultural WHERE organizador_id=?", id, "El organizador está asociado a eventos y no se puede borrar.");
        try (PreparedStatement p = c.prepareStatement("DELETE FROM organizador_evento WHERE id=?")) {
            p.setInt(1, id); if (p.executeUpdate() != 1) throw new SQLException("Organizador no encontrado.");
        }
    }

    private static void exigirSinReferencias(Connection c, String sql, int id, String mensaje) throws SQLException {
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id); try (ResultSet r = p.executeQuery()) { r.next(); if (r.getLong(1) > 0) throw new SQLException(mensaje); }
        }
    }

    private static OrganizadorEvento mapear(ResultSet r) throws SQLException {
        return new OrganizadorEvento(r.getInt("id"), r.getString("ruc"), r.getString("razon_social"), r.getString("email_contacto"), r.getString("telefono"));
    }
}
