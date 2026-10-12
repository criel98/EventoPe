package pe.edu.utp.eventope.util;
import jakarta.servlet.http.HttpSession;
import java.util.Base64;
public final class Csrf {
    private Csrf(){}
    public static boolean valido(HttpSession sesion,String enviado){if(sesion==null||enviado==null)return false;Object guardado=sesion.getAttribute("csrf");return guardado instanceof String s&&java.security.MessageDigest.isEqual(s.getBytes(java.nio.charset.StandardCharsets.UTF_8),enviado.getBytes(java.nio.charset.StandardCharsets.UTF_8));}
    public static String token(HttpSession sesion){
        synchronized(sesion){Object v=sesion.getAttribute("csrf");if(v instanceof String s)return s;String s=Base64.getUrlEncoder().withoutPadding().encodeToString(Totp.nuevaClave());sesion.setAttribute("csrf",s);return s;}
    }
}
