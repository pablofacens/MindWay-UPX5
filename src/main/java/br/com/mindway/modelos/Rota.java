package br.com.mindway.modelos;

public class Rota {

    private String tipo;           
    private double distanciaKm;    
    private double duracaoMinutos; 
    private double co2Gramas;      
    private double caloriasQueimadas;
    private String coordenadasJson; 

    public Rota() {
    }

    public Rota(String tipo, double distanciaKm, double duracaoMinutos) {
        this.tipo = tipo;
        this.distanciaKm = distanciaKm;
        this.duracaoMinutos = duracaoMinutos;
        calcularImpacto();
    }

    private void calcularImpacto() {
        switch (tipo) {
            case "carro":
                this.co2Gramas = distanciaKm * 120; 
                this.caloriasQueimadas = 0;
                break;
            case "bike":
                this.co2Gramas = 0;
                this.caloriasQueimadas = distanciaKm * 40; 
                break;
            case "pe":
                this.co2Gramas = 0;
                this.caloriasQueimadas = distanciaKm * 50; 
                break;
            default:
                this.co2Gramas = 0;
                this.caloriasQueimadas = 0;
        }
    }

    public double getCo2Economizado() {
        double co2Carro = distanciaKm * 120;
        return co2Carro - this.co2Gramas;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public double getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(double duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public double getCo2Gramas() {
        return co2Gramas;
    }

    public void setCo2Gramas(double co2Gramas) {
        this.co2Gramas = co2Gramas;
    }

    public double getCaloriasQueimadas() {
        return caloriasQueimadas;
    }

    public void setCaloriasQueimadas(double caloriasQueimadas) {
        this.caloriasQueimadas = caloriasQueimadas;
    }

    public String getCoordenadasJson() {
        return coordenadasJson;
    }

    public void setCoordenadasJson(String coordenadasJson) {
        this.coordenadasJson = coordenadasJson;
    }
}
