package com.mindway.app.data.api.wikipedia;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WikimediaApi {

    @GET("api.php?action=query&format=json&origin=*&list=geosearch&gsnamespace=6&gslimit=5")
    Call<WikimediaGeoResponse> buscarPorCoordenadas(
            @Query("gscoord") String coordenadas,
            @Query("gsradius") int raioMetros
    );

    @GET("api.php?action=query&format=json&origin=*&prop=imageinfo&iiprop=url&iiurlwidth=400")
    Call<WikimediaImageResponse> buscarImageInfo(
            @Query("pageids") int pageId
    );
}
