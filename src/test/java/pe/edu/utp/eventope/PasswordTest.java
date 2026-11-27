package pe.edu.utp.eventope;
import org.junit.jupiter.api.Test;
import pe.edu.utp.eventope.util.PasswordUtil;
import static org.junit.jupiter.api.Assertions.*;
class PasswordTest {
 @Test void salYVerificacion(){char[] p="Prueba-solamente-2026!".toCharArray();String a=PasswordUtil.crear(p),b=PasswordUtil.crear(p);assertNotEquals(a,b);assertTrue(PasswordUtil.verificar(p,a));assertFalse(PasswordUtil.verificar("incorrecta".toCharArray(),a));assertFalse(PasswordUtil.verificar(p,"hash inválido"));}
 @Test void validaLongitud(){assertThrows(IllegalArgumentException.class,()->PasswordUtil.crear("corta".toCharArray()));}
}
