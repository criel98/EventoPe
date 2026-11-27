package pe.edu.utp.eventope.controller;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import pe.edu.utp.eventope.service.ServicioAutenticacion;
import pe.edu.utp.eventope.util.Csrf;
import java.io.IOException;
import java.util.Arrays;
@WebServlet("/personal/login")
public final class PersonalLoginServlet extends HttpServlet {
    private final ServicioAutenticacion servicio=new ServicioAutenticacion();
    protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{mostrar(req,res);}
    protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
        String identificador=req.getParameter("username");String raw=req.getParameter("password");char[] clave=raw==null?new char[0]:raw.toCharArray();
        req.setAttribute("identificador",identificador);
        try{
            var actor=servicio.autenticarPersonal(identificador,clave);
            if(actor.isPresent()){
                var antigua=req.getSession(false);if(antigua!=null)antigua.invalidate();
                var nueva=req.getSession(true);nueva.setAttribute("sesionUsuario",actor.get());Csrf.token(nueva);
                res.sendRedirect(req.getContextPath()+"/seguridad/panel");return;
            }
            res.setStatus(401);req.setAttribute("error","Usuario o contraseña incorrectos.");
        }catch(IllegalStateException e){res.setStatus(503);req.setAttribute("error","No se pudo iniciar sesión. Inténtalo nuevamente.");}
        finally{Arrays.fill(clave,'\0');}
        mostrar(req,res);
    }
    private void mostrar(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
        req.setAttribute("mostrarCuentaDemo", Boolean.parseBoolean(pe.edu.utp.eventope.config.Configuracion.cargar().opcional("demo.mostrarCredenciales").orElse("false")));
        res.setHeader("Cache-Control","no-store");Csrf.token(req.getSession());req.getRequestDispatcher("/WEB-INF/views/auth/personal-login.jsp").forward(req,res);
    }
}
