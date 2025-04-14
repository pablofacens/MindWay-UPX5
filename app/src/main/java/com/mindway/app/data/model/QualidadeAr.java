package com.mindway.app.data.model;

public class QualidadeAr {

    private int indiceAQI;
    private String classificacao;
    private double pm25;
    private double pm10;

    public QualidadeAr() {}

    public QualidadeAr(int indiceAQI, String classificacao, double pm25, double pm10) {
        this.indiceAQI = indiceAQI;
        this.classificacao = classificacao;
        this.pm25 = pm25;
        this.pm10 = pm10;
    }

    public int getIndiceAQI() {
        return indiceAQI;
    }

    public void setIndiceAQI(int indiceAQI) {
        this.indiceAQI = indiceAQI;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }

    public double getPm25() {
        return pm25;
    }

    public void setPm25(double pm25) {
        this.pm25 = pm25;
    }

    public double getPm10() {
        return pm10;
    }

    public void setPm10(double pm10) {
        this.pm10 = pm10;
    }
}
