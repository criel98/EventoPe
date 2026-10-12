package pe.edu.utp.eventope.controller;
import jakarta.servlet.*;import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import pe.edu.utp.eventope.dto.*;import pe.edu.utp.eventope.util.Csrf;import java.io.IOException;
@WebServlet(urlPatterns={"/carrito","/compras/carrito"})
public final class CarritoServlet extends HttpServlet {
 protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{Object o=req.getSession().getAttribute("carritoCompra");req.setAttribute("carrito",o instanceof CarritoCompra?o:null);req.setAttribute("csrf",Csrf.token(req.getSession()));req.getRequestDispatcher("/WEB-INF/views/compras/carrito.jsp").forward(req,resp);}
 protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{req.setCharacterEncoding("UTF-8");if(!Csrf.valido(req.getSession(false),req.getParameter("csrf"))){resp.sendError(403,"Token de seguridad inválido");return;}try{CarritoCompra c=(CarritoCompra)req.getSession().getAttribute("carritoCompra");if(c==null)throw new IllegalArgumentException("El carrito está vacío.");resp.sendRedirect(req.getContextPath()+"/compras/revision");}catch(IllegalArgumentException e){req.getSession().setAttribute("errorCompra",e.getMessage());resp.sendRedirect(req.getContextPath()+"/carrito");}}
}
