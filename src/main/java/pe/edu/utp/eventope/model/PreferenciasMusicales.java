package pe.edu.utp.eventope.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "preferencias_musicales")
public class PreferenciasMusicales implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "cliente_id")
    private int clienteId;

    @Column(name = "genero_favorito")
    private String generoFavorito;

    @Column(name = "artista_favorito")
    private String artistaFavorito;

    public PreferenciasMusicales() {
    }

    public PreferenciasMusicales(int id, int clienteId, String generoFavorito, String artistaFavorito) {
        this.id = id;
        this.clienteId = clienteId;
        this.generoFavorito = generoFavorito;
        this.artistaFavorito = artistaFavorito;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public String getGeneroFavorito() {
        return generoFavorito;
    }

    public void setGeneroFavorito(String generoFavorito) {
        this.generoFavorito = generoFavorito;
    }

    public String getArtistaFavorito() {
        return artistaFavorito;
    }

    public void setArtistaFavorito(String artistaFavorito) {
        this.artistaFavorito = artistaFavorito;
    }
}
