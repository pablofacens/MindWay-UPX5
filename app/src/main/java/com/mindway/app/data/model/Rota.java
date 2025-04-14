package com.mindway.app.data.model;

import java.util.List;

public class Rota {

    private String id;
    private TipoRota tipo;
    private List<Parada> paradas;
    private double distanciaKm;
    private int tempoMinutos;
    private double custoBRL;
    private double emissaoCO2Gramas;
    private ClimaInfo clima;
    private QualidadeAr qualidadeAr;
    private List<EstacaoBicicleta> bicicletas;
    private List<PontoApoio> pontosApoio;
    private DicaIA dicaIA;
    /** Polilinha OSRM: cada elemento é double[]{lat, lon}. */
    private List<double[]> polilinha;

    public Rota() {}

    public Rota(String id, TipoRota tipo, List<Parada> paradas, double distanciaKm,
                int tempoMinutos, double custoBRL, double emissaoCO2Gramas,
                ClimaInfo clima, QualidadeAr qualidadeAr,
                List<EstacaoBicicleta> bicicletas, List<PontoApoio> pontosApoio,
                DicaIA dicaIA) {
        this.id = id;
        this.tipo = tipo;
        this.paradas = paradas;
        this.distanciaKm = distanciaKm;
        this.tempoMinutos = tempoMinutos;
        this.custoBRL = custoBRL;
        this.emissaoCO2Gramas = emissaoCO2Gramas;
        this.clima = clima;
        this.qualidadeAr = qualidadeAr;
        this.bicicletas = bicicletas;
        this.pontosApoio = pontosApoio;
        this.dicaIA = dicaIA;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TipoRota getTipo() {
        return tipo;
    }

    public void setTipo(TipoRota tipo) {
        this.tipo = tipo;
    }

    public List<Parada> getParadas() {
        return paradas;
    }

    public void setParadas(List<Parada> paradas) {
        this.paradas = paradas;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public int getTempoMinutos() {
        return tempoMinutos;
    }

    public void setTempoMinutos(int tempoMinutos) {
        this.tempoMinutos = tempoMinutos;
    }

    public double getCustoBRL() {
        return custoBRL;
    }

    public void setCustoBRL(double custoBRL) {
        this.custoBRL = custoBRL;
    }

    public double getEmissaoCO2Gramas() {
        return emissaoCO2Gramas;
    }

    public void setEmissaoCO2Gramas(double emissaoCO2Gramas) {
        this.emissaoCO2Gramas = emissaoCO2Gramas;
    }

    public ClimaInfo getClima() {
        return clima;
    }

    public void setClima(ClimaInfo clima) {
        this.clima = clima;
    }

    public QualidadeAr getQualidadeAr() {
        return qualidadeAr;
    }

    public void setQualidadeAr(QualidadeAr qualidadeAr) {
        this.qualidadeAr = qualidadeAr;
    }

    public List<EstacaoBicicleta> getBicicletas() {
        return bicicletas;
    }

    public void setBicicletas(List<EstacaoBicicleta> bicicletas) {
        this.bicicletas = bicicletas;
    }

    public List<PontoApoio> getPontosApoio() {
        return pontosApoio;
    }

    public void setPontosApoio(List<PontoApoio> pontosApoio) {
        this.pontosApoio = pontosApoio;
    }

    public DicaIA getDicaIA() {
        return dicaIA;
    }

    public void setDicaIA(DicaIA dicaIA) {
        this.dicaIA = dicaIA;
    }

    public List<double[]> getPolilinha() {
        return polilinha;
    }

    public void setPolilinha(List<double[]> polilinha) {
        this.polilinha = polilinha;
    }
}
