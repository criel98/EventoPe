package pe.edu.utp.eventope;
import org.junit.jupiter.api.Test;
import pe.edu.utp.eventope.util.*;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;
class CifradoTest {
 @Test void cifraYVinculaEntrada(){var c=new CifradoSecretos(Base64.getEncoder().encodeToString(Totp.nuevaClave()));byte[] s=Totp.nuevaClave();String a=c.cifrar(s,"entrada1"),b=c.cifrar(s,"entrada1");assertNotEquals(a,b);assertArrayEquals(s,c.descifrar(a,"entrada1"));assertThrows(IllegalStateException.class,()->c.descifrar(a,"entrada2"));}
 @Test void claveIncorrectaYTamppering(){var a=new CifradoSecretos(Base64.getEncoder().encodeToString(Totp.nuevaClave()));var b=new CifradoSecretos(Base64.getEncoder().encodeToString(Totp.nuevaClave()));String valor=a.cifrar(Totp.nuevaClave(),"e");assertThrows(IllegalStateException.class,()->b.descifrar(valor,"e"));byte[] bytes=Base64.getDecoder().decode(valor.substring(3));bytes[20]^=1;assertThrows(IllegalStateException.class,()->a.descifrar("v1:"+Base64.getEncoder().encodeToString(bytes),"e"));}
}
