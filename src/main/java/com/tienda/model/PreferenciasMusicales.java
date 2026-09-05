package com.tienda.model;

import java.util.UUID;

public class PreferenciasMusicales {
    private String id;
    private String clienteId;
    private String generoFavorito;
    private String artistaFavorito;

    public PreferenciasMusicales() {
        this.id = UUID.randomUUID().toString();
    }

    public PreferenciasMusicales(String clienteId, String generoFavorito, String artistaFavorito) {
        this();
        this.clienteId = clienteId;
        this.generoFavorito = generoFavorito;
        this.artistaFavorito = artistaFavorito;
    }

    public String getId() {
        return id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
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

    @Override
    public String toString() {
        return "PreferenciasMusicales{" +
                "id='" + id + '\'' +
                ", clienteId='" + clienteId + '\'' +
                ", generoFavorito='" + generoFavorito + '\'' +
                ", artistaFavorito='" + artistaFavorito + '\'' +
                '}';
    }
}
