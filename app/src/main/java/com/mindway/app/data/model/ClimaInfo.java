package com.mindway.app.data.model;

public class ClimaInfo {

    private String descricao;
    private double temperaturaC;
    private int umidade;
    private double velocidadeVentoKmh;
    private String icone;

    public ClimaInfo() {}

    public ClimaInfo(String descricao, double temperaturaC, int umidade,
                     double velocidadeVentoKmh, String icone) {
        this.descricao = descricao;
        this.temperaturaC = temperaturaC;
        this.umidade = umidade;
        this.velocidadeVentoKmh = velocidadeVentoKmh;
        this.icone = icone;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getTemperaturaC() {
        return temperaturaC;
    }

    public void setTemperaturaC(double temperaturaC) {
        this.temperaturaC = temperaturaC;
    }

    public int getUmidade() {
        return umidade;
    }

    public void setUmidade(int umidade) {
        this.umidade = umidade;
    }

    public double getVelocidadeVentoKmh() {
        return velocidadeVentoKmh;
    }

    public void setVelocidadeVentoKmh(double velocidadeVentoKmh) {
        this.velocidadeVentoKmh = velocidadeVentoKmh;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }
}
