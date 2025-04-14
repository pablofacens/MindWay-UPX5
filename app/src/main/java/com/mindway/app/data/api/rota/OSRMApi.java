package com.mindway.app.data.api.rota;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OSRMApi {

    /**
     * @param perfil      Perfil de roteamento: "foot", "bike" ou "driving"
     * @param coordenadas Par ou lista de coordenadas no formato "lon,lat;lon,lat"
     */
    @GET("route/v1/{perfil}/{coordenadas}")
    Call<OSRMResponse> getRota(
            @Path("perfil") String perfil,
            @Path(value = "coordenadas", encoded = true) String coordenadas,
            @Query("overview") String overview,
            @Query("geometries") String geometries
    );
}
