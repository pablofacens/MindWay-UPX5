package br.com.mindway.servicos;

import br.com.mindway.utilidades.ConexaoApi;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ServicoElevacao {

    private static final String URL_BASE = "https://api.open-elevation.com/api/v1/lookup";

    public String buscarElevacao(double[][] coordenadas) throws Exception {

        StringBuilder locations = new StringBuilder();

        int passo = Math.max(1, coordenadas.length / 50);

        for (int i = 0; i < coordenadas.length; i += passo) {
            if (locations.length() > 0) {
                locations.append("|");
            }
            locations.append(coordenadas[i][0]).append(",").append(coordenadas[i][1]);
        }

        String url = URL_BASE + "?locations=" + locations.toString();
        return ConexaoApi.fazerRequisicaoGet(url);
    }

    public double buscarElevacaoPonto(double latitude, double longitude) throws Exception {
        String url = URL_BASE + "?locations=" + latitude + "," + longitude;
        String resposta = ConexaoApi.fazerRequisicaoGet(url);

        JsonObject json = JsonParser.parseString(resposta).getAsJsonObject();
        JsonArray resultados = json.getAsJsonArray("results");

        if (resultados.size() > 0) {
            return resultados.get(0).getAsJsonObject().get("elevation").getAsDouble();
        }
        return 0;
    }

    public JsonObject analisarDificuldade(String elevacoesJson) {
        JsonObject json = JsonParser.parseString(elevacoesJson).getAsJsonObject();
        JsonArray resultados = json.getAsJsonArray("results");

        double ganhoTotal = 0; 
        double perdaTotal = 0; 
        double elevMax = 0;
        double elevMin = Double.MAX_VALUE;

        double elevAnterior = -1;

        for (int i = 0; i < resultados.size(); i++) {
            double elev = resultados.get(i).getAsJsonObject().get("elevation").getAsDouble();

            if (elev > elevMax)
                elevMax = elev;
            if (elev < elevMin)
                elevMin = elev;

            if (elevAnterior >= 0) {
                double diferenca = elev - elevAnterior;
                if (diferenca > 0)
                    ganhoTotal += diferenca;
                else
                    perdaTotal += Math.abs(diferenca);
            }
            elevAnterior = elev;
        }

        String dificuldade;
        String dica;
        if (ganhoTotal < 50) {
            dificuldade = "Fácil";
            dica = "Terreno plano, ótimo para caminhada ou bike.";
        } else if (ganhoTotal < 150) {
            dificuldade = "Moderado";
            dica = "Algumas subidas. Leve água e vá no seu ritmo.";
        } else {
            dificuldade = "Difícil";
            dica = "Muitas subidas! Considere usar transporte público em parte do trajeto.";
        }

        JsonObject resultado = new JsonObject();
        resultado.addProperty("ganhoElevacao", Math.round(ganhoTotal));
        resultado.addProperty("perdaElevacao", Math.round(perdaTotal));
        resultado.addProperty("elevacaoMaxima", Math.round(elevMax));
        resultado.addProperty("elevacaoMinima", Math.round(elevMin));
        resultado.addProperty("dificuldade", dificuldade);
        resultado.addProperty("dica", dica);

        return resultado;
    }
}
