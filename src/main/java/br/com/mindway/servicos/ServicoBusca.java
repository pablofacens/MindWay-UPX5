package br.com.mindway.servicos;

import br.com.mindway.utilidades.ConexaoApi;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ServicoBusca {

    private static final String URL_BASE = "https://nominatim.openstreetmap.org/search";

    public String buscarEndereco(String texto) throws Exception {

        String textoFormatado = URLEncoder.encode(texto, StandardCharsets.UTF_8.toString());
        String url = URL_BASE + "?q=" + textoFormatado
                + "&format=json"
                + "&limit=5"
                + "&countrycodes=br" 
                + "&addressdetails=1";

        String resposta = ConexaoApi.fazerRequisicaoGet(url);
        return resposta;
    }

    public JsonObject buscarPrimeiroResultado(String texto) throws Exception {
        String resposta = buscarEndereco(texto);
        JsonArray resultados = JsonParser.parseString(resposta).getAsJsonArray();

        if (resultados.size() > 0) {
            return resultados.get(0).getAsJsonObject();
        }
        return null;
    }
}
