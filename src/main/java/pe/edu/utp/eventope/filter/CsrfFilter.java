package pe.edu.utp.eventope.filter;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import pe.edu.utp.eventope.util.HashUtil;
import java.io.IOException;
import java.util.Set;
public final class CsrfFilter implements Filter {
    public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain)throws IOException,ServletException {
        var req=(HttpServletRequest)request;var res=(HttpServletResponse)response;
        if(!Set.of("GET","HEAD","OPTIONS").contains(req.getMethod())){
            var session=req.getSession(false);
            String esperado=session==null?null:(String)session.getAttribute("csrf");
            String recibido=req.getHeader("X-CSRF-Token");if(recibido==null)recibido=req.getParameter("csrf");
            if(!HashUtil.iguales(esperado,recibido)){res.sendError(403,"El formulario venció. Recarga la página e inténtalo de nuevo.");return;}
        }
        chain.doFilter(request,response);
    }
}
