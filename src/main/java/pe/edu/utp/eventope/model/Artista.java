package pe.edu.utp.eventope.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "artista")
public class Artista implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nombre_artistico")
    private String nombreArtistico;

    @Column(name = "genero_musical")
    private String generoMusical;

    public Artista() {
    }

    public Artista(int id, String nombreArtistico, String generoMusical) {
        this.id = id;
        this.nombreArtistico = nombreArtistico;
        this.generoMusical = generoMusical;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreArtistico() {
        return nombreArtistico;
    }

    public void setNombreArtistico(String nombreArtistico) {
        this.nombreArtistico = nombreArtistico;
    }

    public String getGeneroMusical() {
        return generoMusical;
    }

    public void setGeneroMusical(String generoMusical) {
        this.generoMusical = generoMusical;
    }
}
