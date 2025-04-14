package com.mindway.app.data.repository;

import android.util.Log;

import com.mindway.app.data.api.RetrofitClient;
import com.mindway.app.data.api.bicicleta.CityBikesApi;
import com.mindway.app.data.api.bicicleta.CityBikesResponse;
import com.mindway.app.data.api.overpass.OverpassResponse;
import com.mindway.app.data.model.EstacaoBicicleta;
import com.mindway.app.data.model.Parada;
import com.mindway.app.data.model.PontoApoio;
import com.mindway.app.domain.repository.TransporteRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class TransporteRepositoryImpl implements TransporteRepository {

    private final CityBikesApi cityBikesApi;

    public TransporteRepositoryImpl() {
        this.cityBikesApi = RetrofitClient.getCityBikesApi();
    }

    /**
     * A API SPTrans (Olho Vivo) não oferece busca de paradas por coordenadas —
     * apenas por nome ou linha. Como não há fonte pública de paradas de metrô/bus
     * com suporte a proximidade geográfica integrada neste projeto, retorna lista
     * vazia por enquanto. Para uma implementação completa, integre um feed GTFS
     * do SPTRANS / CPTM ou use a API de geolocalização da SPTrans quando disponível.
     */
    @Override
    public void buscarParadasTransitoProximas(double lat, double lon, int raioMetros,
                                               CallbackParadas callback) {
        callback.onSucesso(new ArrayList<Parada>());
    }

    @Override
    public void buscarEstacoesTemBiciProximas(double lat, double lon, int raioMetros,
                                              CallbackBicicletas callback) {
        cityBikesApi.getRedeTemBici().enqueue(new retrofit2.Callback<CityBikesResponse>() {

            @Override
            public void onResponse(Call<CityBikesResponse> call,
                                   Response<CityBikesResponse> response) {
                if (!response.isSuccessful()
                        || response.body() == null
                        || response.body().network == null
                        || response.body().network.stations == null) {
                    callback.onSucesso(new ArrayList<>());
                    return;
                }

                List<EstacaoBicicleta> proximas = new ArrayList<>();
                for (CityBikesResponse.Station s : response.body().network.stations) {
                    if (distanciaMetros(lat, lon, s.latitude, s.longitude) <= raioMetros) {
                        proximas.add(new EstacaoBicicleta(
                                s.id, s.name,
                                s.latitude, s.longitude,
                                s.freeBikes, s.emptySlots));
                    }
                }
                callback.onSucesso(proximas);
            }

            @Override
            public void onFailure(Call<CityBikesResponse> call, Throwable t) {
                callback.onErro(t);
            }
        });
    }

    @Override
    public void buscarPontosApoioProximos(double lat, double lon, int raioMetros,
                                           CallbackPontosApoio callback) {
        String query = "[out:json][timeout:25];"
                + "(node[\"amenity\"~\"pharmacy|drinking_water|hospital|toilets|bench|fountain\"]"
                + "(around:" + raioMetros + "," + lat + "," + lon + "););"
                + "out body 20;";

        RetrofitClient.getOverpassApi().buscarPontosApoio(query)
                .enqueue(new retrofit2.Callback<OverpassResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<OverpassResponse> call,
                                           retrofit2.Response<OverpassResponse> response) {
                        if (!response.isSuccessful()
                                || response.body() == null
                                || response.body().elements == null) {
                            callback.onSucesso(new ArrayList<>());
                            return;
                        }
                        List<PontoApoio> pontos = new ArrayList<>();
                        for (OverpassResponse.Element e : response.body().elements) {
                            if (e.tags == null) continue;
                            String nome = (e.tags.name != null && !e.tags.name.isEmpty())
                                    ? e.tags.name
                                    : nomePorAmenidade(e.tags.amenity);
                            pontos.add(new PontoApoio(nome, e.tags.amenity, e.lat, e.lon));
                        }
                        Log.d("MindWay", "Pontos apoio encontrados: " + pontos.size());
                        callback.onSucesso(pontos);
                    }

                    @Override
                    public void onFailure(retrofit2.Call<OverpassResponse> call, Throwable t) {
                        Log.e("MindWay", "Overpass falhou: " + t.getMessage());
                        callback.onErro(t);
                    }
                });
    }

    private static String nomePorAmenidade(String amenity) {
        if (amenity == null) return "Ponto de apoio";
        switch (amenity) {
            case "pharmacy":       return "Farmácia";
            case "drinking_water": return "Bebedouro";
            case "hospital":       return "Hospital";
            case "toilets":        return "Banheiro público";
            case "bench":          return "Banco";
            case "fountain":       return "Fonte";
            default:               return amenity;
        }
    }

    /** Distância Haversine entre dois pontos em metros. */
    private static double distanciaMetros(double lat1, double lon1,
                                          double lat2, double lon2) {
        final double R = 6_371_000.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
