package com.mindway.app.data.api.bicicleta;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CityBikesApi {

    @GET("networks/bikesampa")
    Call<CityBikesResponse> getRedeTemBici();
}
