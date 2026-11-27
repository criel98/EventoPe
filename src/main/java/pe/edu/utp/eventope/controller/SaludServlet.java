package pe.edu.utp.eventope.controller;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
/** Comprueba despliegue HTTP; no afirma conexión a BD ni compra implementada. */
@WebServlet("/salud")
public final class SaludServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException{res.setContentType("text/plain;charset=UTF-8");res.setHeader("Cache-Control","no-store");res.getWriter().println("EventoPe: despliegue disponible. Revisar README para el estado de funciones.");}
}
