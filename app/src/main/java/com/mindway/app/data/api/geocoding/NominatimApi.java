package com.mindway.app.data.api.geocoding;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NominatimApi {

    @GET("search")
    Call<List<NominatimResultado>> buscarEndereco(
            @Query("q")            String endereco,
            @Query("format")       String formato,
            @Query("countrycodes") String paises,
            @Query("limit")        int limite
    );
}
