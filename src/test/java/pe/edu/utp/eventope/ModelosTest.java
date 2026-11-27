package pe.edu.utp.eventope;
import org.junit.jupiter.api.Test;
import pe.edu.utp.eventope.model.*;
import pe.edu.utp.eventope.dto.SesionUsuario;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
class ModelosTest {
 @Test void entradaTerminal(){var e=new EntradaDigital();e.setEstadoUso("DISPONIBLE");e.marcarUtilizada();assertThrows(IllegalStateException.class,e::marcarUtilizada);assertThrows(IllegalStateException.class,e::anular);}
 @Test void neto(){var d=new DetalleTransaccion();d.setPrecioAplicado(new BigDecimal("100.00"));d.setDescuentoAplicado(new BigDecimal("10.00"));assertEquals(new BigDecimal("90.00"),d.obtenerImporteNeto());d.setDescuentoAplicado(new BigDecimal("101"));assertThrows(IllegalStateException.class,d::obtenerImporteNeto);}
 @Test void identidadDeSesion(){assertThrows(IllegalArgumentException.class,()->new SesionUsuario(1,SesionUsuario.Tipo.CLIENTE,SesionUsuario.Rol.ADMIN,"a"));var c=new SesionUsuario(1,SesionUsuario.Tipo.CLIENTE,SesionUsuario.Rol.CLIENTE,"a");assertThrows(SecurityException.class,c::exigirValidacion);}
}
