package pe.edu.utp.eventope.service;

import pe.edu.utp.eventope.config.ConexionBD;
import pe.edu.utp.eventope.config.Configuracion;
import pe.edu.utp.eventope.dao.PerfilUsuarioDAO;
import pe.edu.utp.eventope.dao.PreferenciasDAO;
import pe.edu.utp.eventope.dao.UsuarioClienteDAO;
import pe.edu.utp.eventope.model.PerfilUsuario;
import pe.edu.utp.eventope.model.PreferenciasMusicales;
import pe.edu.utp.eventope.model.UsuarioCliente;
import pe.edu.utp.eventope.util.PasswordUtil;

import java.sql.Connection;
import java.sql.SQLException;

public final class ServicioCuenta {

    private final UsuarioClienteDAO usuarioClienteDAO = new UsuarioClienteDAO();
    private final PerfilUsuarioDAO perfilUsuarioDAO = new PerfilUsuarioDAO();
    private final PreferenciasDAO preferenciasDAO = new PreferenciasDAO();

    public boolean registrarClienteCompleto(UsuarioCliente cliente, char[] passwordPlainText, PerfilUsuario perfil, PreferenciasMusicales preferencias) {
        if (cliente.getDni() == null || !cliente.getDni().matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos numéricos.");
        }
        if (passwordPlainText == null || passwordPlainText.length < 12 || passwordPlainText.length > 128) {
            throw new IllegalArgumentException("La contraseña debe tener entre 12 y 128 caracteres.");
        }

        // Usar el método correcto 'crear' de PasswordUtil
        String hash = PasswordUtil.crear(passwordPlainText);
        cliente.setContrasenaHash(hash);
        cliente.setEstado("ACTIVO");

        try (Connection conn = new ConexionBD(Configuracion.cargar()).abrir()) {
            conn.setAutoCommit(false); // Iniciar transacción atómica

            try {
                // 1. Registrar el cliente principal
                usuarioClienteDAO.insertar(conn, cliente);
                
                if (cliente.getId() <= 0) {
                    conn.rollback();
                    return false;
                }

                // 2. Registrar el perfil asociado
                perfil.setClienteId(cliente.getId());
                perfilUsuarioDAO.insertar(conn, perfil);

                // 3. Registrar las preferencias musicales asociadas
                preferencias.setClienteId(cliente.getId());
                preferenciasDAO.insertar(conn, preferencias);

                conn.commit(); // Confirmar transacción completa
                return true;

            } catch (SQLException e) {
                conn.rollback(); // Revertir todo si ocurre un error
                e.printStackTrace();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public java.util.Map<String, Object> obtenerPerfilCompleto(int clienteId) {
        java.util.Map<String, Object> resultado = new java.util.HashMap<>();
        try (Connection conn = new ConexionBD(Configuracion.cargar()).abrir()) {
            usuarioClienteDAO.buscarPorId(conn, clienteId).ifPresent(c -> resultado.put("cliente", c));
            perfilUsuarioDAO.buscarPorClienteId(conn, clienteId).ifPresent(p -> resultado.put("perfil", p));
            preferenciasDAO.buscarPorClienteId(conn, clienteId).ifPresent(pref -> resultado.put("preferencias", pref));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultado;
    }
}
 
