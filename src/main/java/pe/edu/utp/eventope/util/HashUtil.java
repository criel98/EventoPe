package pe.edu.utp.eventope.util;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.HexFormat;
public final class HashUtil {
    private HashUtil(){}
    public static String sha256(String texto) {
        try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(texto.getBytes(StandardCharsets.UTF_8)));}
        catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}
    }
    public static boolean iguales(String a,String b){return a!=null&&b!=null&&MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8),b.getBytes(StandardCharsets.UTF_8));}
}
