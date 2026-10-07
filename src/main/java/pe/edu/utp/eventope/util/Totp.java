package pe.edu.utp.eventope.util;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.*;
import java.time.Instant;
import java.util.Locale;
/** RFC 6238. EventoPe usa HMAC-SHA256, 8 dígitos, ventanas UTC de 30 segundos. */
public final class Totp {
    public static final int PASO_SEGUNDOS=30;
    private Totp(){}
    public static byte[] nuevaClave(){byte[] b=new byte[32];new SecureRandom().nextBytes(b);return b;}
    public static long intervalo(Instant ahora){if(ahora.getEpochSecond()<0)throw new IllegalArgumentException("Tiempo anterior a epoch");return ahora.getEpochSecond()/PASO_SEGUNDOS;}
    public static Instant expiracion(Instant ahora){return Instant.ofEpochSecond(Math.multiplyExact(intervalo(ahora)+1,PASO_SEGUNDOS));}
    public static String generar(byte[] secreto,Instant ahora){return generar(secreto,ahora,"HmacSHA256");}
    public static String generar(byte[] secreto,Instant ahora,String algoritmo){
        if(secreto==null||secreto.length<20)throw new IllegalArgumentException("Clave demasiado corta");
        if(!java.util.Set.of("HmacSHA1","HmacSHA256","HmacSHA512").contains(algoritmo))throw new IllegalArgumentException("Algoritmo no permitido");
        try{
            Mac mac=Mac.getInstance(algoritmo);mac.init(new SecretKeySpec(secreto,algoritmo));
            byte[] h=mac.doFinal(ByteBuffer.allocate(8).putLong(intervalo(ahora)).array());int o=h[h.length-1]&15;
            int numero=((h[o]&127)<<24)|((h[o+1]&255)<<16)|((h[o+2]&255)<<8)|(h[o+3]&255);
            return String.format(Locale.ROOT,"%08d",numero%100_000_000);
        }catch(GeneralSecurityException e){throw new IllegalStateException(e);}
    }
    public static boolean verificar(byte[] clave,String codigo,Instant ahora){
        return codigo!=null&&codigo.matches("[0-9]{8}")&&HashUtil.iguales(generar(clave,ahora),codigo);
    }
}
