package com.mindway.app.data.api.onibus;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SPTransApi {

    @POST("Login/Autenticar")
    Call<Boolean> autenticar(@Query("token") String token);

    @GET("Parada/BuscarParadasPorNome")
    Call<List<SPTransParada>> buscarParadasPorNome(
            @Query("termosBusca") String termosBusca
    );

    @GET("Parada/BuscarParadasPorLinha")
    Call<List<SPTransParada>> buscarParadasPorLinha(
            @Query("codigoLinha") int codigoLinha
    );
}
