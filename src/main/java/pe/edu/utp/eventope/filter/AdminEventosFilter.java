package pe.edu.utp.eventope.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.eventope.dto.SesionUsuario;

import java.io.IOException;

@WebFilter("/admin/eventos/*")
public final class AdminEventosFilter implements Filter {
    @Override public void doFilter(jakarta.servlet.ServletRequest request,jakarta.servlet.ServletResponse response,FilterChain chain)throws IOException,ServletException{
        HttpServletRequest req=(HttpServletRequest)request;HttpServletResponse resp=(HttpServletResponse)response;
        Object value=req.getSession(false)==null?null:req.getSession(false).getAttribute("sesionUsuario");
        if(!(value instanceof SesionUsuario actor)){
            resp.sendRedirect(req.getContextPath()+"/login");return;
        }
        if(actor.getTipo()!=SesionUsuario.Tipo.PERSONAL||actor.getRol()!=SesionUsuario.Rol.ADMIN){resp.sendError(HttpServletResponse.SC_FORBIDDEN);return;}
        chain.doFilter(request,response);
    }
}
