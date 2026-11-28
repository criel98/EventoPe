package pe.edu.utp.eventope.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.model.*;
import pe.edu.utp.eventope.service.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named="EVENTOPE_TEST_DB_URL",matches=".+")
class AdminEventosTest {
    private ProveedorConexion conexiones;
    private ServicioAdminEventos admin;
    private ServicioCatalogo catalogo;
    private ServicioApiEventos api;
    private String sufijo;
    private int organizadorId,artistaId,localId,eventoId,zonaId,clienteId;
    private String nombreEvento;

    @BeforeEach void preparar()throws Exception{
        String databaseUrl=System.getenv("EVENTOPE_TEST_DB_URL");
        Assumptions.assumeTrue(databaseUrl!=null&&!databaseUrl.isBlank(),"Configura EVENTOPE_TEST_DB_URL para ejecutar las pruebas de integración.");
        conexiones=()->DriverManager.getConnection(databaseUrl,System.getenv().getOrDefault("EVENTOPE_TEST_DB_USER","root"),System.getenv().getOrDefault("EVENTOPE_TEST_DB_PASSWORD",""));
        admin=new ServicioAdminEventos(conexiones);catalogo=new ServicioCatalogo(conexiones);api=new ServicioApiEventos(conexiones);
        sufijo=UUID.randomUUID().toString().replace("-","").substring(0,12);nombreEvento="Prueba RF "+sufijo;
        try(Connection c=conexiones.abrir()){
            if(!"eventope_base_test".equals(c.getCatalog()))throw new IllegalStateException("Las pruebas de administración solo se ejecutan en eventope_base_test.");
        }
        OrganizadorEvento organizador=new OrganizadorEvento(0,"2"+String.format("%010d",Math.floorMod(sufijo.hashCode(),1_000_000_000)),"Organizador "+sufijo,"org"+sufijo+"@example.invalid",null);
        admin.guardar(organizador);organizadorId=buscarId("organizador_evento","ruc",organizador.getRuc());
        Artista artista=new Artista(0,"Artista "+sufijo,"Rock","Perú",null);admin.guardar(artista);artistaId=buscarId("artista","nombre_artistico",artista.getNombreArtistico());
        LocalConcierto local=new LocalConcierto(0,"Local "+sufijo,"Dirección de prueba",100,"Lima");admin.guardar(local);localId=buscarId("local_concierto","nombre_local",local.getNombreLocal());
        EventoCultural evento=new EventoCultural(0,nombreEvento,"Prueba de integración","CONCIERTO",LocalDateTime.now().plusDays(30),50,localId,organizadorId,artistaId,"BORRADOR");
        admin.guardar(evento);eventoId=buscarId("evento_cultural","nombre",nombreEvento);
        admin.guardar(new CategoriaAsiento(0,eventoId,"General "+sufijo,new BigDecimal("25.00"),10,7));
        zonaId=buscarId("categoria_asiento","evento_id",Integer.toString(eventoId));
    }

    @AfterEach void limpiar()throws Exception{
        if(conexiones==null)return;
        try(Connection c=conexiones.abrir()){
            if(!"eventope_base_test".equals(c.getCatalog()))return;
            if(eventoId>0){
                try(PreparedStatement p=c.prepareStatement("DELETE FROM transaccion_compra WHERE evento_id=?")){p.setInt(1,eventoId);p.executeUpdate();}
                try(PreparedStatement p=c.prepareStatement("DELETE FROM entrada_digital WHERE evento_id=?")){p.setInt(1,eventoId);p.executeUpdate();}
                try(PreparedStatement p=c.prepareStatement("DELETE FROM categoria_asiento WHERE evento_id=?")){p.setInt(1,eventoId);p.executeUpdate();}
                try(PreparedStatement p=c.prepareStatement("DELETE FROM evento_cultural WHERE id=?")){p.setInt(1,eventoId);p.executeUpdate();}
            }
            if(localId>0)try(PreparedStatement p=c.prepareStatement("DELETE FROM local_concierto WHERE id=?")){p.setInt(1,localId);p.executeUpdate();}
            if(artistaId>0)try(PreparedStatement p=c.prepareStatement("DELETE FROM artista WHERE id=?")){p.setInt(1,artistaId);p.executeUpdate();}
            if(organizadorId>0)try(PreparedStatement p=c.prepareStatement("DELETE FROM organizador_evento WHERE id=?")){p.setInt(1,organizadorId);p.executeUpdate();}
            if(clienteId>0)try(PreparedStatement p=c.prepareStatement("DELETE FROM usuario_cliente WHERE id=?")){p.setInt(1,clienteId);p.executeUpdate();}
        }
    }

    @Test void publicarRequiereAlMenosUnaZonaValida()throws Exception{
        admin.eliminarZona(zonaId);
        assertThrows(IllegalArgumentException.class,()->admin.publicar(eventoId));
        assertEquals("BORRADOR",admin.eventos().stream().filter(e->e.getId()==eventoId).findFirst().orElseThrow().getEstado());
    }

    @Test void creaEditaPublicaYDesactivaEntidadesDeCatalogo()throws Exception{
        OrganizadorEvento org=admin.organizadores().stream().filter(o->o.getId()==organizadorId).findFirst().orElseThrow();org.setRazonSocial("Editado "+sufijo);admin.guardar(org);
        Artista artista=admin.artistas().stream().filter(a->a.getId()==artistaId).findFirst().orElseThrow();artista.setGeneroMusical("Jazz");admin.guardar(artista);
        LocalConcierto local=admin.locales().stream().filter(l->l.getId()==localId).findFirst().orElseThrow();local.setDireccion("Dirección actualizada");admin.guardar(local);
        EventoCultural evento=admin.eventos().stream().filter(e->e.getId()==eventoId).findFirst().orElseThrow();evento.setDescripcion("Descripción editada");admin.guardar(evento);
        assertEquals("Editado "+sufijo,admin.organizadores().stream().filter(o->o.getId()==organizadorId).findFirst().orElseThrow().getRazonSocial());
        assertEquals("Jazz",admin.artistas().stream().filter(a->a.getId()==artistaId).findFirst().orElseThrow().getGeneroMusical());
        assertEquals("Dirección actualizada",admin.locales().stream().filter(l->l.getId()==localId).findFirst().orElseThrow().getDireccion());
        assertEquals("Descripción editada",admin.eventos().stream().filter(e->e.getId()==eventoId).findFirst().orElseThrow().getDescripcion());
        admin.publicar(eventoId);
        assertTrue(catalogo.buscar(nombreEvento,null,null).stream().anyMatch(e->e.getId()==eventoId));
        assertEquals(1,api.listar(null,null,null,nombreEvento).size());
        assertEquals(1,((List<?>)api.detalle(eventoId).orElseThrow().get("zonas")).size());
        admin.desactivar(eventoId);
        assertFalse(catalogo.buscar(nombreEvento,null,null).stream().anyMatch(e->e.getId()==eventoId));
        assertTrue(api.detalle(eventoId).isEmpty());
    }

    @Test void rechazaPrecioAforoReduccionYBorradoConCompra()throws Exception{
        assertThrows(IllegalArgumentException.class,()->admin.guardar(new CategoriaAsiento(0,eventoId,"Precio inválido",new BigDecimal("-0.01"),2,2)));
        assertThrows(IllegalArgumentException.class,()->admin.guardar(new CategoriaAsiento(0,eventoId,"Aforo excedido",BigDecimal.ONE,45,45)));
        try(Connection c=conexiones.abrir();PreparedStatement p=c.prepareStatement("INSERT INTO usuario_cliente(nombres,apellidos,dni,email,contrasena_hash) VALUES(?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS)){
            p.setString(1,"Prueba");p.setString(2,"Admin");p.setString(3,"9"+sufijo.substring(0,7));p.setString(4,"cli"+sufijo+"@example.invalid");p.setString(5,"hash-no-login");p.executeUpdate();try(ResultSet r=p.getGeneratedKeys()){assertTrue(r.next());clienteId=r.getInt(1);}
        }
        try(Connection c=conexiones.abrir();PreparedStatement p=c.prepareStatement("INSERT INTO entrada_digital(codigo_entrada,evento_id,categoria_id,comprador_id) VALUES(?,?,?,?)")){
            for(int i=0;i<3;i++){p.setString(1,"entrada-test-"+sufijo+"-"+i);p.setInt(2,eventoId);p.setInt(3,zonaId);p.setInt(4,clienteId);p.addBatch();}p.executeBatch();
        }
        try(Connection c=conexiones.abrir();PreparedStatement p=c.prepareStatement("UPDATE categoria_asiento SET aforo_disponible=aforo_total WHERE id=?")){
            p.setInt(1,zonaId);p.executeUpdate();
        }
        CategoriaAsiento reducida=new CategoriaAsiento(zonaId,eventoId,"General "+sufijo,new BigDecimal("25.00"),2,0);
        assertThrows(IllegalArgumentException.class,()->admin.guardar(reducida));
        assertThrows(java.sql.SQLException.class,()->admin.eliminarZona(zonaId));
        EventoCultural exceso=new EventoCultural(0,"Aforo local excedido",null,"CONCIERTO",LocalDateTime.now().plusDays(40),101,localId,organizadorId,artistaId,"BORRADOR");
        assertThrows(IllegalArgumentException.class,()->admin.guardar(exceso));

        try(Connection c=conexiones.abrir();PreparedStatement p=c.prepareStatement("INSERT INTO transaccion_compra(codigo_unico,solicitud_hash,cliente_id,evento_id,subtotal,descuento,igv,total) VALUES(?,?,?,?,1,0,0,1)")){
            p.setString(1,"compra-test-"+sufijo);p.setString(2,"a".repeat(64));p.setInt(3,clienteId);p.setInt(4,eventoId);p.executeUpdate();
        }
        assertThrows(java.sql.SQLException.class,()->admin.eliminarEvento(eventoId));
        assertThrows(java.sql.SQLException.class,()->admin.eliminarLocal(localId));
        assertThrows(java.sql.SQLException.class,()->admin.eliminarOrganizador(organizadorId));
        assertThrows(java.sql.SQLException.class,()->admin.eliminarArtista(artistaId));
    }

    @Test void apiRespondeCodigosEstrictos()throws Exception{
        admin.publicar(eventoId);
        ApiResultado detalle=consultarApi("/"+eventoId,Map.of());
        assertEquals(200,detalle.status());assertTrue(detalle.body().contains("\"organizador\""));assertTrue(detalle.body().contains("\"zonas\""));assertTrue(detalle.body().contains("\"disponibilidad\":7"));
        assertEquals(200,consultarApi("/",Map.of("categoria","CONCIERTO","ciudad","Lima","fecha",LocalDateTime.now().plusDays(30).toLocalDate().toString(),"q",sufijo)).status());
        assertEquals(400,consultarApi("/no-numero",Map.of()).status());
        assertEquals(400,consultarApi("/",Map.of("fecha","fecha-invalida")).status());
        assertEquals(404,consultarApi("/2147483000",Map.of()).status());

    }

    private int buscarId(String tabla,String columna,String valor)throws Exception{
        String sql="SELECT id FROM "+tabla+" WHERE "+columna+"=? ORDER BY id DESC LIMIT 1";
        try(Connection c=conexiones.abrir();PreparedStatement p=c.prepareStatement(sql)){p.setString(1,valor);try(ResultSet r=p.executeQuery()){assertTrue(r.next());return r.getInt(1);}}
    }
    private ApiResultado consultarApi(String ruta,Map<String,String> parametros)throws Exception{
        AtomicInteger estado=new AtomicInteger();StringWriter cuerpo=new StringWriter();
        HttpServletRequest req=proxy(HttpServletRequest.class,(m,a)->switch(m.getName()){case "getPathInfo"->ruta;case "getParameter"->parametros.get(a[0]);default->null;});
        HttpServletResponse resp=proxy(HttpServletResponse.class,(m,a)->{switch(m.getName()){case "setStatus"->estado.set((int)a[0]);case "getWriter"-> {return new PrintWriter(cuerpo);}case "sendError"->{estado.set((int)a[0]);}default->{}}return null;});
        new EventoApiServlet(api).doGet(req,resp);return new ApiResultado(estado.get(),cuerpo.toString());
    }
    @SuppressWarnings("unchecked") private static <T>T proxy(Class<T> type,Handler handler){
        return (T)Proxy.newProxyInstance(type.getClassLoader(),new Class<?>[]{type},(proxy,method,args)->handler.invoke(method,args==null?new Object[0]:args));
    }
    @FunctionalInterface private interface Handler{Object invoke(java.lang.reflect.Method method,Object[] args)throws Throwable;}
    private record ApiResultado(int status,String body){}
}
