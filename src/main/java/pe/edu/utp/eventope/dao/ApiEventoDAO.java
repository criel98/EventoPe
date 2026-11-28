package pe.edu.utp.eventope.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Consultas públicas para la API; nunca incluye eventos que no estén publicados. */
public final class ApiEventoDAO {
    public List<Map<String,Object>> listar(Connection c,String categoria,String ciudad,String fecha,String q)throws SQLException{
        StringBuilder sql=new StringBuilder("SELECT e.id,e.nombre,e.descripcion,e.tipo_evento,e.fecha,e.aforo,l.nombre_local,l.ciudad,COALESCE(a.nombre_artistico,'Sin artista') artista,MIN(z.precio_base) precio_desde FROM evento_cultural e JOIN local_concierto l ON l.id=e.local_id LEFT JOIN artista a ON a.id=e.artista_id LEFT JOIN categoria_asiento z ON z.evento_id=e.id WHERE e.estado='PUBLICADO'");
        List<Object> parametros=new ArrayList<>();
        if(categoria!=null){sql.append(" AND e.tipo_evento=?");parametros.add(categoria);}
        if(ciudad!=null){sql.append(" AND LOWER(l.ciudad)=LOWER(?)");parametros.add(ciudad);}
        if(fecha!=null){sql.append(" AND DATE(e.fecha)=?");parametros.add(fecha);}
        if(q!=null){sql.append(" AND (e.nombre LIKE ? OR COALESCE(e.descripcion,'') LIKE ? OR COALESCE(a.nombre_artistico,'') LIKE ?)");String like="%"+q+"%";parametros.add(like);parametros.add(like);parametros.add(like);}
        sql.append(" GROUP BY e.id,e.nombre,e.descripcion,e.tipo_evento,e.fecha,e.aforo,l.nombre_local,l.ciudad,a.nombre_artistico ORDER BY e.fecha");
        List<Map<String,Object>> salida=new ArrayList<>();
        try(PreparedStatement p=c.prepareStatement(sql.toString())){
            for(int i=0;i<parametros.size();i++)p.setObject(i+1,parametros.get(i));
            try(ResultSet r=p.executeQuery()){while(r.next()){
                Map<String,Object> evento=new LinkedHashMap<>();evento.put("id",r.getInt("id"));evento.put("nombre",r.getString("nombre"));evento.put("descripcion",r.getString("descripcion"));evento.put("categoria",r.getString("tipo_evento"));evento.put("fecha",r.getTimestamp("fecha").toLocalDateTime());evento.put("aforo",r.getInt("aforo"));evento.put("local",r.getString("nombre_local"));evento.put("ciudad",r.getString("ciudad"));evento.put("artista",r.getString("artista"));evento.put("precioDesde",r.getBigDecimal("precio_desde"));salida.add(evento);
            }}
        }
        return salida;
    }

    public Optional<Map<String,Object>> detalle(Connection c,int id)throws SQLException{
        String sql="SELECT e.id,e.nombre,e.descripcion,e.tipo_evento,e.fecha,e.aforo,l.id local_id,l.nombre_local,l.direccion,l.ciudad,l.capacidad_max,o.id organizador_id,o.ruc,o.razon_social,o.email_contacto,o.telefono,a.id artista_id,a.nombre_artistico,a.genero_musical,a.pais_origen,a.red_social FROM evento_cultural e JOIN local_concierto l ON l.id=e.local_id JOIN organizador_evento o ON o.id=e.organizador_id LEFT JOIN artista a ON a.id=e.artista_id WHERE e.id=? AND e.estado='PUBLICADO'";
        Map<String,Object> evento=new LinkedHashMap<>();
        try(PreparedStatement p=c.prepareStatement(sql)){p.setInt(1,id);try(ResultSet r=p.executeQuery()){
            if(!r.next())return Optional.empty();
            evento.put("id",r.getInt("id"));evento.put("nombre",r.getString("nombre"));evento.put("descripcion",r.getString("descripcion"));evento.put("categoria",r.getString("tipo_evento"));evento.put("fecha",r.getTimestamp("fecha").toLocalDateTime());evento.put("aforo",r.getInt("aforo"));
            Map<String,Object> local=new LinkedHashMap<>();local.put("id",r.getInt("local_id"));local.put("nombre",r.getString("nombre_local"));local.put("direccion",r.getString("direccion"));local.put("ciudad",r.getString("ciudad"));local.put("aforoMaximo",r.getInt("capacidad_max"));evento.put("local",local);
            Map<String,Object> organizador=new LinkedHashMap<>();organizador.put("id",r.getInt("organizador_id"));organizador.put("ruc",r.getString("ruc"));organizador.put("razonSocial",r.getString("razon_social"));organizador.put("email",r.getString("email_contacto"));organizador.put("telefono",r.getString("telefono"));evento.put("organizador",organizador);
            List<Map<String,Object>> artistas=new ArrayList<>();int artistaId=r.getInt("artista_id");if(!r.wasNull()){Map<String,Object> artista=new LinkedHashMap<>();artista.put("id",artistaId);artista.put("nombreArtistico",r.getString("nombre_artistico"));artista.put("genero",r.getString("genero_musical"));artista.put("pais",r.getString("pais_origen"));artista.put("redSocial",r.getString("red_social"));artistas.add(artista);}evento.put("artistas",artistas);
        }}
        List<Map<String,Object>> zonas=new ArrayList<>();
        try(PreparedStatement p=c.prepareStatement("SELECT id,nombre_zona,precio_base,aforo_total,aforo_disponible FROM categoria_asiento WHERE evento_id=? ORDER BY precio_base,nombre_zona")){
            p.setInt(1,id);try(ResultSet r=p.executeQuery()){while(r.next()){Map<String,Object> z=new LinkedHashMap<>();z.put("id",r.getInt("id"));z.put("nombre",r.getString("nombre_zona"));z.put("precio",r.getBigDecimal("precio_base"));z.put("capacidad",r.getInt("aforo_total"));z.put("disponibilidad",r.getInt("aforo_disponible"));zonas.add(z);}}
        }
        evento.put("zonas",zonas);return Optional.of(evento);
    }
}
