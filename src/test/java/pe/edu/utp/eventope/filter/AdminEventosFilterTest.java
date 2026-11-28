package pe.edu.utp.eventope.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import pe.edu.utp.eventope.dto.SesionUsuario;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdminEventosFilterTest {
    @Test void redirigeCuandoNoHaySesionActiva(){
        Resultado resultado=ejecutar(null);
        assertEquals("/login",resultado.redirect());
        assertEquals(0,resultado.continuaciones());
    }

    @Test void devuelve403AClienteYGuardia(){
        assertEquals(HttpServletResponse.SC_FORBIDDEN,ejecutar(new SesionUsuario(1,SesionUsuario.Tipo.CLIENTE,SesionUsuario.Rol.CLIENTE,"Cliente")).status());
        assertEquals(HttpServletResponse.SC_FORBIDDEN,ejecutar(new SesionUsuario(2,SesionUsuario.Tipo.PERSONAL,SesionUsuario.Rol.VALIDACION,"Guardia")).status());
    }

    @Test void permiteSoloAdministrador(){
        Resultado resultado=ejecutar(new SesionUsuario(3,SesionUsuario.Tipo.PERSONAL,SesionUsuario.Rol.ADMIN,"Admin"));
        assertEquals(1,resultado.continuaciones());
        assertEquals(0,resultado.status());
    }

    private Resultado ejecutar(SesionUsuario actor){
        AtomicInteger estado=new AtomicInteger();AtomicReference<String> redirect=new AtomicReference<>();AtomicInteger continuaciones=new AtomicInteger();
        HttpSession session=actor==null?null:proxy(HttpSession.class,(method,args)->"getAttribute".equals(method.getName())?actor:null);
        HttpServletRequest req=proxy(HttpServletRequest.class,(method,args)->switch(method.getName()){case "getSession"->session;case "getContextPath"->"";default->null;});
        HttpServletResponse resp=proxy(HttpServletResponse.class,(method,args)->{if("sendError".equals(method.getName()))estado.set((int)args[0]);if("sendRedirect".equals(method.getName()))redirect.set((String)args[0]);return null;});
        FilterChain chain=proxy(FilterChain.class,(method,args)->{continuaciones.incrementAndGet();return null;});
        try{new AdminEventosFilter().doFilter(req,resp,chain);}
        catch(Exception e){throw new AssertionError("El filtro no pudo verificar la sesión.",e);}
        return new Resultado(estado.get(),redirect.get(),continuaciones.get());
    }
    @SuppressWarnings("unchecked") private static <T>T proxy(Class<T> type,Handler handler){
        return (T)Proxy.newProxyInstance(type.getClassLoader(),new Class<?>[]{type},(proxy,method,args)->handler.invoke(method,args==null?new Object[0]:args));
    }
    @FunctionalInterface private interface Handler{Object invoke(java.lang.reflect.Method method,Object[] args)throws Throwable;}
    private record Resultado(int status,String redirect,int continuaciones){}
}
