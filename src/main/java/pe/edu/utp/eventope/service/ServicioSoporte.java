package pe.edu.utp.eventope.service;

import pe.edu.utp.eventope.config.ConexionBD;
import pe.edu.utp.eventope.config.Configuracion;
import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.dao.ReclamoSoporteDAO;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.model.ReclamoSoporte;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ServicioSoporte {

    private static final Set<String> ESTADOS_PERMITIDOS = Set.of(
            "PENDIENTE", "EN_REVISION", "RESUELTO", "CERRADO"
    );

    private final ProveedorConexion conexiones;
    private final ReclamoSoporteDAO reclamoDAO;

    public ServicioSoporte() {
        this(() -> new ConexionBD(Configuracion.cargar()).abrir(), new ReclamoSoporteDAO());
    }

    public ServicioSoporte(ProveedorConexion conexiones, ReclamoSoporteDAO reclamoDAO) {
        this.conexiones = conexiones;
        this.reclamoDAO = reclamoDAO;
    }

    public ReclamoSoporte registrarReclamo(
            SesionUsuario actor,
            Integer incidenciaId,
            String asunto,
            String descripcion
    ) {
        if (actor == null) {
            throw new SecurityException("Sesión no iniciada.");
        }
        actor.exigirCliente();

        String asuntoLimpio = validarTextoObligatorio(asunto, "El asunto", 150);
        String descripcionLimpia = validarTextoObligatorio(descripcion, "La descripción", 2000);
        if (incidenciaId != null && incidenciaId <= 0) {
            throw new IllegalArgumentException("El identificador de incidencia no es válido.");
        }

        ReclamoSoporte reclamo = new ReclamoSoporte(
                0,
                actor.getId(),
                null,
                incidenciaId,
                asuntoLimpio,
                descripcionLimpia,
                "PENDIENTE",
                LocalDateTime.now(),
                null
        );

        try (Connection c = conexiones.abrir()) {
            reclamoDAO.insertar(c, reclamo);
            return reclamo;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo registrar el reclamo.", e);
        }
    }

    public List<ReclamoSoporte> listarReclamosCliente(SesionUsuario actor) {
        if (actor == null) {
            throw new SecurityException("Sesión no iniciada.");
        }
        actor.exigirCliente();

        try (Connection c = conexiones.abrir()) {
            return reclamoDAO.listarPorCliente(c, actor.getId());
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron cargar tus reclamos.", e);
        }
    }

    public List<ReclamoSoporte> listarReclamosGestion(SesionUsuario actor) {
        exigirSoporteOAdmin(actor);
        try (Connection c = conexiones.abrir()) {
            return reclamoDAO.listarTodos(c);
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron cargar los reclamos de soporte.", e);
        }
    }

    public void responderReclamo(
            SesionUsuario actor,
            int reclamoId,
            String respuesta,
            String nuevoEstado
    ) {
        exigirSoporteOAdmin(actor);
        if (reclamoId <= 0) {
            throw new IllegalArgumentException("Identificador de reclamo inválido.");
        }
        String respuestaLimpia = validarTextoObligatorio(respuesta, "La respuesta", 2000);
        String estadoNormalizado = normalizarEstado(nuevoEstado);

        try (Connection c = conexiones.abrir()) {
            if (reclamoDAO.buscarPorId(c, reclamoId).isEmpty()) {
                throw new IllegalArgumentException("El reclamo indicado no existe.");
            }
            boolean ok = reclamoDAO.responderYActualizarEstado(
                    c, reclamoId, actor.getId(), respuestaLimpia, estadoNormalizado
            );
            if (!ok) {
                throw new IllegalStateException("No se pudo actualizar el reclamo.");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al responder el reclamo.", e);
        }
    }

    private void exigirSoporteOAdmin(SesionUsuario actor) {
        if (actor == null) {
            throw new SecurityException("Sesión no iniciada.");
        }
        actor.exigirSoporte();
    }

    private String validarTextoObligatorio(String valor, String campo, int maxLen) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
        String limpio = valor.trim();
        if (limpio.isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío.");
        }
        if (limpio.length() > maxLen) {
            throw new IllegalArgumentException(campo + " no puede superar los " + maxLen + " caracteres.");
        }
        return limpio;
    }

    private String normalizarEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return "RESUELTO";
        }
        String normalizado = estado.trim().toUpperCase(Locale.ROOT);
        if (!ESTADOS_PERMITIDOS.contains(normalizado)) {
            throw new IllegalArgumentException(
                    "Estado inválido. Use PENDIENTE, EN_REVISION, RESUELTO o CERRADO."
            );
        }
        return normalizado;
    }
}
