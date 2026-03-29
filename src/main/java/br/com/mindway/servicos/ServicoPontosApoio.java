package br.com.mindway.servicos;

import br.com.mindway.utilidades.ConexaoApi;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URLEncoder;

public class ServicoPontosApoio {

    private static final String URL_BASE = "https://overpass-api.de/api/interpreter?data=";

    public String buscarPontosApoio(double latitude, double longitude, int raioMetros) throws Exception {

        String query = "[out:json][timeout:15];"
                + "("

                + "node[\"amenity\"=\"drinking_water\"](around:" + raioMetros + "," + latitude + "," + longitude + ");"

                + "node[\"amenity\"=\"toilets\"](around:" + raioMetros + "," + latitude + "," + longitude + ");"

                + "node[\"amenity\"=\"pharmacy\"](around:" + raioMetros + "," + latitude + "," + longitude + ");"
                + "way[\"amenity\"=\"pharmacy\"](around:" + raioMetros + "," + latitude + "," + longitude + ");"

                + "node[\"amenity\"=\"hospital\"](around:" + raioMetros + "," + latitude + "," + longitude + ");"
                + "way[\"amenity\"=\"hospital\"](around:" + raioMetros + "," + latitude + "," + longitude + ");"
                + "node[\"amenity\"=\"clinic\"](around:" + raioMetros + "," + latitude + "," + longitude + ");"

                + "node[\"amenity\"=\"police\"](around:" + raioMetros + "," + latitude + "," + longitude + ");"

                + "node[\"amenity\"=\"atm\"](around:" + raioMetros + "," + latitude + "," + longitude + ");"
                + "node[\"amenity\"=\"bank\"](around:" + raioMetros + "," + latitude + "," + longitude + ");"
                + ");"
                + "out center body 100;"; 

        String url = URL_BASE + URLEncoder.encode(query, "UTF-8");
        String resposta = ConexaoApi.fazerRequisicaoGet(url);

        return organizarPorTipo(resposta);
    }

    private String organizarPorTipo(String json) {
        JsonObject resposta = JsonParser.parseString(json).getAsJsonObject();
        JsonArray elementos = resposta.getAsJsonArray("elements");

        JsonArray bebedouros = new JsonArray();
        JsonArray banheiros = new JsonArray();
        JsonArray farmacias = new JsonArray();
        JsonArray hospitais = new JsonArray();
        JsonArray delegacias = new JsonArray();
        JsonArray bancos = new JsonArray();

        for (JsonElement el : elementos) {
            JsonObject no = el.getAsJsonObject();
            JsonObject tags = no.has("tags") ? no.getAsJsonObject("tags") : new JsonObject();

            double lat, lon;
            if (no.has("lat")) {
                lat = no.get("lat").getAsDouble();
                lon = no.get("lon").getAsDouble();
            } else if (no.has("center")) {
                JsonObject center = no.getAsJsonObject("center");
                lat = center.get("lat").getAsDouble();
                lon = center.get("lon").getAsDouble();
            } else {
                continue;
            }

            String nome = tags.has("name") ? tags.get("name").getAsString() : "";
            String amenity = tags.has("amenity") ? tags.get("amenity").getAsString() : "";

            JsonObject ponto = new JsonObject();
            ponto.addProperty("lat", lat);
            ponto.addProperty("lon", lon);
            ponto.addProperty("nome", nome.isEmpty() ? getNomePadrao(amenity) : nome);

            switch (amenity) {
                case "drinking_water":
                    ponto.addProperty("tipo", "bebedouro");
                    ponto.addProperty("emoji", "💧");
                    bebedouros.add(ponto);
                    break;
                case "toilets":
                    ponto.addProperty("tipo", "banheiro");
                    ponto.addProperty("emoji", "🚻");
                    banheiros.add(ponto);
                    break;
                case "pharmacy":
                    ponto.addProperty("tipo", "farmacia");
                    ponto.addProperty("emoji", "💊");
                    farmacias.add(ponto);
                    break;
                case "hospital":
                case "clinic":
                    ponto.addProperty("tipo", "hospital");
                    ponto.addProperty("emoji", "🏥");
                    hospitais.add(ponto);
                    break;
                case "police":
                    ponto.addProperty("tipo", "delegacia");
                    ponto.addProperty("emoji", "👮");
                    delegacias.add(ponto);
                    break;
                case "atm":
                case "bank":
                    ponto.addProperty("tipo", "banco");
                    ponto.addProperty("emoji", "🏦");
                    bancos.add(ponto);
                    break;
            }
        }

        JsonObject resultado = new JsonObject();
        resultado.add("bebedouros", bebedouros);
        resultado.add("banheiros", banheiros);
        resultado.add("farmacias", farmacias);
        resultado.add("hospitais", hospitais);
        resultado.add("delegacias", delegacias);
        resultado.add("bancos", bancos);

        int total = bebedouros.size() + banheiros.size() + farmacias.size()
                + hospitais.size() + delegacias.size() + bancos.size();
        resultado.addProperty("total", total);

        return resultado.toString();
    }

    private String getNomePadrao(String amenity) {
        switch (amenity) {
            case "drinking_water":
                return "Bebedouro";
            case "toilets":
                return "Banheiro Público";
            case "pharmacy":
                return "Farmácia";
            case "hospital":
                return "Hospital";
            case "clinic":
                return "Clínica/UBS";
            case "police":
                return "Delegacia";
            case "atm":
                return "Caixa Eletrônico";
            case "bank":
                return "Banco";
            default:
                return "Ponto de Apoio";
        }
    }
}
