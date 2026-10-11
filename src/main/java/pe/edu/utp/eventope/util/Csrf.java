package pe.edu.utp.eventope.util;
import jakarta.servlet.http.HttpSession;
import java.util.Base64;
public final class Csrf {
    private Csrf(){}
    public static String token(HttpSession sesion){
        synchronized(sesion){Object v=sesion.getAttribute("csrf");if(v instanceof String s)return s;String s=Base64.getUrlEncoder().withoutPadding().encodeToString(Totp.nuevaClave());sesion.setAttribute("csrf",s);return s;}
    }
}
