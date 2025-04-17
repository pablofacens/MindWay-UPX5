package com.mindway.app.domain.repository;

import com.mindway.app.data.api.rota.OSRMResponse;

public interface RotaRepository {

    void buscarRota(String perfil,
                    double latOrigem, double lonOrigem,
                    double latDestino, double lonDestino,
                    Callback callback);

    interface Callback {
        void onSucesso(OSRMResponse resposta);
        void onErro(Throwable erro);
    }
}
