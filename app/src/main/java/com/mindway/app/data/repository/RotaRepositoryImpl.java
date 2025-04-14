package com.mindway.app.data.repository;

import android.util.Log;

import com.mindway.app.data.api.RetrofitClient;
import com.mindway.app.data.api.rota.OSRMApi;
import com.mindway.app.data.api.rota.OSRMResponse;
import com.mindway.app.domain.repository.RotaRepository;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class RotaRepositoryImpl implements RotaRepository {

    private final OSRMApi osrmApi;

    public RotaRepositoryImpl() {
        this.osrmApi = RetrofitClient.getOSRMApi();
    }

    @Override
    public void buscarRota(String perfil,
                           double latOrigem, double lonOrigem,
                           double latDestino, double lonDestino,
                           Callback callback) {
        // OSRM espera coordenadas no formato lon,lat (longitude primeiro)
        String coordenadas = lonOrigem + "," + latOrigem + ";" + lonDestino + "," + latDestino;

        Log.d("MindWay", "Chamando OSRM: perfil=" + perfil + " coords=" + coordenadas);

        osrmApi.getRota(perfil, coordenadas, "full", "geojson")
                .enqueue(new retrofit2.Callback<OSRMResponse>() {

                    @Override
                    public void onResponse(Call<OSRMResponse> call,
                                           Response<OSRMResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            int nRotas = response.body().routes != null
                                    ? response.body().routes.size() : 0;
                            Log.d("MindWay", "OSRM OK perfil=" + perfil
                                    + " rotas=" + nRotas
                                    + " code=" + response.body().code);
                            callback.onSucesso(response.body());
                        } else {
                            Log.e("MindWay", "Erro OSRM HTTP " + response.code()
                                    + " perfil=" + perfil);
                            callback.onErro(new IOException(
                                    "OSRM retornou HTTP " + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<OSRMResponse> call, Throwable t) {
                        Log.e("MindWay", "Erro OSRM rede perfil=" + perfil
                                + ": " + t.getMessage());
                        callback.onErro(t);
                    }
                });
    }
}
