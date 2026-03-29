package br.com.mindway.servicos;

import br.com.mindway.modelos.PontoTransporte;
import br.com.mindway.utilidades.ConexaoApi;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;

public class ServicoTransporte {

    private static final String URL_BASE = "https://overpass-api.de/api/interpreter?data=";

    public List<PontoTransporte> buscarTransporteProximo(
            double latitude, double longitude, int raioMetros) throws Exception {

        String query = "[out:json][timeout:10];"
                + "("
                + "node[\"highway\"=\"bus_stop\"](around:" + raioMetros + "," + latitude + "," + longitude + ");"
                + "node[\"railway\"=\"station\"](around:" + raioMetros + "," + latitude + "," + longitude + ");"
                + "node[\"station\"=\"subway\"](around:" + raioMetros + "," + latitude + "," + longitude + ");"
                + ");"
                + "out body;";

        String url = URL_BASE + java.net.URLEncoder.encode(query, "UTF-8");
        String resposta = ConexaoApi.fazerRequisicaoGet(url);

        return converterParaPontos(resposta);
    }

    private List<PontoTransporte> converterParaPontos(String json) {
        List<PontoTransporte> pontos = new ArrayList<>();
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        JsonArray elementos = obj.getAsJsonArray("elements");

        for (JsonElement elemento : elementos) {
            JsonObject no = elemento.getAsJsonObject();

            double lat = no.get("lat").getAsDouble();
            double lon = no.get("lon").getAsDouble();

            String nome = "Sem nome";
            JsonObject tags = no.has("tags") ? no.getAsJsonObject("tags") : null;
            if (tags != null && tags.has("name")) {
                nome = tags.get("name").getAsString();
            }

            String tipo = identificarTipo(tags);

            pontos.add(new PontoTransporte(nome, tipo, lat, lon));
        }

        return pontos;
    }

    private String identificarTipo(JsonObject tags) {
        if (tags == null)
            return "onibus";

        if (tags.has("station") && "subway".equals(tags.get("station").getAsString())) {
            return "metro";
        }
        if (tags.has("railway") && "station".equals(tags.get("railway").getAsString())) {
            return "trem";
        }
        return "onibus"; 
    }
}
