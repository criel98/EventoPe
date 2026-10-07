package pe.edu.utp.eventope.model;

import java.io.Serializable;

/** Datos de artista. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class Artista implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String nombreArtistico;
    private String generoMusical;
    private String paisOrigen;
    private String redSocial;

    public Artista() {}

    public Artista(int id, String nombreArtistico, String generoMusical, String paisOrigen, String redSocial) {
        this.id = id;
        this.nombreArtistico = nombreArtistico;
        this.generoMusical = generoMusical;
        this.paisOrigen = paisOrigen;
        this.redSocial = redSocial;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombreArtistico() { return nombreArtistico; }
    public void setNombreArtistico(String nombreArtistico) { this.nombreArtistico = nombreArtistico; }

    public String getGeneroMusical() { return generoMusical; }
    public void setGeneroMusical(String generoMusical) { this.generoMusical = generoMusical; }

    public String getPaisOrigen() { return paisOrigen; }
    public void setPaisOrigen(String paisOrigen) { this.paisOrigen = paisOrigen; }

    public String getRedSocial() { return redSocial; }
    public void setRedSocial(String redSocial) { this.redSocial = redSocial; }


}
