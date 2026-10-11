package pe.edu.utp.eventope.util;
import java.util.Base64;
/** Ejecutar localmente; guardar resultado en configuración privada, nunca en Git. */
public final class GenerarClaveMaestra {
    public static void main(String[] args){System.out.println(Base64.getEncoder().encodeToString(Totp.nuevaClave()));}
}
