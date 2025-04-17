package com.mindway.app.domain.repository;

public interface WikipediaRepository {

    void buscarImagemPOI(String nome, double lat, double lon, Callback callback);

    interface Callback {
        void onSucesso(String urlImagem, String descricao);
        void onErro(Throwable erro);
    }
}
