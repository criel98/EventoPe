package pe.edu.utp.eventope.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import pe.edu.utp.eventope.config.ConexionBD;
import pe.edu.utp.eventope.config.Configuracion;
import pe.edu.utp.eventope.dto.ResultadoDevolucion;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.service.ServicioDevolucion;
import pe.edu.utp.eventope.util.Csrf;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/compras/devolver")
public final class DevolucionServlet extends HttpServlet {
    private ServicioDevolucion servicio;
    @Override public void init() throws ServletException {
        try { servicio=new ServicioDevolucion(new ConexionBD(Configuracion.cargar())); }
        catch(RuntimeException e){ throw new ServletException("No se pudo configurar devoluciones",e); }
    }

    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws IOException,ServletException {
        Object o=req.getSession().getAttribute("sesionUsuario");
        if(!(o instanceof SesionUsuario actor)||actor.getTipo()!=SesionUsuario.Tipo.CLIENTE) {
            resp.sendRedirect(req.getContextPath()+"/login"); return;
        }
        req.setAttribute("csrf",Csrf.token(req.getSession()));
        req.setAttribute("transaccionId",req.getParameter("id"));
        resp.setHeader("Cache-Control","no-store");
        req.getRequestDispatcher("/WEB-INF/views/compras/devolucion.jsp").forward(req,resp);
    }

    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws IOException,ServletException {
        if(!Csrf.valido(req.getSession(false),req.getParameter("csrf"))) {
            resp.sendError(403,"Token de seguridad inválido"); return;
        }
        Object o=req.getSession().getAttribute("sesionUsuario");
        if(!(o instanceof SesionUsuario actor)||actor.getTipo()!=SesionUsuario.Tipo.CLIENTE) {
            resp.sendRedirect(req.getContextPath()+"/login"); return;
        }
        try {
            int id=Integer.parseInt(req.getParameter("transaccionId"));
            ResultadoDevolucion r=servicio.devolver(actor.getId(),id);
            req.setAttribute("resultado",r);
            req.getRequestDispatcher("/WEB-INF/views/compras/devolucion.jsp").forward(req,resp);
        } catch(IllegalArgumentException|SQLException e) {
            req.setAttribute("error",e.getMessage());
            req.setAttribute("transaccionId",req.getParameter("transaccionId"));
            req.setAttribute("csrf",Csrf.token(req.getSession()));
            req.getRequestDispatcher("/WEB-INF/views/compras/devolucion.jsp").forward(req,resp);
        }
    }
}
