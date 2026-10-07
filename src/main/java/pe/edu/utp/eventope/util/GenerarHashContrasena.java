package pe.edu.utp.eventope.util;
import java.util.Arrays;
public final class GenerarHashContrasena {
    public static void main(String[] args){
        var consola=System.console();if(consola==null)throw new IllegalStateException("Ejecutar en terminal interactiva; no pasar contraseña en argumentos");
        char[] valor=consola.readPassword("Contraseña de prueba (12–128 caracteres): ");
        try{System.out.println(PasswordUtil.crear(valor));}finally{if(valor!=null)Arrays.fill(valor,'\0');}
    }
}
