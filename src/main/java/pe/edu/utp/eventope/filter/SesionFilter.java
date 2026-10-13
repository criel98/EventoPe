package pe.edu.utp.eventope.filter;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import pe.edu.utp.eventope.dto.SesionUsuario;
import java.io.IOException;
public final class SesionFilter implements Filter {
    public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain)throws IOException,ServletException {
        var req=(HttpServletRequest)request;var res=(HttpServletResponse)response;
        res.setHeader("Cache-Control","no-store");
        String ruta=req.getRequestURI().substring(req.getContextPath().length());
        boolean seguridad=ruta.equals("/seguridad")||ruta.startsWith("/seguridad/");
        var session=req.getSession(false);Object value=session==null?null:session.getAttribute("sesionUsuario");
        if(!(value instanceof SesionUsuario actor)){
            if("GET".equals(req.getMethod()))res.sendRedirect(req.getContextPath()+(seguridad?"/personal/login":"/login"));
            else res.sendError(401,"Inicia sesión para continuar");return;
        }
        if(seguridad?(actor.getTipo()!=SesionUsuario.Tipo.PERSONAL||actor.getRol()!=SesionUsuario.Rol.VALIDACION):actor.getTipo()!=SesionUsuario.Tipo.CLIENTE){res.sendError(403);return;}
        chain.doFilter(request,response);
    }
}
