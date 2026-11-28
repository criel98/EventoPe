package pe.edu.utp.eventope.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import pe.edu.utp.eventope.dto.SesionUsuario;

import java.io.IOException;

public final class SesionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var req = (HttpServletRequest) request;
        var res = (HttpServletResponse) response;
        res.setHeader("Cache-Control", "no-store");

        String ruta = req.getRequestURI().substring(req.getContextPath().length());

        boolean esAdmin = ruta.equals("/admin") || ruta.startsWith("/admin/");
        boolean esSoporte = ruta.equals("/soporte") || ruta.startsWith("/soporte/");
        boolean esSeguridad = ruta.equals("/seguridad") || ruta.startsWith("/seguridad/");
        boolean esPersonalRuta = esAdmin || esSoporte || esSeguridad;

        var session = req.getSession(false);
        Object value = session == null ? null : session.getAttribute("sesionUsuario");

        if (!(value instanceof SesionUsuario actor)) {
            if ("GET".equals(req.getMethod())) {
                res.sendRedirect(req.getContextPath() + (esPersonalRuta ? "/personal/login" : "/login"));
            } else {
                res.sendError(401, "Inicia sesión para continuar");
            }
            return;
        }

        boolean permitido;
        if (esAdmin) {
            permitido = actor.getTipo() == SesionUsuario.Tipo.PERSONAL && actor.getRol() == SesionUsuario.Rol.ADMIN;
        } else if (esSoporte) {
            permitido = actor.getTipo() == SesionUsuario.Tipo.PERSONAL
                    && (actor.getRol() == SesionUsuario.Rol.SOPORTE || actor.getRol() == SesionUsuario.Rol.ADMIN);
        } else if (esSeguridad) {
            permitido = actor.getTipo() == SesionUsuario.Tipo.PERSONAL && actor.getRol() == SesionUsuario.Rol.VALIDACION;
        } else {
            permitido = actor.getTipo() == SesionUsuario.Tipo.CLIENTE && actor.getRol() == SesionUsuario.Rol.CLIENTE;
        }

        if (!permitido) {
            res.sendError(403, "No tienes permisos para acceder a este recurso.");
            return;
        }

        chain.doFilter(request, response);
    }
}
