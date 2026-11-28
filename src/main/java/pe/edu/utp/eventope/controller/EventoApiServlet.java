package pe.edu.utp.eventope.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.eventope.config.ConexionBD;
import pe.edu.utp.eventope.config.Configuracion;
import pe.edu.utp.eventope.service.ServicioApiEventos;
import pe.edu.utp.eventope.util.Json;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/api/eventos/*")
public final class EventoApiServlet extends HttpServlet {
    private ServicioApiEventos servicio;
    EventoApiServlet() {}
    EventoApiServlet(ServicioApiEventos servicio){this.servicio=servicio;}
    @Override public void init()throws ServletException{
        if(servicio!=null)return;
        try{servicio=new ServicioApiEventos(new ConexionBD(Configuracion.cargar()));}
        catch(RuntimeException e){throw new ServletException("Falta configuración de la API de eventos.",e);}
    }
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws IOException{
        resp.setContentType("application/json");resp.setCharacterEncoding("UTF-8");resp.setHeader("Cache-Control","no-store");
        String ruta=req.getPathInfo();
        try{
            if(ruta==null||ruta.isEmpty()||"/".equals(ruta)){
                escribir(resp,HttpServletResponse.SC_OK,servicio.listar(req.getParameter("categoria"),req.getParameter("ciudad"),req.getParameter("fecha"),req.getParameter("q")));
                return;
            }
            if(!ruta.matches("/[^/]+")){escribirError(resp,HttpServletResponse.SC_BAD_REQUEST,"La ruta debe incluir un único ID numérico.");return;}
            String idTexto=ruta.substring(1);final int id;
            if(!idTexto.matches("[0-9]+")){escribirError(resp,HttpServletResponse.SC_BAD_REQUEST,"El ID debe ser numérico.");return;}
            try{id=Integer.parseInt(idTexto);}catch(NumberFormatException e){escribirError(resp,HttpServletResponse.SC_BAD_REQUEST,"El ID está fuera del rango permitido.");return;}
            if(id<=0){escribirError(resp,HttpServletResponse.SC_NOT_FOUND,"Evento inexistente o no publicado.");return;}
            var evento=servicio.detalle(id);
            if(evento.isEmpty())escribirError(resp,HttpServletResponse.SC_NOT_FOUND,"Evento inexistente o no publicado.");
            else escribir(resp,HttpServletResponse.SC_OK,evento.get());
        }catch(IllegalArgumentException e){escribirError(resp,HttpServletResponse.SC_BAD_REQUEST,e.getMessage());}
        catch(SQLException e){getServletContext().log("Error consultando la API de eventos.",e);escribirError(resp,HttpServletResponse.SC_SERVICE_UNAVAILABLE,"La API no está disponible temporalmente.");}
    }
    private static void escribir(HttpServletResponse resp,int status,Object valor)throws IOException{resp.setStatus(status);resp.getWriter().write(Json.serializar(valor));}
    private static void escribirError(HttpServletResponse resp,int status,String mensaje)throws IOException{java.util.Map<String,String> error=java.util.Map.of("error",mensaje==null?"Solicitud inválida.":mensaje);escribir(resp,status,error);}
}
