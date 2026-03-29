package br.com.mindway.modelos;

public class PontoTransporte {

    private String nome;        
    private String tipo;        
    private double latitude;
    private double longitude;

    public PontoTransporte() {
    }

    public PontoTransporte(String nome, String tipo, double latitude, double longitude) {
        this.nome = nome;
        this.tipo = tipo;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getEmoji() {
        switch (tipo) {
            case "onibus":
                return "🚌";
            case "metro":
                return "🚇";
            case "trem":
                return "🚆";
            default:
                return "📍";
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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
}
