package com.mindway.app.data.api.qualidadear;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenAQApi {

    @GET("measurements")
    Call<OpenAQResponse> getMedicoes(
            @Query("coordinates") String coordenadas,
            @Query("radius") int raioMetros,
            @Query("limit") int limite
    );
}
