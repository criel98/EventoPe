package pe.edu.utp.eventope.filter;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import pe.edu.utp.eventope.util.HashUtil;
import java.io.IOException;
public final class CsrfFilter implements Filter {
    public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain)throws IOException,ServletException{
        var req=(HttpServletRequest)request;var res=(HttpServletResponse)response;
        if(!java.util.Set.of("GET","HEAD","OPTIONS").contains(req.getMethod())){
            var s=req.getSession(false);String esperado=s==null?null:(String)s.getAttribute("csrf");
            String recibido=req.getHeader("X-CSRF-Token");if(recibido==null)recibido=req.getParameter("csrf");
            if(!HashUtil.iguales(esperado,recibido)){res.sendError(403,"Formulario vencido o no autorizado");return;}
        }
        chain.doFilter(request,response);
    }
}
