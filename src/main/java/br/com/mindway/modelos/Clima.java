package br.com.mindway.modelos;

public class Clima {

    private double temperatura;     
    private double sensacaoTermica; 
    private int umidade;            
    private String descricao;       
    private String icone;           
    private double ventoKmh;        
    private String cidade;          

    public Clima() {
    }

    public boolean climaBomParaSair() {
        if (descricao == null) return true;
        String desc = descricao.toLowerCase();
        boolean temChuva = desc.contains("chuva") || desc.contains("rain")
                || desc.contains("storm") || desc.contains("tempest");
        boolean ventoForte = ventoKmh > 40;
        return !temChuva && !ventoForte;
    }

    public String getDica() {
        if (!climaBomParaSair()) {
            return "⚠️ Cuidado! O clima não está ideal para bike ou caminhada.";
        }
        if (temperatura > 30) {
            return "☀️ Está quente! Leve água e use protetor solar.";
        }
        if (temperatura < 15) {
            return "🧥 Está frio! Vista um casaco para pedalar.";
        }
        return "✅ Clima bom para sair! Aproveite o trajeto.";
    }

    public String getIconeUrl() {
        if (icone == null || icone.isEmpty()) {
            return "";
        }
        return "https://openweathermap.org/img/wn/" + icone + "@2x.png";
    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public double getSensacaoTermica() {
        return sensacaoTermica;
    }

    public void setSensacaoTermica(double sensacaoTermica) {
        this.sensacaoTermica = sensacaoTermica;
    }

    public int getUmidade() {
        return umidade;
    }

    public void setUmidade(int umidade) {
        this.umidade = umidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public double getVentoKmh() {
        return ventoKmh;
    }

    public void setVentoKmh(double ventoKmh) {
        this.ventoKmh = ventoKmh;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
}
