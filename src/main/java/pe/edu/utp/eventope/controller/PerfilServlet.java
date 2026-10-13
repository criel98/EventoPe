package pe.edu.utp.eventope.controller;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.service.*;
import pe.edu.utp.eventope.util.Csrf;
import java.io.IOException;
@WebServlet(urlPatterns={"/cuenta/perfil","/perfil"})
public final class PerfilServlet extends HttpServlet {
    private final ServicioCuenta servicio=new ServicioCuenta();
    private SesionUsuario actor(HttpServletRequest req){return (SesionUsuario)req.getSession(false).getAttribute("sesionUsuario");}
    protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
        try{req.setAttribute("datos",FormularioCuenta.desdePerfil(servicio.obtenerPerfilCompleto(actor(req))));}
        catch(SecurityException e){res.sendError(403);return;}
        catch(IllegalStateException e){res.setStatus(503);req.setAttribute("error","No se pudo cargar el perfil. Inténtalo nuevamente.");}
        mostrar(req,res);
    }
    protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
        var datos=FormularioCuenta.valores(req);req.setAttribute("datos",datos);
        try{
            var u=FormularioCuenta.cliente(datos);var actor=actor(req);
            servicio.actualizarPerfil(actor,u,FormularioCuenta.perfil(datos),FormularioCuenta.preferencias(datos));
            req.getSession().setAttribute("sesionUsuario",new SesionUsuario(actor.getId(),actor.getTipo(),actor.getRol(),u.getNombres()+" "+u.getApellidos()));
            res.sendRedirect(req.getContextPath()+"/cuenta/perfil?guardado=ok");return;
        }catch(ValidacionCuentaException e){res.setStatus(400);req.setAttribute("errores",e.getErrores());}
        catch(SecurityException e){res.sendError(403);return;}
        catch(IllegalStateException e){res.setStatus(503);req.setAttribute("error","No se pudo guardar el perfil. Inténtalo nuevamente.");}
        mostrar(req,res);
    }
    private void mostrar(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
        res.setHeader("Cache-Control","no-store");Csrf.token(req.getSession());req.getRequestDispatcher("/WEB-INF/views/cuenta/perfil.jsp").forward(req,res);
    }
}
