package pe.edu.utp.eventope.service;

import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.dao.*;
import pe.edu.utp.eventope.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Reglas de administración de cartelera. Cada operación de escritura es atómica. */
public final class ServicioAdminEventos {
    private final ProveedorConexion conexiones;
    private final OrganizadorDAO organizadores = new OrganizadorDAO();
    private final ArtistaDAO artistas = new ArtistaDAO();
    private final LocalDAO locales = new LocalDAO();
    private final EventoDAO eventos = new EventoDAO();
    private final CategoriaAsientoDAO categorias = new CategoriaAsientoDAO();

    public ServicioAdminEventos(ProveedorConexion conexiones) { this.conexiones = conexiones; }

    public List<OrganizadorEvento> organizadores() throws SQLException { try(Connection c=conexiones.abrir()){return organizadores.listar(c);} }
    public List<Artista> artistas() throws SQLException { try(Connection c=conexiones.abrir()){return artistas.listar(c);} }
    public List<LocalConcierto> locales() throws SQLException { try(Connection c=conexiones.abrir()){return locales.listar(c);} }
    public List<EventoCultural> eventos() throws SQLException { try(Connection c=conexiones.abrir()){return eventos.listarAdministracion(c);} }
    public List<CategoriaAsiento> zonas(int eventoId) throws SQLException {
        if(eventoId<=0)throw new IllegalArgumentException("Id de evento inválido.");
        try(Connection c=conexiones.abrir()){exigirEvento(c,eventoId,false);return categorias.listarPorEvento(c,eventoId);}
    }

    public void guardar(OrganizadorEvento o) throws SQLException {
        o.validar();
        transaccion(c->{if(o.getId()==0)organizadores.insertar(c,o);else organizadores.actualizar(c,o);return null;});
    }
    public void guardar(Artista a) throws SQLException {
        a.validar();
        transaccion(c->{if(a.getId()==0)artistas.insertar(c,a);else artistas.actualizar(c,a);return null;});
    }
    public void guardar(LocalConcierto l) throws SQLException {
        l.validar();
        transaccion(c->{
            if(l.getId()>0){
                locales.buscar(c,l.getId(),true).orElseThrow(()->new IllegalArgumentException("Local no encontrado."));
                try(PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM evento_cultural WHERE local_id=? AND aforo>?")){
                    p.setInt(1,l.getId());p.setInt(2,l.getCapacidadMax());
                    try(ResultSet r=p.executeQuery()){r.next();if(r.getLong(1)>0)throw new IllegalArgumentException("La nueva capacidad es inferior al aforo de un evento asociado.");}
                }
                locales.actualizar(c,l);
            }else locales.insertar(c,l);
            return null;
        });
    }
    public void guardar(EventoCultural e) throws SQLException {
        validarEvento(e);
        transaccion(c->{
            validarCapacidadLocal(c,e.getLocalId(),e.getAforo());
            if(e.getId()==0)e.setEstado("BORRADOR");
            else{
                EventoCultural actual=exigirEvento(c,e.getId(),true);
                e.setEstado(actual.getEstado());
            }
            validarAforoZonas(c,e.getAforo(),e.getId());
            if(e.getId()==0)eventos.insertar(c,e);else eventos.actualizar(c,e);
            return null;
        });
    }

    public void publicar(int id) throws SQLException {
        transaccion(c->{
            EventoCultural e=exigirEvento(c,id,true);
            List<CategoriaAsiento> zs=categorias.listarPorEvento(c,id);
            if(zs.isEmpty())throw new IllegalArgumentException("Agrega al menos una zona válida antes de publicar.");
            long total=0;
            for(CategoriaAsiento z:zs){z.validar();total+=z.getAforoTotal();}
            if(total>e.getAforo())throw new IllegalArgumentException("La suma de las zonas supera el aforo del evento.");
            e.publicar();eventos.actualizar(c,e);return null;
        });
    }
    public void desactivar(int id) throws SQLException {
        transaccion(c->{EventoCultural e=exigirEvento(c,id,true);e.desactivar();eventos.actualizar(c,e);return null;});
    }
    public void eliminarEvento(int id)throws SQLException{
        transaccion(c->{exigirEvento(c,id,true);eventos.eliminar(c,id);return null;});
    }
    public void eliminarOrganizador(int id)throws SQLException{transaccion(c->{organizadores.eliminar(c,id);return null;});}
    public void eliminarArtista(int id)throws SQLException{transaccion(c->{artistas.eliminar(c,id);return null;});}
    public void eliminarLocal(int id)throws SQLException{transaccion(c->{locales.eliminar(c,id);return null;});}
    public void eliminarZona(int id)throws SQLException{transaccion(c->{categorias.eliminar(c,id);return null;});}

    public void guardar(CategoriaAsiento z) throws SQLException {
        z.validar();
        transaccion(c->{
            EventoCultural evento=exigirEvento(c,z.getEventoId(),true);
            long total=0;
            for(CategoriaAsiento existente:categorias.listarPorEvento(c,z.getEventoId())){
                if(existente.getId()!=z.getId())total+=existente.getAforoTotal();
            }
            int comprometidas=0;
            if(z.getId()>0){
                CategoriaAsiento actual=categorias.buscar(c,z.getId(),true).orElseThrow(()->new IllegalArgumentException("Zona no encontrada."));
                if(actual.getEventoId()!=z.getEventoId())throw new IllegalArgumentException("La zona no pertenece al evento indicado.");
                int entradasComprometidas=categorias.contarEntradasNoAnuladas(c,z.getId());
                actual.validarReduccionCapacidad(z.getAforoTotal(),entradasComprometidas);
                comprometidas=Math.max(actual.getAforoTotal()-actual.getAforoDisponible(),entradasComprometidas);
            }
            if(total+z.getAforoTotal()>evento.getAforo())throw new IllegalArgumentException("La suma de las zonas supera el aforo del evento.");
            z.setAforoDisponible(z.getAforoTotal()-comprometidas);
            if(z.getId()==0)categorias.insertar(c,z);else categorias.actualizar(c,z);
            return null;
        });
    }

    private void validarEvento(EventoCultural e) {
        if(e.getNombre()==null||e.getNombre().isBlank()||e.getFecha()==null||e.getAforo()<=0||e.getLocalId()<=0||e.getOrganizadorId()<=0)
            throw new IllegalArgumentException("Completa nombre, fecha, aforo, local y organizador con valores válidos.");
        String tipo=e.getTipoEvento()==null?"":e.getTipoEvento().trim().toUpperCase(Locale.ROOT);
        if(!Set.of("CONCIERTO","TEATRO","FESTIVAL").contains(tipo))throw new IllegalArgumentException("La categoría del evento no es válida.");
        e.setTipoEvento(tipo);
    }
    private void validarCapacidadLocal(Connection c,int localId,int aforo)throws SQLException{
        LocalConcierto local=locales.buscar(c,localId,true).orElseThrow(()->new IllegalArgumentException("El local seleccionado no existe."));
        if(aforo>local.getCapacidadMax())throw new IllegalArgumentException("El aforo del evento supera la capacidad del local.");
    }
    private void validarAforoZonas(Connection c,int aforo,int eventoId)throws SQLException{
        String sql="SELECT COALESCE(SUM(aforo_total),0) FROM categoria_asiento WHERE evento_id=?";
        try(PreparedStatement p=c.prepareStatement(sql)){p.setInt(1,eventoId);try(ResultSet r=p.executeQuery()){r.next();if(r.getLong(1)>aforo)throw new IllegalArgumentException("El aforo no puede quedar por debajo de la suma de las zonas.");}}
    }
    private EventoCultural exigirEvento(Connection c,int id,boolean bloquear)throws SQLException{
        if(id<=0)throw new IllegalArgumentException("Id de evento inválido.");
        if(!bloquear)return eventos.buscarAdministracion(c,id).orElseThrow(()->new IllegalArgumentException("Evento no encontrado."));
        try(PreparedStatement p=c.prepareStatement("SELECT id,nombre,descripcion,tipo_evento,fecha,aforo,local_id,organizador_id,artista_id,estado FROM evento_cultural WHERE id=? FOR UPDATE")){
            p.setInt(1,id);try(ResultSet r=p.executeQuery()){
                if(!r.next())throw new IllegalArgumentException("Evento no encontrado.");
                int artistId=r.getInt("artista_id");Integer nullableArtist=r.wasNull()?null:artistId;
                return new EventoCultural(r.getInt("id"),r.getString("nombre"),r.getString("descripcion"),r.getString("tipo_evento"),r.getTimestamp("fecha").toLocalDateTime(),r.getInt("aforo"),r.getInt("local_id"),r.getInt("organizador_id"),nullableArtist,r.getString("estado"));
            }
        }
    }

    private <T>T transaccion(Trabajo<T> trabajo)throws SQLException{
        try(Connection c=conexiones.abrir()){
            boolean auto=c.getAutoCommit();c.setAutoCommit(false);
            try{T respuesta=trabajo.ejecutar(c);c.commit();return respuesta;}
            catch(SQLException|RuntimeException e){try{c.rollback();}catch(SQLException rollback){e.addSuppressed(rollback);}throw e;}
            finally{c.setAutoCommit(auto);}
        }
    }
    @FunctionalInterface private interface Trabajo<T>{T ejecutar(Connection c)throws SQLException;}
}
