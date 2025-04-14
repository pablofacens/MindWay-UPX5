package com.mindway.app.data.api.ia;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeminiApi {

    @POST("models/gemini-2.5-flash:generateContent")
    Call<GeminiResponse> gerarConteudo(
            @Query("key") String chave,
            @Body GeminiRequest body
    );
}
