package com.mindway.app.data.api.clima;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherApi {

    @GET("weather?units=metric&lang=pt_br")
    Call<OpenWeatherResponse> getClima(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String chave
    );
}
