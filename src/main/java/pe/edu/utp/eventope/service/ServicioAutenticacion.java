package pe.edu.utp.eventope.service;

import pe.edu.utp.eventope.config.ConexionBD;
import pe.edu.utp.eventope.config.Configuracion;
import pe.edu.utp.eventope.dao.PersonalDAO;
import pe.edu.utp.eventope.dao.UsuarioClienteDAO;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.model.AdministradorPlataforma;
import pe.edu.utp.eventope.model.UsuarioCliente;
import pe.edu.utp.eventope.util.PasswordUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public final class ServicioAutenticacion {

    private final UsuarioClienteDAO usuarioClienteDAO = new UsuarioClienteDAO();
    private final PersonalDAO personalDAO = new PersonalDAO();

    public Optional<SesionUsuario> autenticarCliente(String email, char[] password) {
        try (Connection conn = new ConexionBD(Configuracion.cargar()).abrir()) {
            Optional<UsuarioCliente> optCliente = usuarioClienteDAO.buscarPorEmail(conn, email);
            if (optCliente.isPresent()) {
                UsuarioCliente cliente = optCliente.get();
                if ("ACTIVO".equalsIgnoreCase(cliente.getEstado()) &&
                    PasswordUtil.verificar(password, cliente.getContrasenaHash())) {
                    
                    String nombreCompleto = cliente.getNombres() + " " + cliente.getApellidos();
                    SesionUsuario sesion = new SesionUsuario(
                        cliente.getId(),
                        SesionUsuario.Tipo.CLIENTE,
                        SesionUsuario.Rol.CLIENTE,
                        nombreCompleto
                    );
                    return Optional.of(sesion);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<SesionUsuario> autenticarPersonal(String username, char[] password) {
        try (Connection conn = new ConexionBD(Configuracion.cargar()).abrir()) {
            Optional<AdministradorPlataforma> optAdmin = personalDAO.buscarPorUsername(conn, username);
            if (optAdmin.isPresent()) {
                AdministradorPlataforma admin = optAdmin.get();
                if ("ACTIVO".equalsIgnoreCase(admin.getEstado()) &&
                    PasswordUtil.verificar(password, admin.getContrasenaHash())) {
                    
                    SesionUsuario.Rol rol;
                    String nivel = admin.getNivelAcceso();
                    if ("VALIDACION".equalsIgnoreCase(nivel)) {
                        rol = SesionUsuario.Rol.VALIDACION;
                    } else if ("SOPORTE".equalsIgnoreCase(nivel)) {
                        rol = SesionUsuario.Rol.SOPORTE;
                    } else {
                        rol = SesionUsuario.Rol.ADMIN;
                    }

                    SesionUsuario sesion = new SesionUsuario(
                        admin.getId(),
                        SesionUsuario.Tipo.PERSONAL,
                        rol,
                        admin.getUsername()
                    );
                    return Optional.of(sesion);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
