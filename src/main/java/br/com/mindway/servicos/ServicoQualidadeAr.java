package br.com.mindway.servicos;

import br.com.mindway.utilidades.ConexaoApi;

public class ServicoQualidadeAr {

    private static final String API_TOKEN = "demo";
    private static final String URL_BASE = "https://api.waqi.info";

    public String buscarPorCoordenadas(double latitude, double longitude) throws Exception {
        String url = URL_BASE + "/feed/geo:" + latitude + ";" + longitude
                + "/?token=" + API_TOKEN;
        return ConexaoApi.fazerRequisicaoGet(url);
    }

    public String buscarPorCidade(String cidade) throws Exception {
        String url = URL_BASE + "/feed/" + cidade + "/?token=" + API_TOKEN;
        return ConexaoApi.fazerRequisicaoGet(url);
    }

    public static String interpretarAQI(int aqi) {
        if (aqi <= 50) {
            return "Boa ✅ — Ar limpo, ótimo para atividades ao ar livre!";
        } else if (aqi <= 100) {
            return "Moderada ⚠️ — Aceitável. Pessoas sensíveis podem ter leve desconforto.";
        } else if (aqi <= 150) {
            return "Ruim para sensíveis 😷 — Evite exercícios intensos ao ar livre.";
        } else if (aqi <= 200) {
            return "Ruim 🔴 — Prefira transporte fechado (ônibus, metrô).";
        } else if (aqi <= 300) {
            return "Muito ruim ⛔ — Alerta de saúde! Evite atividades ao ar livre.";
        } else {
            return "Perigosa 🚨 — Emergência! NÃO faça atividades ao ar livre.";
        }
    }
}
