package pe.edu.utp.eventope.service;

import pe.edu.utp.eventope.config.ConexionBD;
import pe.edu.utp.eventope.config.Configuracion;
import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.dao.PersonalDAO;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.model.AdministradorPlataforma;
import pe.edu.utp.eventope.util.PasswordUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ServicioPersonal {

    private static final Set<String> ROLES_VALIDOS = Set.of("ADMIN", "SOPORTE", "VALIDACION");
    private static final Set<String> ESTADOS_VALIDOS = Set.of("ACTIVO", "INACTIVO");

    private final ProveedorConexion conexiones;
    private final PersonalDAO personalDAO;

    public ServicioPersonal() {
        this(() -> new ConexionBD(Configuracion.cargar()).abrir(), new PersonalDAO());
    }

    public ServicioPersonal(ProveedorConexion conexiones, PersonalDAO personalDAO) {
        this.conexiones = conexiones;
        this.personalDAO = personalDAO;
    }

    public List<AdministradorPlataforma> listarPersonal(SesionUsuario actor) {
        exigirAdmin(actor);
        try (Connection c = conexiones.abrir()) {
            return personalDAO.listarTodos(c);
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener la lista de personal.", e);
        }
    }

    public AdministradorPlataforma crearPersonal(
            SesionUsuario actor,
            String username,
            char[] clave,
            String nivelAcceso,
            String areaSoporte,
            String estado
    ) {
        exigirAdmin(actor);
        String usuarioLimpio = validarUsername(username);
        String rolNormalizado = normalizarRol(nivelAcceso);
        String estadoNormalizado = normalizarEstado(estado);
        String areaLimpia = normalizarAreaSoporte(areaSoporte);

        if (clave == null || clave.length < 8 || clave.length > 128) {
            if (clave != null) {
                Arrays.fill(clave, '\0');
            }
            throw new IllegalArgumentException("La contraseña debe tener entre 8 y 128 caracteres.");
        }

        try {
            String hash = PasswordUtil.crear(clave);
            AdministradorPlataforma nuevo = new AdministradorPlataforma(
                    0,
                    usuarioLimpio,
                    hash,
                    rolNormalizado,
                    areaLimpia,
                    estadoNormalizado
            );
            try (Connection c = conexiones.abrir()) {
                if (personalDAO.buscarPorUsername(c, usuarioLimpio).isPresent()) {
                    throw new IllegalArgumentException("Ya existe un miembro de personal con ese nombre de usuario.");
                }
                personalDAO.insertar(c, nuevo);
                return nuevo;
            } catch (SQLException e) {
                throw new IllegalStateException("No se pudo registrar el miembro de personal.", e);
            }
        } finally {
            Arrays.fill(clave, '\0');
        }
    }

    public void actualizarPersonal(
            SesionUsuario actor,
            int idPersonal,
            String nivelAcceso,
            String areaSoporte,
            String estado
    ) {
        exigirAdmin(actor);
        if (idPersonal <= 0) {
            throw new IllegalArgumentException("Identificador de personal inválido.");
        }
        String rolNormalizado = normalizarRol(nivelAcceso);
        String estadoNormalizado = normalizarEstado(estado);
        String areaLimpia = normalizarAreaSoporte(areaSoporte);

        try (Connection c = conexiones.abrir()) {
            if (personalDAO.buscarPorId(c, idPersonal).isEmpty()) {
                throw new IllegalArgumentException("El miembro de personal indicado no existe.");
            }
            boolean actualizado = personalDAO.actualizarConfiguracion(
                    c, idPersonal, rolNormalizado, areaLimpia, estadoNormalizado
            );
            if (!actualizado) {
                throw new IllegalStateException("No se pudo actualizar el registro de personal.");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar el miembro de personal.", e);
        }
    }

    public void cambiarContrasena(SesionUsuario actor, int idPersonal, char[] nuevaClave) {
        exigirAdmin(actor);
        if (idPersonal <= 0) {
            if (nuevaClave != null) {
                Arrays.fill(nuevaClave, '\0');
            }
            throw new IllegalArgumentException("Identificador de personal inválido.");
        }
        if (nuevaClave == null || nuevaClave.length < 8 || nuevaClave.length > 128) {
            if (nuevaClave != null) {
                Arrays.fill(nuevaClave, '\0');
            }
            throw new IllegalArgumentException("La nueva contraseña debe tener entre 8 y 128 caracteres.");
        }
        try {
            String nuevoHash = PasswordUtil.crear(nuevaClave);
            try (Connection c = conexiones.abrir()) {
                if (personalDAO.buscarPorId(c, idPersonal).isEmpty()) {
                    throw new IllegalArgumentException("El miembro de personal indicado no existe.");
                }
                boolean ok = personalDAO.actualizarContrasenaHash(c, idPersonal, nuevoHash);
                if (!ok) {
                    throw new IllegalStateException("No se pudo actualizar la contraseña.");
                }
            } catch (SQLException e) {
                throw new IllegalStateException("Error al actualizar la contraseña del personal.", e);
            }
        } finally {
            Arrays.fill(nuevaClave, '\0');
        }
    }

    private void exigirAdmin(SesionUsuario actor) {
        if (actor == null) {
            throw new SecurityException("Sesión no iniciada.");
        }
        actor.exigirAdmin();
    }

    private String validarUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        String limpio = username.trim();
        if (limpio.isEmpty() || limpio.length() > 50) {
            throw new IllegalArgumentException("El nombre de usuario debe tener entre 1 y 50 caracteres.");
        }
        return limpio;
    }

    private String normalizarRol(String nivelAcceso) {
        if (nivelAcceso == null || nivelAcceso.isBlank()) {
            throw new IllegalArgumentException("El nivel de acceso es obligatorio.");
        }
        String rol = nivelAcceso.trim().toUpperCase(Locale.ROOT);
        if (!ROLES_VALIDOS.contains(rol)) {
            throw new IllegalArgumentException("Nivel de acceso inválido. Use ADMIN, SOPORTE o VALIDACION.");
        }
        return rol;
    }

    private String normalizarEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return "ACTIVO";
        }
        String est = estado.trim().toUpperCase(Locale.ROOT);
        if (!ESTADOS_VALIDOS.contains(est)) {
            throw new IllegalArgumentException("Estado inválido. Use ACTIVO o INACTIVO.");
        }
        return est;
    }

    private String normalizarAreaSoporte(String areaSoporte) {
        if (areaSoporte == null) {
            return null;
        }
        String limpio = areaSoporte.trim();
        if (limpio.isEmpty()) {
            return null;
        }
        if (limpio.length() > 50) {
            throw new IllegalArgumentException("El área de soporte no puede superar los 50 caracteres.");
        }
        return limpio;
    }
}
