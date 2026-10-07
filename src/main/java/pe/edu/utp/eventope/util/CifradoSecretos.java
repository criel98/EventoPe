package pe.edu.utp.eventope.util;
import javax.crypto.Cipher;
import javax.crypto.spec.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
/** AES-256-GCM; vincula cada secreto al código de su entrada mediante AAD. Clave maestra externa. */
public final class CifradoSecretos {
    private final SecretKeySpec clave;
    public CifradoSecretos(String base64){byte[] b=Base64.getDecoder().decode(base64);if(b.length!=32)throw new IllegalArgumentException("Clave maestra: 32 bytes");clave=new SecretKeySpec(b,"AES");Arrays.fill(b,(byte)0);}
    public String cifrar(byte[] secreto,String codigoEntrada){
        byte[] iv=new byte[12];new SecureRandom().nextBytes(iv);
        try{Cipher c=preparar(Cipher.ENCRYPT_MODE,iv,codigoEntrada);byte[] datos=c.doFinal(secreto);return "v1:"+Base64.getEncoder().encodeToString(ByteBuffer.allocate(iv.length+datos.length).put(iv).put(datos).array());}
        catch(GeneralSecurityException e){throw new IllegalStateException("No se pudo cifrar el secreto",e);}
    }
    public byte[] descifrar(String valor,String codigoEntrada){
        try{
            if(valor==null||!valor.startsWith("v1:"))throw new IllegalArgumentException("Formato cifrado inválido");
            byte[] b=Base64.getDecoder().decode(valor.substring(3));if(b.length<29)throw new IllegalArgumentException("Cifrado incompleto");
            return preparar(Cipher.DECRYPT_MODE,Arrays.copyOfRange(b,0,12),codigoEntrada).doFinal(Arrays.copyOfRange(b,12,b.length));
        }catch(GeneralSecurityException e){throw new IllegalStateException("Secreto inválido o clave maestra incorrecta",e);}
    }
    private Cipher preparar(int modo,byte[] iv,String codigo) throws GeneralSecurityException {
        Cipher c=Cipher.getInstance("AES/GCM/NoPadding");c.init(modo,clave,new GCMParameterSpec(128,iv));c.updateAAD(codigo.getBytes(StandardCharsets.UTF_8));return c;
    }
}
