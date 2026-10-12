package pe.edu.utp.eventope.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.service.ServicioAutenticacion;
import pe.edu.utp.eventope.util.Csrf;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Asegurar que la sesión tenga su token CSRF generado para el JSP
        HttpSession sesion = req.getSession(true);
        Csrf.token(sesion);

        // Redirigir al JSP de login de clientes
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String passwordStr = req.getParameter("password");

        if (email == null || passwordStr == null) {
            req.setAttribute("error", "Credenciales inválidas.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        char[] password = passwordStr.toCharArray();
        Optional<SesionUsuario> optSesion = servicioAutenticacion.autenticarCliente(email, password);

        if (optSesion.isPresent()) {
            SesionUsuario sesionUsuario = optSesion.get();

            // Renovar ID de sesión para evitar ataques de fijación de sesión
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession nuevaSesion = req.getSession(true);

            // Guardar la sesión bajo la clave requerida
            nuevaSesion.setAttribute("sesionUsuario", sesionUsuario);
            // Generar nuevo token CSRF para la nueva sesión
            Csrf.token(nuevaSesion);

            // Redirigir al catálogo o sección de cliente
            resp.sendRedirect(req.getContextPath() + "/catalogo");
        } else {
            // Error genérico para no exponer si el correo existe o no
            req.setAttribute("error", "Correo o contraseña incorrectos.");
            req.setAttribute("emailIngresado", email); // Conservar datos no sensibles
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }
} 
