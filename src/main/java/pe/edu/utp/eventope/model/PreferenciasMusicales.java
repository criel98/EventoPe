package pe.edu.utp.eventope.model;

import java.io.Serializable;

/** Datos de preferencias_musicales. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class PreferenciasMusicales implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int clienteId;
    private String generoFavorito;
    private String artistaFavorito;

    public PreferenciasMusicales() {}

    public PreferenciasMusicales(int id, int clienteId, String generoFavorito, String artistaFavorito) {
        this.id = id;
        this.clienteId = clienteId;
        this.generoFavorito = generoFavorito;
        this.artistaFavorito = artistaFavorito;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public String getGeneroFavorito() { return generoFavorito; }
    public void setGeneroFavorito(String generoFavorito) { this.generoFavorito = generoFavorito; }

    public String getArtistaFavorito() { return artistaFavorito; }
    public void setArtistaFavorito(String artistaFavorito) { this.artistaFavorito = artistaFavorito; }


}
