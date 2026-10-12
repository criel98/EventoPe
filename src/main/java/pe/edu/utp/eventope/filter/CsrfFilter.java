package pe.edu.utp.eventope.filter;

import jakarta.servlet.*;
import java.io.IOException;

public final class CsrfFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    } 
}
