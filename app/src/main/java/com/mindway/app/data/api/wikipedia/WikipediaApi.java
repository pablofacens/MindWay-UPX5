package com.mindway.app.data.api.wikipedia;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WikipediaApi {

    @GET("api.php?action=query&format=json&origin=*&list=search&srprop=snippet")
    Call<WikipediaSearchResponse> buscarArtigos(
            @Query("srsearch") String termo,
            @Query("srlimit") int limite
    );

    @GET("api.php?action=query&format=json&origin=*&prop=pageimages|extracts&pithumbsize=500&exintro=1&explaintext=1")
    Call<WikipediaPageResponse> buscarThumbnail(
            @Query("pageids") int pageId
    );
}
