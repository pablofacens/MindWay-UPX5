package com.mindway.app.domain.repository;

import com.mindway.app.data.model.EstacaoBicicleta;
import com.mindway.app.data.model.Parada;
import com.mindway.app.data.model.PontoApoio;

import java.util.List;

public interface TransporteRepository {

    void buscarParadasTransitoProximas(double lat, double lon, int raioMetros,
                                       CallbackParadas callback);

    void buscarEstacoesTemBiciProximas(double lat, double lon, int raioMetros,
                                       CallbackBicicletas callback);

    /** Busca pontos de apoio (farmácias, bebedouros, banheiros…) via Overpass/OSM. */
    void buscarPontosApoioProximos(double lat, double lon, int raioMetros,
                                    CallbackPontosApoio callback);

    interface CallbackParadas {
        void onSucesso(List<Parada> paradas);
        void onErro(Throwable erro);
    }

    interface CallbackBicicletas {
        void onSucesso(List<EstacaoBicicleta> estacoes);
        void onErro(Throwable erro);
    }

    interface CallbackPontosApoio {
        void onSucesso(List<PontoApoio> pontos);
        void onErro(Throwable erro);
    }
}
