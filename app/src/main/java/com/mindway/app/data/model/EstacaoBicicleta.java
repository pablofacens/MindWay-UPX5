package com.mindway.app.data.model;

public class EstacaoBicicleta {

    private String id;
    private String nome;
    private double latitude;
    private double longitude;
    private int bicicletasDisponiveis;
    private int vagasLivres;

    public EstacaoBicicleta() {}

    public EstacaoBicicleta(String id, String nome, double latitude, double longitude,
                            int bicicletasDisponiveis, int vagasLivres) {
        this.id = id;
        this.nome = nome;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bicicletasDisponiveis = bicicletasDisponiveis;
        this.vagasLivres = vagasLivres;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getBicicletasDisponiveis() {
        return bicicletasDisponiveis;
    }

    public void setBicicletasDisponiveis(int bicicletasDisponiveis) {
        this.bicicletasDisponiveis = bicicletasDisponiveis;
    }

    public int getVagasLivres() {
        return vagasLivres;
    }

    public void setVagasLivres(int vagasLivres) {
        this.vagasLivres = vagasLivres;
    }
}
