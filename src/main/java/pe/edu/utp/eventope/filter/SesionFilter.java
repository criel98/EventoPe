package pe.edu.utp.eventope.filter;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import pe.edu.utp.eventope.dto.SesionUsuario;
import java.io.IOException;

public final class SesionFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        var req = (HttpServletRequest) request;
        var res = (HttpServletResponse) response;
        
        String ruta = req.getRequestURI().substring(req.getContextPath().length());

        // Permitir el paso libre a las rutas públicas de login y registro
        if (ruta.equals("/login") || ruta.equals("/registro") || ruta.startsWith("/assets/")) {
            chain.doFilter(request, response);
            return;
        }

        var sesion = req.getSession(false);
        Object raw = sesion == null ? null : sesion.getAttribute("sesionUsuario");
        if (!(raw instanceof SesionUsuario actor)) {
            res.sendError(401, "Inicia sesión para continuar");
            return;
        }

        boolean seguridad = ruta.equals("/seguridad") || ruta.startsWith("/seguridad/");
        if (seguridad ? actor.getRol() != SesionUsuario.Rol.VALIDACION : actor.getTipo() != SesionUsuario.Tipo.CLIENTE) {
            res.sendError(403);
            return;
        }

        res.setHeader("Cache-Control", "no-store");
        chain.doFilter(request, response);
    }
}
 
