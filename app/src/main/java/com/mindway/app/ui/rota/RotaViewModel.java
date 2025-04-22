package com.mindway.app.ui.rota;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mindway.app.data.model.DicaIA;
import com.mindway.app.data.model.Rota;
import com.mindway.app.domain.usecase.BuscarRotaEconomicaUseCase;
import com.mindway.app.domain.usecase.BuscarRotaRapidaUseCase;
import com.mindway.app.domain.usecase.BuscarRotaVerdeUseCase;
import com.mindway.app.domain.usecase.ObterDicaIAUseCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RotaViewModel extends ViewModel {

    // Mensagem exibida quando nenhuma API retorna resultado — deve coincidir com strings.xml
    private static final String MSG_NENHUMA_ROTA = "Nenhuma rota encontrada";

    private final MediatorLiveData<List<Rota>> _rotas           = new MediatorLiveData<>();
    private final MutableLiveData<Rota>         _rotaSelecionada = new MutableLiveData<>();
    private final MutableLiveData<Boolean>      _carregando      = new MutableLiveData<>(false);
    private final MutableLiveData<String>       _erro            = new MutableLiveData<>();
    private final MediatorLiveData<DicaIA>      _dicaIA          = new MediatorLiveData<>();

    public final LiveData<List<Rota>> rotas           = _rotas;
    public final LiveData<Rota>       rotaSelecionada  = _rotaSelecionada;
    public final LiveData<Boolean>    carregando       = _carregando;
    public final LiveData<String>     erro             = _erro;
    public final LiveData<DicaIA>     dicaIA           = _dicaIA;

    private final BuscarRotaRapidaUseCase    useCaseRapida;
    private final BuscarRotaEconomicaUseCase useCaseEconomica;
    private final BuscarRotaVerdeUseCase     useCaseVerde;
    private final ObterDicaIAUseCase         useCaseIA;

    RotaViewModel(BuscarRotaRapidaUseCase useCaseRapida,
                  BuscarRotaEconomicaUseCase useCaseEconomica,
                  BuscarRotaVerdeUseCase useCaseVerde,
                  ObterDicaIAUseCase useCaseIA) {
        this.useCaseRapida    = useCaseRapida;
        this.useCaseEconomica = useCaseEconomica;
        this.useCaseVerde     = useCaseVerde;
        this.useCaseIA        = useCaseIA;
    }

    public void buscarRotas(double latOrigem, double lonOrigem,
                            double latDestino, double lonDestino) {
        if (Boolean.TRUE.equals(_carregando.getValue())) return;

        Log.d("MindWay", "buscarRotas: origem=" + latOrigem + "," + lonOrigem
                + " destino=" + latDestino + "," + lonDestino);

        _carregando.setValue(true);
        _erro.setValue(null);

        List<Rota> lista        = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger pendentes = new AtomicInteger(3);

        adicionarFonte(useCaseRapida.executar(latOrigem, lonOrigem, latDestino, lonDestino),
                lista, pendentes);
        adicionarFonte(useCaseEconomica.executar(latOrigem, lonOrigem, latDestino, lonDestino),
                lista, pendentes);
        adicionarFonte(useCaseVerde.executar(latOrigem, lonOrigem, latDestino, lonDestino),
                lista, pendentes);
    }

    private void adicionarFonte(LiveData<Rota> fonte,
                                 List<Rota> lista,
                                 AtomicInteger pendentes) {
        _rotas.addSource(fonte, rota -> {
            _rotas.removeSource(fonte);
            if (rota != null) {
                Log.d("MindWay", "Rota recebida: tipo=" + rota.getTipo()
                        + " dist=" + String.format("%.1f", rota.getDistanciaKm()) + "km"
                        + " tempo=" + rota.getTempoMinutos() + "min"
                        + " custo=R$" + String.format("%.2f", rota.getCustoBRL()));
                lista.add(rota);
            } else {
                Log.e("MindWay", "Erro: use case retornou rota nula");
            }
            if (pendentes.decrementAndGet() == 0) {
                if (lista.isEmpty()) {
                    Log.e("MindWay", "Erro: nenhuma rota retornada pelos 3 use cases");
                    _erro.postValue(MSG_NENHUMA_ROTA);
                } else {
                    Log.d("MindWay", "Total de rotas recebidas: " + lista.size());
                    _rotas.postValue(new ArrayList<>(lista));
                }
                _carregando.postValue(false);
            }
        });
    }

    public void selecionarRota(Rota rota) {
        _rotaSelecionada.setValue(rota);
        _dicaIA.setValue(null);
        LiveData<DicaIA> ldDica = useCaseIA.executar(rota);
        _dicaIA.addSource(ldDica, dica -> {
            _dicaIA.removeSource(ldDica);
            _dicaIA.setValue(dica);
        });
    }
}
