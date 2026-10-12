package pe.edu.utp.eventope.dao;

import pe.edu.utp.eventope.dto.EventoDetalleDTO;
import pe.edu.utp.eventope.dto.EventoResumenDTO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class EventoDAO {
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
