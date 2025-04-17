package com.mindway.app.domain.repository;

import com.mindway.app.data.model.DicaIA;

public interface IARepository {

    void gerarDica(String prompt, Callback callback);

    interface Callback {
        void onSucesso(DicaIA dica);
        void onErro(Throwable erro);
    }
}
