package br.com.mindway.servicos;

import br.com.mindway.modelos.EstacaoBike;
import br.com.mindway.utilidades.ConexaoApi;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;

public class ServicoBike {

    private static final String URL_BASE = "https://api.citybik.es/v2/networks";

    public List<EstacaoBike> buscarEstacoes(String cidadeOuRede) throws Exception {
        List<EstacaoBike> estacoes = new ArrayList<>();

        String respostaRedes = ConexaoApi.fazerRequisicaoGet(URL_BASE);
        JsonObject jsonRedes = JsonParser.parseString(respostaRedes).getAsJsonObject();
        JsonArray redes = jsonRedes.getAsJsonArray("networks");

        String redeId = null;
        for (JsonElement elemento : redes) {
            JsonObject rede = elemento.getAsJsonObject();
            JsonObject localizacao = rede.getAsJsonObject("location");
            String cidade = localizacao.get("city").getAsString().toLowerCase();
            String nome = rede.get("name").getAsString().toLowerCase();

            if (cidade.contains(cidadeOuRede.toLowerCase()) ||
                    nome.contains(cidadeOuRede.toLowerCase())) {
                redeId = rede.get("id").getAsString();
                break;
            }
        }

        if (redeId == null) {
            return estacoes; 
        }

        String respostaEstacoes = ConexaoApi.fazerRequisicaoGet(URL_BASE + "/" + redeId);
        JsonObject jsonEstacoes = JsonParser.parseString(respostaEstacoes).getAsJsonObject();
        JsonObject redeDetalhe = jsonEstacoes.getAsJsonObject("network");
        JsonArray stations = redeDetalhe.getAsJsonArray("stations");

        for (JsonElement elemento : stations) {
            JsonObject estacaoJson = elemento.getAsJsonObject();

            EstacaoBike estacao = new EstacaoBike();
            estacao.setNome(estacaoJson.get("name").getAsString());
            estacao.setLatitude(estacaoJson.get("latitude").getAsDouble());
            estacao.setLongitude(estacaoJson.get("longitude").getAsDouble());

            if (estacaoJson.has("free_bikes") && !estacaoJson.get("free_bikes").isJsonNull()) {
                estacao.setBikesDisponiveis(estacaoJson.get("free_bikes").getAsInt());
            }
            if (estacaoJson.has("empty_slots") && !estacaoJson.get("empty_slots").isJsonNull()) {
                estacao.setVagasLivres(estacaoJson.get("empty_slots").getAsInt());
            }

            estacoes.add(estacao);
        }

        return estacoes;
    }
}
