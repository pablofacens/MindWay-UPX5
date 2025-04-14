package com.mindway.app.data.repository;

import com.mindway.app.AppConfig;
import com.mindway.app.data.api.RetrofitClient;
import com.mindway.app.data.api.ia.GeminiApi;
import com.mindway.app.data.api.ia.GeminiRequest;
import com.mindway.app.data.api.ia.GeminiResponse;
import com.mindway.app.data.model.DicaIA;
import com.mindway.app.domain.repository.IARepository;

import retrofit2.Call;
import retrofit2.Response;

public class IARepositoryImpl implements IARepository {

    private final GeminiApi geminiApi;

    public IARepositoryImpl() {
        this.geminiApi = RetrofitClient.getGeminiApi();
    }

    @Override
    public void gerarDica(String prompt, Callback callback) {
        geminiApi.gerarConteudo(AppConfig.GEMINI_API_KEY, new GeminiRequest(prompt))
                .enqueue(new retrofit2.Callback<GeminiResponse>() {

                    @Override
                    public void onResponse(Call<GeminiResponse> call,
                                           Response<GeminiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String texto = response.body().getTexto();
                            if (texto != null) {
                                callback.onSucesso(
                                        new DicaIA(texto, "", System.currentTimeMillis()));
                            } else {
                                callback.onErro(new Exception("Resposta vazia da IA"));
                            }
                        } else {
                            callback.onErro(new Exception("Gemini HTTP " + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<GeminiResponse> call, Throwable t) {
                        callback.onErro(t);
                    }
                });
    }
}
