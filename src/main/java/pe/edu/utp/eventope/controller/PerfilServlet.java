package pe.edu.utp.eventope.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.service.ServicioCuenta;

import java.io.IOException;
import java.util.Map;

@WebServlet("/perfil")
public class PerfilServlet extends HttpServlet {

    private final ServicioCuenta servicioCuenta = new ServicioCuenta();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession sesion = req.getSession(false);
        
        // Verificar si el usuario ha iniciado sesión con la clave correcta "sesionUsuario"
        if (sesion == null || sesion.getAttribute("sesionUsuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        SesionUsuario sesionUsuario = (SesionUsuario) sesion.getAttribute("sesionUsuario");
        int clienteId = sesionUsuario.getId();

        // Validar que sea estrictamente un cliente y tenga un ID válido
        if (clienteId <= 0 || sesionUsuario.getTipo() != SesionUsuario.Tipo.CLIENTE) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Consultar el perfil completo a través del servicio
        Map<String, Object> perfilCompleto = servicioCuenta.obtenerPerfilCompleto(clienteId);

        req.setAttribute("perfilCompleto", perfilCompleto);
        req.getRequestDispatcher("/WEB-INF/views/auth/perfil.jsp").forward(req, resp);
    }
}
 
