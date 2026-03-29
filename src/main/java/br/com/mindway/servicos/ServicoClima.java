package br.com.mindway.servicos;

import br.com.mindway.modelos.Clima;
import br.com.mindway.utilidades.ConexaoApi;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ServicoClima {

    private static final String API_KEY = "SUA_CHAVE_AQUI";
    private static final String URL_BASE = "https://api.openweathermap.org/data/2.5/weather";

    public Clima buscarClimaPorCoordenadas(double latitude, double longitude) throws Exception {
        String url = URL_BASE
                + "?lat=" + latitude
                + "&lon=" + longitude
                + "&appid=" + API_KEY
                + "&units=metric" 
                + "&lang=pt_br"; 

        String resposta = ConexaoApi.fazerRequisicaoGet(url);
        return converterParaClima(resposta);
    }

    public Clima buscarClimaPorCidade(String cidade) throws Exception {
        String url = URL_BASE
                + "?q=" + cidade + ",BR"
                + "&appid=" + API_KEY
                + "&units=metric"
                + "&lang=pt_br";

        String resposta = ConexaoApi.fazerRequisicaoGet(url);
        return converterParaClima(resposta);
    }

    private Clima converterParaClima(String json) {
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        Clima clima = new Clima();

        JsonObject main = obj.getAsJsonObject("main");
        clima.setTemperatura(main.get("temp").getAsDouble());
        clima.setSensacaoTermica(main.get("feels_like").getAsDouble());
        clima.setUmidade(main.get("humidity").getAsInt());

        JsonArray weather = obj.getAsJsonArray("weather");
        if (weather.size() > 0) {
            JsonObject tempo = weather.get(0).getAsJsonObject();
            clima.setDescricao(tempo.get("description").getAsString());
            clima.setIcone(tempo.get("icon").getAsString());
        }

        JsonObject vento = obj.getAsJsonObject("wind");
        double ventoMs = vento.get("speed").getAsDouble();
        clima.setVentoKmh(ventoMs * 3.6); 

        clima.setCidade(obj.get("name").getAsString());

        return clima;
    }
}
