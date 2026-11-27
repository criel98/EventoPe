package pe.edu.utp.eventope.controller;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import pe.edu.utp.eventope.service.*;
import pe.edu.utp.eventope.util.Csrf;
import java.io.IOException;
import java.util.Arrays;
@WebServlet("/registro")
public final class RegistroServlet extends HttpServlet {
    private final ServicioCuenta servicio=new ServicioCuenta();
    protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{mostrar(req,res);}
    protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
        var datos=FormularioCuenta.valores(req);req.setAttribute("datos",datos);
        String raw=req.getParameter("password");char[] clave=raw==null?new char[0]:raw.toCharArray();
        try{
            servicio.registrarClienteCompleto(FormularioCuenta.cliente(datos),clave,FormularioCuenta.perfil(datos),FormularioCuenta.preferencias(datos));
            res.sendRedirect(req.getContextPath()+"/login?registro=ok");return;
        }catch(ValidacionCuentaException e){res.setStatus(400);req.setAttribute("errores",e.getErrores());}
        catch(IllegalStateException e){res.setStatus(503);req.setAttribute("error","No se pudo guardar la cuenta. Inténtalo nuevamente.");}
        finally{Arrays.fill(clave,'\0');}
        mostrar(req,res);
    }
    private void mostrar(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
        res.setHeader("Cache-Control","no-store");Csrf.token(req.getSession());req.getRequestDispatcher("/WEB-INF/views/auth/registro.jsp").forward(req,res);
    }
}
