package pe.edu.utp.eventope.service;

import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.dao.ApiEventoDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ServicioApiEventos {
    private final ProveedorConexion conexiones;
    private final ApiEventoDAO dao=new ApiEventoDAO();
    public ServicioApiEventos(ProveedorConexion conexiones){this.conexiones=conexiones;}

    public List<Map<String,Object>> listar(String categoria,String ciudad,String fecha,String q)throws SQLException{
        categoria=normalizar(categoria,"categoria");ciudad=normalizar(ciudad,"ciudad");q=normalizar(q,"q");
        if(categoria!=null&&!List.of("CONCIERTO","TEATRO","FESTIVAL").contains(categoria.toUpperCase(java.util.Locale.ROOT)))throw new IllegalArgumentException("El filtro categoria debe ser CONCIERTO, TEATRO o FESTIVAL.");
        if(categoria!=null)categoria=categoria.toUpperCase(java.util.Locale.ROOT);
        if(fecha!=null&&!fecha.isBlank()){try{fecha=LocalDate.parse(fecha).toString();}catch(DateTimeParseException e){throw new IllegalArgumentException("El filtro fecha debe usar el formato AAAA-MM-DD.");}}
        else fecha=null;
        try(Connection c=conexiones.abrir()){return dao.listar(c,categoria,ciudad,fecha,q);}
    }
    public Optional<Map<String,Object>> detalle(int id)throws SQLException{if(id<=0)throw new IllegalArgumentException("Id inválido.");try(Connection c=conexiones.abrir()){return dao.detalle(c,id);}}
    private static String normalizar(String valor,String nombre){
        if(valor==null||valor.isBlank())return null;
        String t=valor.trim();if(t.length()>150)throw new IllegalArgumentException("El filtro "+nombre+" admite hasta 150 caracteres.");return t;
    }
}
