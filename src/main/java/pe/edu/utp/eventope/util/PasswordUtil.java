package pe.edu.utp.eventope.util;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.*;
import java.util.Base64;
/** PBKDF2-HMAC-SHA256 con sal independiente; nunca SHA-256 simple para contraseñas. */
public final class PasswordUtil {
    private static final int ITERACIONES=600_000;
    private PasswordUtil(){}
    public static String crear(char[] clave){
        if(clave==null||clave.length<12||clave.length>128)throw new IllegalArgumentException("Use entre 12 y 128 caracteres");
        byte[] sal=new byte[16];new SecureRandom().nextBytes(sal);
        return "pbkdf2-sha256$"+ITERACIONES+"$"+Base64.getEncoder().encodeToString(sal)+"$"+Base64.getEncoder().encodeToString(derivar(clave,sal,ITERACIONES));
    }
    public static boolean verificar(char[] clave,String hash){
        if(clave==null||clave.length>128||hash==null||hash.length()>255)return false;
        try{
            String[] p=hash.split("[$]",-1);if(p.length!=4||!p[0].equals("pbkdf2-sha256"))return false;
            int n=Integer.parseInt(p[1]);if(n<600_000||n>1_200_000)return false;
            byte[] sal=Base64.getDecoder().decode(p[2]),esperado=Base64.getDecoder().decode(p[3]);
            return sal.length==16&&esperado.length==32&&MessageDigest.isEqual(esperado,derivar(clave,sal,n));
        }catch(IllegalArgumentException e){return false;}
    }
    private static byte[] derivar(char[] clave,byte[] sal,int n){
        PBEKeySpec spec=new PBEKeySpec(clave,sal,n,256);
        try{return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();}
        catch(GeneralSecurityException e){throw new IllegalStateException(e);}finally{spec.clearPassword();}
    }
}
