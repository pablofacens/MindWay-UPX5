package com.mindway.app.data.api.overpass;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface OverpassApi {

    @POST("interpreter")
    @FormUrlEncoded
    Call<OverpassResponse> buscarPontosApoio(@Field("data") String query);
}
