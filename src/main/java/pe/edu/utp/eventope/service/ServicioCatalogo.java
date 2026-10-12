package pe.edu.utp.eventope.service;

import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.dao.CategoriaAsientoDAO;
import pe.edu.utp.eventope.dao.EventoDAO;
import pe.edu.utp.eventope.dto.EventoDetalleDTO;
import pe.edu.utp.eventope.dto.EventoResumenDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class ServicioCatalogo {
    private final ProveedorConexion conexiones;
    private final EventoDAO eventoDAO = new EventoDAO();
    private final CategoriaAsientoDAO categoriaDAO = new CategoriaAsientoDAO();

    public ServicioCatalogo(ProveedorConexion conexiones) {
        this.conexiones = conexiones;
    }

    public List<EventoResumenDTO> buscar(String nombre, String artista, String tipo) throws SQLException {
        try (Connection c = conexiones.abrir()) {
            return eventoDAO.buscar(c, sanea(nombre), sanea(artista), sanea(tipo));
        }
    }

    public EventoDetalleDTO detalle(int eventoId) throws SQLException {
        if (eventoId <= 0) {
            throw new IllegalArgumentException("Id de evento inválido");
        }
        try (Connection c = conexiones.abrir()) {
            EventoDetalleDTO evento = eventoDAO.buscarDetallePublicoPorId(c, eventoId)
                    .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));
            evento.setCategorias(categoriaDAO.listarPorEvento(c, eventoId));
            return evento;
        }
    }

    private String sanea(String valor) {
        if (valor == null) {
            return null;
        }
        String nueva = valor.trim();
        return nueva.isEmpty() ? null : nueva;
    }
}
