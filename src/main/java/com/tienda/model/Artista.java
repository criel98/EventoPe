package com.tienda.model;

public class Artista {
    private int id;
    private String nombreArtistico;
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
