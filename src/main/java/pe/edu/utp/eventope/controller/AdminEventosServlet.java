package pe.edu.utp.eventope.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.eventope.config.ConexionBD;
import pe.edu.utp.eventope.config.Configuracion;
import pe.edu.utp.eventope.model.*;
import pe.edu.utp.eventope.service.ServicioAdminEventos;
import pe.edu.utp.eventope.util.Csrf;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/admin/eventos/*")
public final class AdminEventosServlet extends HttpServlet {
    private ServicioAdminEventos servicio;
    @Override public void init()throws ServletException{
        try{servicio=new ServicioAdminEventos(new ConexionBD(Configuracion.cargar()));}
        catch(RuntimeException e){throw new ServletException("Falta configuración de administración de eventos.",e);}
    }
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
        Csrf.token(req.getSession());String mensaje=(String)req.getSession().getAttribute("adminEventosMensaje");if(mensaje!=null){req.getSession().removeAttribute("adminEventosMensaje");req.setAttribute("mensaje",mensaje);}
        try{render(req,resp,ruta(req));}
        catch(IllegalArgumentException e){resp.sendError(HttpServletResponse.SC_BAD_REQUEST,e.getMessage());}
        catch(SQLException e){getServletContext().log("Error cargando la administración de eventos.",e);resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,"La administración no está disponible temporalmente.");}
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
        String ruta=ruta(req);
        try{guardar(req,ruta);req.getSession().setAttribute("adminEventosMensaje","Cambios guardados correctamente.");resp.sendRedirect(req.getContextPath()+url(ruta,req));}
        catch(IllegalArgumentException e){resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);req.setAttribute("error",e.getMessage());try{render(req,resp,ruta);}catch(SQLException sql){throw new ServletException(sql);}}
        catch(SQLException e){getServletContext().log("No se pudo guardar el cambio administrativo.",e);resp.setStatus(HttpServletResponse.SC_CONFLICT);req.setAttribute("error","No se pudo guardar el cambio. Verifica los datos y que el registro no tenga compras o entradas asociadas.");try{render(req,resp,ruta);}catch(SQLException sql){throw new ServletException(sql);}}
    }
    private void guardar(HttpServletRequest req,String ruta)throws SQLException{
        String accion=requerido(req,"accion");
        if("/".equals(ruta)){
            switch(accion){
                case "guardar"->{EventoCultural e=new EventoCultural();e.setId(entero(req,"id",0));e.setNombre(requerido(req,"nombre"));e.setDescripcion(vacioANull(req.getParameter("descripcion")));e.setTipoEvento(requerido(req,"tipoEvento"));e.setFecha(LocalDateTime.parse(requerido(req,"fecha")));e.setAforo(entero(req,"aforo",0));e.setLocalId(entero(req,"localId",0));e.setOrganizadorId(entero(req,"organizadorId",0));int a=entero(req,"artistaId",0);e.setArtistaId(a==0?null:a);servicio.guardar(e);}
                case "publicar"->servicio.publicar(entero(req,"id",0));
                case "desactivar"->servicio.desactivar(entero(req,"id",0));
                case "eliminar"->servicio.eliminarEvento(entero(req,"id",0));
                default->throw new IllegalArgumentException("Acción de evento no válida.");
            }
            return;
        }
        switch(ruta){
            case "/organizadores"->{
                if("eliminar".equals(accion)){servicio.eliminarOrganizador(entero(req,"id",0));return;}
                OrganizadorEvento o=new OrganizadorEvento(entero(req,"id",0),requerido(req,"ruc"),requerido(req,"razonSocial"),requerido(req,"emailContacto"),vacioANull(req.getParameter("telefono")));servicio.guardar(o);
            }
            case "/artistas"->{
                if("eliminar".equals(accion)){servicio.eliminarArtista(entero(req,"id",0));return;}
                Artista a=new Artista(entero(req,"id",0),requerido(req,"nombreArtistico"),vacioANull(req.getParameter("generoMusical")),vacioANull(req.getParameter("paisOrigen")),vacioANull(req.getParameter("redSocial")));servicio.guardar(a);
            }
            case "/locales"->{
                if("eliminar".equals(accion)){servicio.eliminarLocal(entero(req,"id",0));return;}
                LocalConcierto l=new LocalConcierto(entero(req,"id",0),requerido(req,"nombreLocal"),requerido(req,"direccion"),entero(req,"capacidadMax",0),requerido(req,"ciudad"));servicio.guardar(l);
            }
            case "/zonas"->{
                int eventoId=entero(req,"eventoId",0);
                if("eliminar".equals(accion)){servicio.eliminarZona(entero(req,"id",0));return;}
                CategoriaAsiento z=new CategoriaAsiento(entero(req,"id",0),eventoId,requerido(req,"nombreZona"),new BigDecimal(requerido(req,"precioBase")),entero(req,"aforoTotal",0),0);servicio.guardar(z);
            }
            default->throw new IllegalArgumentException("Sección administrativa no válida.");
        }
    }
    private void render(HttpServletRequest req,HttpServletResponse resp,String ruta)throws SQLException,ServletException,IOException{
        String jsp;
        if("/".equals(ruta)){
            req.setAttribute("eventos",servicio.eventos());req.setAttribute("locales",servicio.locales());req.setAttribute("organizadores",servicio.organizadores());req.setAttribute("artistas",servicio.artistas());
            String editar=req.getParameter("editar");if(editar!=null){
                EventoCultural evento=buscarEvento(Integer.parseInt(editar));req.setAttribute("eventoEditar",evento);
                req.setAttribute("fechaEdicion",evento.getFecha().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
            }
            jsp="/WEB-INF/views/admin/eventos.jsp";
        }else if("/zonas".equals(ruta)){
            int eventId=entero(req,"eventoId",0);req.setAttribute("evento",buscarEvento(eventId));req.setAttribute("zonas",servicio.zonas(eventId));
            String editar=req.getParameter("editar");if(editar!=null){int id=Integer.parseInt(editar);req.setAttribute("zonaEditar",servicio.zonas(eventId).stream().filter(z->z.getId()==id).findFirst().orElseThrow(()->new IllegalArgumentException("Zona no encontrada.")));}
            jsp="/WEB-INF/views/admin/zonas.jsp";
        }else{
            switch(ruta){
                case "/organizadores"->{var lista=servicio.organizadores();req.setAttribute("seccion","organizadores");req.setAttribute("titulo","Organizadores");req.setAttribute("elementos",lista);cargarEdicion(req,lista);}
                case "/artistas"->{var lista=servicio.artistas();req.setAttribute("seccion","artistas");req.setAttribute("titulo","Artistas");req.setAttribute("elementos",lista);cargarEdicion(req,lista);}
                case "/locales"->{var lista=servicio.locales();req.setAttribute("seccion","locales");req.setAttribute("titulo","Locales");req.setAttribute("elementos",lista);cargarEdicion(req,lista);}
                default->throw new IllegalArgumentException("Sección administrativa no válida.");
            }
            jsp="/WEB-INF/views/admin/catalogos.jsp";
        }
        req.getRequestDispatcher(jsp).forward(req,resp);
    }
    private EventoCultural buscarEvento(int id)throws SQLException{
        return servicio.eventos().stream().filter(e->e.getId()==id).findFirst().orElseThrow(()->new IllegalArgumentException("Evento no encontrado."));
    }
    private static void cargarEdicion(HttpServletRequest req,java.util.List<?> elementos){
        String editar=req.getParameter("editar");if(editar==null)return;
        int id;try{id=Integer.parseInt(editar);}catch(NumberFormatException e){throw new IllegalArgumentException("Id de registro inválido.");}
        Object elemento=elementos.stream().filter(item->item instanceof OrganizadorEvento o&&o.getId()==id
                ||item instanceof Artista a&&a.getId()==id||item instanceof LocalConcierto l&&l.getId()==id).findFirst()
                .orElseThrow(()->new IllegalArgumentException("Registro no encontrado."));
        req.setAttribute("elemento",elemento);
    }
    private static String ruta(HttpServletRequest req){String p=req.getPathInfo();return p==null||p.isBlank()?"/":p;}
    private static String url(String ruta,HttpServletRequest req){if("/zonas".equals(ruta))return ruta+"?eventoId="+req.getParameter("eventoId");return "/".equals(ruta)?"/admin/eventos":"/admin/eventos"+ruta;}
    private static int entero(HttpServletRequest req,String campo,int minimo){String valor=req.getParameter(campo);if(valor==null||valor.isBlank())return minimo;try{return Integer.parseInt(valor);}catch(NumberFormatException e){throw new IllegalArgumentException("El campo "+campo+" debe ser un número entero.");}}
    private static String requerido(HttpServletRequest req,String campo){String valor=req.getParameter(campo);if(valor==null||valor.isBlank())throw new IllegalArgumentException("Completa el campo "+campo+".");return valor.trim();}
    private static String vacioANull(String valor){return valor==null||valor.isBlank()?null:valor.trim();}
}
