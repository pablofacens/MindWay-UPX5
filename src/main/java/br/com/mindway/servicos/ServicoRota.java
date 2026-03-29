package br.com.mindway.servicos;

import br.com.mindway.modelos.Rota;
import br.com.mindway.utilidades.ConexaoApi;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ServicoRota {

    private static final String URL_BASE = "https://router.project-osrm.org/route/v1";

    public Rota calcularRota(double latOrigem, double lonOrigem,
            double latDestino, double lonDestino,
            String tipo) throws Exception {

        String url = URL_BASE + "/" + tipo + "/"
                + lonOrigem + "," + latOrigem + ";"
                + lonDestino + "," + latDestino
                + "?overview=full&geometries=geojson";

        String resposta = ConexaoApi.fazerRequisicaoGet(url);
        JsonObject json = JsonParser.parseString(resposta).getAsJsonObject();

        JsonArray rotas = json.getAsJsonArray("routes");
        if (rotas.size() == 0) {
            return null; 
        }

        JsonObject rotaJson = rotas.get(0).getAsJsonObject();

        double distanciaMetros = rotaJson.get("distance").getAsDouble();
        double distanciaKm = distanciaMetros / 1000.0;

        double duracaoSegundos = rotaJson.get("duration").getAsDouble();
        double duracaoMinutos = duracaoSegundos / 60.0;

        String tipoInterno = converterTipo(tipo);

        Rota rota = new Rota(tipoInterno, distanciaKm, duracaoMinutos);

        JsonObject geometria = rotaJson.getAsJsonObject("geometry");
        rota.setCoordenadasJson(geometria.toString());

        return rota;
    }

    public Rota[] calcularTodasRotas(double latOrigem, double lonOrigem,
            double latDestino, double lonDestino) throws Exception {
        Rota[] rotas = new Rota[3];
        rotas[0] = calcularRota(latOrigem, lonOrigem, latDestino, lonDestino, "foot");
        rotas[1] = calcularRota(latOrigem, lonOrigem, latDestino, lonDestino, "bike");
        rotas[2] = calcularRota(latOrigem, lonOrigem, latDestino, lonDestino, "car");
        return rotas;
    }

    private String converterTipo(String tipoApi) {
        switch (tipoApi) {
            case "foot":
                return "pe";
            case "bike":
                return "bike";
            case "car":
                return "carro";
            default:
                return tipoApi;
        }
    }
}
