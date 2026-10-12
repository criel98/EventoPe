package pe.edu.utp.eventope;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import pe.edu.utp.eventope.config.ConexionBD;
import pe.edu.utp.eventope.config.Configuracion;
import pe.edu.utp.eventope.dto.*;
import pe.edu.utp.eventope.service.ServicioCompra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba de integración optativa. Ejecutar únicamente contra una BD de pruebas desechable:
 * -Deventope.config=/ruta/privada/eventope-test.properties
 * -Deventope.test.clienteId=... -Deventope.test.eventoId=... -Deventope.test.categoriaId=...
 * La prueba crea una compra real de dos entradas y reduce el aforo en dos.
 */
class CompraMySqlTest {
    @Test
    void compraEsAtomicaYReintentoNoDuplica() throws Exception {
        String clienteP=System.getProperty("eventope.test.clienteId");
        String eventoP=System.getProperty("eventope.test.eventoId");
        String categoriaP=System.getProperty("eventope.test.categoriaId");
        Assumptions.assumeTrue(clienteP!=null&&eventoP!=null&&categoriaP!=null,
                "Configura los IDs de una BD de pruebas desechable para ejecutar la compra MySQL");
        int cliente=Integer.parseInt(clienteP),evento=Integer.parseInt(eventoP),categoria=Integer.parseInt(categoriaP);
        var conexion=new ConexionBD(Configuracion.cargar());
        int aforoAntes;
        try(Connection c=conexion.abrir();PreparedStatement p=c.prepareStatement("SELECT aforo_disponible FROM categoria_asiento WHERE id=? AND evento_id=? AND aforo_disponible>=2")){
            p.setInt(1,categoria);p.setInt(2,evento);try(ResultSet r=p.executeQuery()){Assumptions.assumeTrue(r.next(),"La categoría debe pertenecer al evento y tener dos cupos disponibles");aforoAntes=r.getInt(1);}
        }
        CarritoCompra carrito=new CarritoCompra(evento,"Prueba de integración",List.of(new LineaCarrito(categoria,"Zona de prueba",2,"0.00")));
        carrito.setNominaciones(List.of(new NominacionAsistente("12345670","Prueba Uno","Integración"),new NominacionAsistente("12345671","Prueba Dos","Integración")));
        ServicioCompra servicio=new ServicioCompra(conexion);
        CompraConfirmada primera=servicio.confirmar(cliente,carrito);
        CompraConfirmada reintento=servicio.confirmar(cliente,carrito);
        assertEquals(primera.getId(),reintento.getId(),"El reintento debe recuperar la misma compra");
        try(Connection c=conexion.abrir();PreparedStatement p=c.prepareStatement("SELECT (SELECT COUNT(*) FROM detalle_transaccion WHERE transaccion_id=?), (SELECT COUNT(*) FROM boleto_nominado b JOIN detalle_transaccion d ON d.entrada_id=b.entrada_id WHERE d.transaccion_id=?), (SELECT aforo_disponible FROM categoria_asiento WHERE id=?)")){
            p.setInt(1,primera.getId());p.setInt(2,primera.getId());p.setInt(3,categoria);try(ResultSet r=p.executeQuery()){assertTrue(r.next());assertEquals(2,r.getInt(1));assertEquals(2,r.getInt(2));assertEquals(aforoAntes-2,r.getInt(3));}
        }
    }
}
