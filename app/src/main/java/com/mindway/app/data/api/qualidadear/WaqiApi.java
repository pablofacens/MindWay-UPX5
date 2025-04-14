package com.mindway.app.data.api.qualidadear;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WaqiApi {

    @GET("feed/geo:{lat};{lon}/")
    Call<WaqiResponse> getQualidadeAr(
            @Path("lat") double lat,
            @Path("lon") double lon,
            @Query("token") String token
    );
}
