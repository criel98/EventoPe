package pe.edu.utp.eventope.controller;

import pe.edu.utp.eventope.model.UsuarioCliente;
import pe.edu.utp.eventope.model.PerfilUsuario;
import pe.edu.utp.eventope.model.PreferenciasMusicales;
import pe.edu.utp.eventope.service.ServicioCuenta;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@WebServlet("/registro")
public final class RegistroServlet extends HttpServlet {

    private final ServicioCuenta servicioCuenta = new ServicioCuenta();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Genera el token CSRF con la clave "csrf" que espera el filtro CsrfFilter
        HttpSession session = request.getSession();
        if (session.getAttribute("csrf") == null) {
            String token = UUID.randomUUID().toString();
            session.setAttribute("csrf", token);
        }
        
        request.getRequestDispatcher("/WEB-INF/views/auth/registro.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 1. Recoger parámetros del formulario de registro
            String nombres = request.getParameter("nombres");
            String apellidos = request.getParameter("apellidos");
            String dni = request.getParameter("dni");
            String email = request.getParameter("email");
            String passwordStr = request.getParameter("password");
            String telefono = request.getParameter("telefono");
            String pais = request.getParameter("pais");
            String fechaNacStr = request.getParameter("fechaNacimiento");
            
            // Parámetros de preferencias musicales
            String generoMusical = request.getParameter("generoMusical");
            String artistaFavorito = request.getParameter("artistaFavorito");

            if (passwordStr == null) {
                passwordStr = "";
            }
            char[] passwordChars = passwordStr.toCharArray();

            // 2. Construir objeto UsuarioCliente
            UsuarioCliente cliente = new UsuarioCliente();
            cliente.setNombres(nombres);
            cliente.setApellidos(apellidos);
            cliente.setDni(dni);
            cliente.setEmail(email);
            cliente.setTelefono(telefono);

            // 3. Construir objeto PerfilUsuario
            PerfilUsuario perfil = new PerfilUsuario();
            perfil.setPais(pais != null && !pais.isEmpty() ? pais : "Perú");
            if (fechaNacStr != null && !fechaNacStr.isEmpty()) {
                perfil.setFechaNacimiento(LocalDate.parse(fechaNacStr));
            } else {
                perfil.setFechaNacimiento(LocalDate.of(2000, 1, 1));
            }
            perfil.setEstadoVerificacion(false);

            // 4. Construir PreferenciasMusicales
            PreferenciasMusicales preferencias = new PreferenciasMusicales();
            preferencias.setGeneroFavorito(generoMusical != null && !generoMusical.isEmpty() ? generoMusical : "Rock");
            preferencias.setArtistaFavorito(artistaFavorito != null && !artistaFavorito.isEmpty() ? artistaFavorito : "Soda Stereo");

            // 5. Ejecutar el registro completo y transaccional
            boolean exito = servicioCuenta.registrarClienteCompleto(cliente, passwordChars, perfil, preferencias);

            if (exito) {
                response.sendRedirect(request.getContextPath() + "/login");
            } else {
                request.setAttribute("error", "No se pudo completar el registro. Verifique sus datos.");
                request.getRequestDispatcher("/WEB-INF/views/auth/registro.jsp").forward(request, response);
            }

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/auth/registro.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ocurrió un error inesperado: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/auth/registro.jsp").forward(request, response);
        }
    }
}
