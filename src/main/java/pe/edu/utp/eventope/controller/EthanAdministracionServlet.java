package pe.edu.utp.eventope.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import pe.edu.utp.eventope.config.ConexionBD;
import pe.edu.utp.eventope.config.Configuracion;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.service.ReglasCupon;
import pe.edu.utp.eventope.util.Csrf;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.DateTimeException;
import java.time.LocalDateTime;

@WebServlet("/admin/ethan")
public final class EthanAdministracionServlet extends HttpServlet {
    private ConexionBD db;
    @Override public void init() throws ServletException {
        try { db=new ConexionBD(Configuracion.cargar()); }
        catch(RuntimeException e){ throw new ServletException("No se pudo configurar administración Ethan",e); }
    }

    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws IOException,ServletException {
        if(!exigirAdmin(req,resp)) return;
        try(Connection c=db.abrir()) {
            try(Statement s=c.createStatement();ResultSet r=s.executeQuery(
                    "SELECT id,codigo_cupon,porcentaje_descuento,fecha_expiracion,estado FROM cupon_descuento ORDER BY id")) {
                req.setAttribute("cupones",pe.edu.utp.eventope.dao.ResultadoConsulta.cupones(r));
            }
            try(Statement s=c.createStatement();ResultSet r=s.executeQuery(
                    "SELECT p.evento_id,e.nombre,p.dias_minimos_previos,p.penalizacion_porcentaje,p.estado_activo "
                    +"FROM politica_devolucion p JOIN evento_cultural e ON e.id=p.evento_id ORDER BY p.evento_id")) {
                req.setAttribute("politicas",pe.edu.utp.eventope.dao.ResultadoConsulta.politicas(r));
            }
        } catch(SQLException e){ throw new ServletException("No se pudo consultar la administración",e); }
        req.setAttribute("csrf",Csrf.token(req.getSession()));
        req.getRequestDispatcher("/WEB-INF/views/admin/ethan.jsp").forward(req,resp);
    }

    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws IOException,ServletException {
        if(!exigirAdmin(req,resp)) return;
        if(!Csrf.valido(req.getSession(false),req.getParameter("csrf"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,"Token de seguridad inválido");
            return;
        }
        String accion=req.getParameter("accion");
        try(Connection c=db.abrir()) {
            c.setAutoCommit(false);
            try {
                if("cupon".equals(accion)) guardarCupon(c,req);
                else if("politica".equals(accion)) guardarPolitica(c,req);
                else throw new IllegalArgumentException("Acción no válida.");
                c.commit();
                req.getSession().setAttribute("mensajeEthan","Configuración guardada correctamente.");
                resp.sendRedirect(req.getContextPath()+"/admin/ethan");
            } catch(SQLException|IllegalArgumentException e) {
                try {
                    c.rollback();
                } catch(SQLException rollback) {
                    e.addSuppressed(rollback);
                    throw new ServletException("No se pudo revertir la configuración parcial",e);
                }
                if(e instanceof SQLException) {
                    getServletContext().log("No se pudo guardar la configuración de Ethan",e);
                    req.setAttribute("error","No se pudo guardar. Comprueba los datos y que el evento exista.");
                } else {
                    req.setAttribute("error",e.getMessage());
                }
                doGet(req,resp);
            }
        } catch(SQLException e){ throw new ServletException("No se pudo guardar la configuración",e); }
    }

    private static void guardarCupon(Connection c,HttpServletRequest req) throws SQLException {
        String codigo=ReglasCupon.normalizarCodigo(requerido(req,"codigo"));
        BigDecimal porcentaje=new BigDecimal(requerido(req,"porcentaje"));
        Timestamp expira;
        try {
            expira=Timestamp.valueOf(LocalDateTime.parse(requerido(req,"expiracion")));
        } catch(DateTimeException e) {
            throw new IllegalArgumentException("La fecha de vencimiento no es válida.",e);
        }
        if(porcentaje.signum()<0||porcentaje.compareTo(new BigDecimal("100"))>0)
            throw new IllegalArgumentException("Datos de cupón inválidos.");
        try(PreparedStatement p=c.prepareStatement(
                "INSERT INTO cupon_descuento(codigo_cupon,porcentaje_descuento,fecha_expiracion,estado) "
                +"VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE porcentaje_descuento=VALUES(porcentaje_descuento),"
                +"fecha_expiracion=VALUES(fecha_expiracion),estado=VALUES(estado)")) {
            p.setString(1,codigo); p.setBigDecimal(2,porcentaje); p.setTimestamp(3,expira);
            p.setString(4,"on".equals(req.getParameter("activo"))?"ACTIVO":"INACTIVO");
            p.executeUpdate();
        }
    }

    private static void guardarPolitica(Connection c,HttpServletRequest req) throws SQLException {
        int evento=Integer.parseInt(requerido(req,"evento"));
        int dias=Integer.parseInt(requerido(req,"dias"));
        BigDecimal penalizacion=new BigDecimal(requerido(req,"penalizacion"));
        if(dias<0||penalizacion.signum()<0||penalizacion.compareTo(new BigDecimal("100"))>0)
            throw new IllegalArgumentException("Datos de política inválidos.");
        try(PreparedStatement p=c.prepareStatement(
                "INSERT INTO politica_devolucion(evento_id,dias_minimos_previos,penalizacion_porcentaje,estado_activo) "
                +"VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE dias_minimos_previos=VALUES(dias_minimos_previos),"
                +"penalizacion_porcentaje=VALUES(penalizacion_porcentaje),estado_activo=VALUES(estado_activo)")) {
            p.setInt(1,evento); p.setInt(2,dias); p.setBigDecimal(3,penalizacion);
            p.setBoolean(4,"on".equals(req.getParameter("activa")));
            p.executeUpdate();
        }
    }

    private static String requerido(HttpServletRequest req,String nombre) {
        String valor=req.getParameter(nombre);
        if(valor==null||valor.isBlank()) throw new IllegalArgumentException("Completa todos los campos requeridos.");
        return valor.trim();
    }

    private static boolean exigirAdmin(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        Object o=req.getSession().getAttribute("sesionUsuario");
        if(!(o instanceof SesionUsuario a)||a.getTipo()!=SesionUsuario.Tipo.PERSONAL||a.getRol()!=SesionUsuario.Rol.ADMIN) {
            resp.sendRedirect(req.getContextPath()+"/admin/ethan/login"); return false;
        }
        return true;
    }
}
