package br.com.mindway.modelos;

public class EstacaoBike {

    private String nome;            
    private double latitude;
    private double longitude;
    private int bikesDisponiveis;   
    private int vagasLivres;        

    public EstacaoBike() {
    }

    public EstacaoBike(String nome, double latitude, double longitude,
                       int bikesDisponiveis, int vagasLivres) {
        this.nome = nome;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bikesDisponiveis = bikesDisponiveis;
        this.vagasLivres = vagasLivres;
    }

    public boolean temBikeDisponivel() {
        return bikesDisponiveis > 0;
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

    public int getBikesDisponiveis() {
        return bikesDisponiveis;
    }

    public void setBikesDisponiveis(int bikesDisponiveis) {
        this.bikesDisponiveis = bikesDisponiveis;
    }

    public int getVagasLivres() {
        return vagasLivres;
    }

    public void setVagasLivres(int vagasLivres) {
        this.vagasLivres = vagasLivres;
    }
}
