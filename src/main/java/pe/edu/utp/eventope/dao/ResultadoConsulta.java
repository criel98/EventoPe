package pe.edu.utp.eventope.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class ResultadoConsulta {
    private ResultadoConsulta() {}

    public record Cupon(int id,String codigo,BigDecimal porcentaje,LocalDateTime expiracion,String estado){
        public int getId(){return id;}
        public String getCodigo(){return codigo;}
        public BigDecimal getPorcentaje(){return porcentaje;}
        public LocalDateTime getExpiracion(){return expiracion;}
        public String getEstado(){return estado;}
    }

    public record Politica(int eventoId,String evento,int dias,BigDecimal penalizacion,boolean activa){
        public int getEventoId(){return eventoId;}
        public String getEvento(){return evento;}
        public int getDias(){return dias;}
        public BigDecimal getPenalizacion(){return penalizacion;}
        public boolean isActiva(){return activa;}
    }

    public static List<Cupon> cupones(ResultSet r) throws SQLException {
        List<Cupon> lista=new ArrayList<>();
        while(r.next()) lista.add(new Cupon(r.getInt(1),r.getString(2),r.getBigDecimal(3),
                r.getTimestamp(4).toLocalDateTime(),r.getString(5)));
        return lista;
    }

    public static List<Politica> politicas(ResultSet r) throws SQLException {
        List<Politica> lista=new ArrayList<>();
        while(r.next()) lista.add(new Politica(r.getInt(1),r.getString(2),r.getInt(3),
                r.getBigDecimal(4),r.getBoolean(5)));
        return lista;
    }
}
