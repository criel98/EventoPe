package pe.edu.utp.eventope.dao;

import pe.edu.utp.eventope.dto.EventoDetalleDTO;
import pe.edu.utp.eventope.dto.EventoResumenDTO;
import pe.edu.utp.eventope.model.EventoCultural;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class EventoDAO {
    public List<EventoCultural> listarAdministracion(Connection c) throws SQLException {
        List<EventoCultural> result = new ArrayList<>();
        try (PreparedStatement p = c.prepareStatement("SELECT id,nombre,descripcion,tipo_evento,fecha,aforo,local_id,organizador_id,artista_id,estado FROM evento_cultural ORDER BY fecha DESC");
             ResultSet r = p.executeQuery()) {
            while (r.next()) result.add(mapearEvento(r));
        }
        return result;
    }

    public Optional<EventoCultural> buscarAdministracion(Connection c, int id) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("SELECT id,nombre,descripcion,tipo_evento,fecha,aforo,local_id,organizador_id,artista_id,estado FROM evento_cultural WHERE id=?")) {
            p.setInt(1, id);
            try (ResultSet r = p.executeQuery()) { return r.next() ? Optional.of(mapearEvento(r)) : Optional.empty(); }
        }
    }

    public void insertar(Connection c, EventoCultural e) throws SQLException {
        String sql = "INSERT INTO evento_cultural(nombre,descripcion,tipo_evento,fecha,aforo,local_id,organizador_id,artista_id,estado) VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement p = c.prepareStatement(sql)) {
            enlazarEvento(p, e); p.executeUpdate();
        }
    }

    public void actualizar(Connection c, EventoCultural e) throws SQLException {
        String sql = "UPDATE evento_cultural SET nombre=?,descripcion=?,tipo_evento=?,fecha=?,aforo=?,local_id=?,organizador_id=?,artista_id=?,estado=? WHERE id=?";
        try (PreparedStatement p = c.prepareStatement(sql)) {
            enlazarEvento(p, e); p.setInt(10, e.getId());
            if (p.executeUpdate() != 1) throw new SQLException("Evento no encontrado.");
        }
    }

    public void eliminar(Connection c, int id) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("SELECT (SELECT COUNT(*) FROM transaccion_compra WHERE evento_id=?)+(SELECT COUNT(*) FROM entrada_digital WHERE evento_id=?)")) {
            p.setInt(1,id); p.setInt(2,id);
            try (ResultSet r=p.executeQuery()) { r.next(); if(r.getLong(1)>0) throw new SQLException("El evento tiene compras o entradas asociadas y no se puede borrar."); }
        }
        try (PreparedStatement p=c.prepareStatement("SELECT (SELECT COUNT(*) FROM dispositivo_validacion WHERE evento_id=?)+(SELECT COUNT(*) FROM control_acceso_puerta WHERE evento_id=?)")) {
            p.setInt(1,id);p.setInt(2,id);
            try(ResultSet r=p.executeQuery()){r.next();if(r.getLong(1)>0)throw new SQLException("El evento tiene registros de seguridad asociados y no se puede borrar.");}
        }
        try (PreparedStatement p=c.prepareStatement("DELETE FROM categoria_asiento WHERE evento_id=?")) { p.setInt(1,id);p.executeUpdate(); }
        try (PreparedStatement p=c.prepareStatement("DELETE FROM evento_cultural WHERE id=?")) {
            p.setInt(1,id); if(p.executeUpdate()!=1)throw new SQLException("Evento no encontrado.");
        }
    }

    private static void enlazarEvento(PreparedStatement p, EventoCultural e) throws SQLException {
        p.setString(1,e.getNombre()); p.setString(2,e.getDescripcion()); p.setString(3,e.getTipoEvento());
        p.setTimestamp(4,java.sql.Timestamp.valueOf(e.getFecha())); p.setInt(5,e.getAforo()); p.setInt(6,e.getLocalId()); p.setInt(7,e.getOrganizadorId());
        if(e.getArtistaId()==null)p.setNull(8,java.sql.Types.INTEGER);else p.setInt(8,e.getArtistaId());
        p.setString(9,e.getEstado());
    }

    private static EventoCultural mapearEvento(ResultSet r) throws SQLException {
        int artista=r.getInt("artista_id"); Integer artistaId=r.wasNull()?null:artista;
        return new EventoCultural(r.getInt("id"),r.getString("nombre"),r.getString("descripcion"),r.getString("tipo_evento"),
                r.getTimestamp("fecha").toLocalDateTime(),r.getInt("aforo"),r.getInt("local_id"),r.getInt("organizador_id"),artistaId,r.getString("estado"));
    }

    public List<EventoResumenDTO> buscar(Connection c, String nombre, String artista, String tipo) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT e.id, e.nombre, e.descripcion, e.tipo_evento, e.fecha, e.aforo, l.nombre_local, " +
                "COALESCE(a.nombre_artistico, 'Sin artista') AS artista_nombre, MIN(c.precio_base) AS precio_desde " +
                "FROM evento_cultural e " +
                "LEFT JOIN local_concierto l ON l.id = e.local_id " +
                "LEFT JOIN artista a ON a.id = e.artista_id " +
                "LEFT JOIN categoria_asiento c ON c.evento_id = e.id " +
                "WHERE e.estado = 'PUBLICADO' "
        );

        List<Object> params = new ArrayList<>();
        if (nombre != null && !nombre.isBlank()) {
            sql.append("AND (e.nombre LIKE ? OR a.nombre_artistico LIKE ?) ");
            params.add("%" + nombre.trim() + "%");
            params.add("%" + nombre.trim() + "%");
        }
        if (artista != null && !artista.isBlank()) {
            sql.append("AND a.nombre_artistico LIKE ? ");
            params.add("%" + artista.trim() + "%");
        }
        if (tipo != null && !tipo.isBlank()) {
            sql.append("AND e.tipo_evento = ? ");
            params.add(tipo.trim().toUpperCase(Locale.ROOT));
        }

        sql.append("GROUP BY e.id, e.nombre, e.descripcion, e.tipo_evento, e.fecha, e.aforo, l.nombre_local, a.nombre_artistico ORDER BY e.fecha ASC");

        List<EventoResumenDTO> eventos = new ArrayList<>();
        try (PreparedStatement p = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                p.setObject(i + 1, params.get(i));
            }
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    eventos.add(new EventoResumenDTO(
                            r.getInt("id"),
                            r.getString("nombre"),
                            r.getString("descripcion"),
                            r.getString("tipo_evento"),
                            r.getTimestamp("fecha").toLocalDateTime(),
                            r.getInt("aforo"),
                            r.getString("nombre_local"),
                            r.getString("artista_nombre"),
                            r.getBigDecimal("precio_desde")
                    ));
                }
            }
        }
        return eventos;
    }

    public java.util.Optional<EventoDetalleDTO> buscarDetallePublicoPorId(Connection c, int id) throws SQLException {
        String sql = "SELECT e.id, e.nombre, e.descripcion, e.tipo_evento, e.fecha, e.aforo, " +
                     "l.nombre_local, l.ciudad, COALESCE(a.nombre_artistico, 'Sin artista') AS artista_nombre " +
                     "FROM evento_cultural e " +
                     "LEFT JOIN local_concierto l ON l.id = e.local_id " +
                     "LEFT JOIN artista a ON a.id = e.artista_id " +
                     "WHERE e.id = ? AND e.estado = 'PUBLICADO'";

        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) {
                    return java.util.Optional.empty();
                }
                EventoDetalleDTO dto = new EventoDetalleDTO(
                        r.getInt("id"),
                        r.getString("nombre"),
                        r.getString("descripcion"),
                        r.getString("tipo_evento"),
                        r.getTimestamp("fecha").toLocalDateTime(),
                        r.getInt("aforo"),
                        r.getString("nombre_local"),
                        r.getString("ciudad"),
                        r.getString("artista_nombre")
                );
                return java.util.Optional.of(dto);
            }
        }
    }
}
