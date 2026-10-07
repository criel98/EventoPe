package pe.edu.utp.eventope.filter;
import jakarta.servlet.*;
import java.io.IOException;
public final class Utf8Filter implements Filter {
    public void doFilter(ServletRequest req,ServletResponse res,FilterChain chain)throws IOException,ServletException{req.setCharacterEncoding("UTF-8");res.setCharacterEncoding("UTF-8");chain.doFilter(req,res);}
}
