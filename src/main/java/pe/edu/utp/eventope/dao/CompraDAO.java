package pe.edu.utp.eventope.dao;

import pe.edu.utp.eventope.dto.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/** Persistencia de compra. No abre, confirma ni cierra la conexión recibida. */
public final class CompraDAO {
    public Optional<CompraConfirmada> buscarPorClave(Connection c, String clave) throws SQLException {
        String sql="SELECT t.id,t.codigo_unico,t.total,t.evento_id,e.nombre,cp.serie_correlativo,(SELECT COUNT(*) FROM entrada_digital en WHERE en.comprador_id=t.cliente_id AND en.evento_id=t.evento_id AND en.fecha_emision>=t.fecha_hora AND en.estado_uso<>'ANULADA') cantidad FROM transaccion_compra t JOIN evento_cultural e ON e.id=t.evento_id LEFT JOIN comprobante_pago cp ON cp.transaccion_id=t.id WHERE t.codigo_unico=?";
        try(PreparedStatement p=c.prepareStatement(sql)){p.setString(1,clave);try(ResultSet r=p.executeQuery()){if(r.next())return Optional.of(new CompraConfirmada(r.getInt("id"),r.getString("codigo_unico"),r.getString("serie_correlativo"),r.getString("nombre"),r.getBigDecimal("total"),r.getInt("cantidad")));}}return Optional.empty();
    }
    public CompraConfirmada guardar(Connection c,int clienteId,CarritoCompra carrito,String hash) throws SQLException {
        Optional<CompraConfirmada> previa=buscarPorClave(c,carrito.getClaveConfirmacion());
        if(previa.isPresent()){
            try(PreparedStatement p=c.prepareStatement("SELECT cliente_id,solicitud_hash FROM transaccion_compra WHERE id=?")){p.setInt(1,previa.get().getId());try(ResultSet r=p.executeQuery()){if(!r.next()||r.getInt(1)!=clienteId||!hash.equals(r.getString(2)))throw new SQLException("La clave de confirmación ya fue usada con otro pedido.");}}
            return previa.get();
        }
        try(PreparedStatement p=c.prepareStatement("SELECT id FROM usuario_cliente WHERE id=? FOR UPDATE")){p.setInt(1,clienteId);try(ResultSet r=p.executeQuery()){if(!r.next())throw new SQLException("La cuenta de cliente ya no existe.");}}
        // Revisa otra vez después del bloqueo del cliente: dos POST simultáneos de la misma sesión
        // quedan serializados y el segundo recupera la compra ya confirmada.
        previa=buscarPorClave(c,carrito.getClaveConfirmacion());
        if(previa.isPresent()){
            try(PreparedStatement p=c.prepareStatement("SELECT cliente_id,solicitud_hash FROM transaccion_compra WHERE id=?")){p.setInt(1,previa.get().getId());try(ResultSet r=p.executeQuery()){if(!r.next()||r.getInt(1)!=clienteId||!hash.equals(r.getString(2)))throw new SQLException("La clave de confirmación ya fue usada con otro pedido.");}}
            return previa.get();
        }
        String nombreEvento;String estado;
        try(PreparedStatement p=c.prepareStatement("SELECT nombre,estado FROM evento_cultural WHERE id=? FOR UPDATE")){p.setInt(1,carrito.getEventoId());try(ResultSet r=p.executeQuery()){if(!r.next())throw new SQLException("Evento no encontrado.");nombreEvento=r.getString("nombre");estado=r.getString("estado");}}
        if(!"PUBLICADO".equals(estado))throw new SQLException("El evento ya no está disponible para la compra.");
        int compradas=0;try(PreparedStatement p=c.prepareStatement("SELECT id FROM entrada_digital WHERE comprador_id=? AND evento_id=? AND estado_uso<>'ANULADA' FOR UPDATE")){p.setInt(1,clienteId);p.setInt(2,carrito.getEventoId());try(ResultSet r=p.executeQuery()){while(r.next())compradas++;}}
        if(compradas+carrito.getCantidadTotal()>4)throw new SQLException("Solo puedes tener cuatro entradas no anuladas por evento.");
        Map<Integer,PrecioZona> zonas=new LinkedHashMap<>();List<Integer> ids=new ArrayList<>();for(LineaCarrito l:carrito.getLineas())ids.add(l.getCategoriaId());Collections.sort(ids);
        for(int id:ids){try(PreparedStatement p=c.prepareStatement("SELECT id,evento_id,nombre_zona,precio_base,aforo_disponible FROM categoria_asiento WHERE id=? FOR UPDATE")){p.setInt(1,id);try(ResultSet r=p.executeQuery()){if(!r.next())throw new SQLException("Categoría no encontrada.");if(r.getInt("evento_id")!=carrito.getEventoId())throw new SQLException("La zona no pertenece al evento seleccionado.");zonas.put(id,new PrecioZona(r.getString("nombre_zona"),r.getBigDecimal("precio_base"),r.getInt("aforo_disponible")));}}}
        BigDecimal subtotal=BigDecimal.ZERO;for(LineaCarrito l:carrito.getLineas()){PrecioZona z=zonas.get(l.getCategoriaId());if(z.disponible<l.getCantidad())throw new SQLException("No hay suficientes entradas disponibles en la zona "+z.nombre+".");subtotal=subtotal.add(z.precio.multiply(BigDecimal.valueOf(l.getCantidad())));}
        String sql="INSERT INTO transaccion_compra(codigo_unico,solicitud_hash,cliente_id,evento_id,cupon_id,subtotal,descuento,igv,total,estado) VALUES(?,?,?,?,NULL,?,0,0,?,'CONFIRMADA')";int compraId;
        try(PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){p.setString(1,carrito.getClaveConfirmacion());p.setString(2,hash);p.setInt(3,clienteId);p.setInt(4,carrito.getEventoId());p.setBigDecimal(5,subtotal);p.setBigDecimal(6,subtotal);p.executeUpdate();try(ResultSet k=p.getGeneratedKeys()){if(!k.next())throw new SQLException("No se pudo crear la compra.");compraId=k.getInt(1);}}
        int nom=0;
        for(LineaCarrito l:carrito.getLineas())for(int i=0;i<l.getCantidad();i++){
            NominacionAsistente a=carrito.getNominaciones().get(nom++);String codigo=UUID.randomUUID().toString();int entradaId;
            try(PreparedStatement p=c.prepareStatement("INSERT INTO entrada_digital(codigo_entrada,evento_id,categoria_id,comprador_id,estado_uso) VALUES(?,?,?,?,'DISPONIBLE')",Statement.RETURN_GENERATED_KEYS)){p.setString(1,codigo);p.setInt(2,carrito.getEventoId());p.setInt(3,l.getCategoriaId());p.setInt(4,clienteId);p.executeUpdate();try(ResultSet k=p.getGeneratedKeys()){if(!k.next())throw new SQLException("No se pudo emitir la entrada.");entradaId=k.getInt(1);}}
            try(PreparedStatement p=c.prepareStatement("INSERT INTO detalle_transaccion(transaccion_id,entrada_id,precio_aplicado,descuento_aplicado) VALUES(?,?,?,0)")){p.setInt(1,compraId);p.setInt(2,entradaId);p.setBigDecimal(3,zonas.get(l.getCategoriaId()).precio);p.executeUpdate();}
            try(PreparedStatement p=c.prepareStatement("INSERT INTO boleto_nominado(entrada_id,dni_asistente,nombres_asistente,apellidos_asistente) VALUES(?,?,?,?)")){p.setInt(1,entradaId);p.setString(2,a.getDni());p.setString(3,a.getNombres());p.setString(4,a.getApellidos());p.executeUpdate();}
        }
        for(LineaCarrito l:carrito.getLineas())try(PreparedStatement p=c.prepareStatement("UPDATE categoria_asiento SET aforo_disponible=aforo_disponible-? WHERE id=? AND evento_id=? AND aforo_disponible>=?")){p.setInt(1,l.getCantidad());p.setInt(2,l.getCategoriaId());p.setInt(3,carrito.getEventoId());p.setInt(4,l.getCantidad());if(p.executeUpdate()!=1)throw new SQLException("El aforo cambió durante la compra; inténtalo nuevamente.");}
        String operacion="SIM-"+UUID.randomUUID().toString().replace("-","").substring(0,20).toUpperCase(Locale.ROOT);
        try(PreparedStatement p=c.prepareStatement("INSERT INTO metodo_pago(transaccion_id,nombre_metodo,nro_operacion,estado_transaccion) VALUES(?,'PAGO SIMULADO',?,'APROBADO')")){p.setInt(1,compraId);p.setString(2,operacion);p.executeUpdate();}
        String serie="AC-"+UUID.randomUUID().toString().substring(0,8).toUpperCase(Locale.ROOT);
        try(PreparedStatement p=c.prepareStatement("INSERT INTO comprobante_pago(transaccion_id,serie_correlativo,tipo_comprobante) VALUES(?,?,'ACADEMICO')")){p.setInt(1,compraId);p.setString(2,serie);p.executeUpdate();}
        return new CompraConfirmada(compraId,carrito.getClaveConfirmacion(),serie,nombreEvento,subtotal,carrito.getCantidadTotal());
    }
    private static final class PrecioZona {final String nombre;final BigDecimal precio;final int disponible;PrecioZona(String n,BigDecimal p,int d){nombre=n;precio=p;disponible=d;}}
}
