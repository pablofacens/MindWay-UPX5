package com.mindway.app.ui.rota;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mindway.app.domain.usecase.BuscarRotaEconomicaUseCase;
import com.mindway.app.domain.usecase.BuscarRotaRapidaUseCase;
import com.mindway.app.domain.usecase.BuscarRotaVerdeUseCase;
import com.mindway.app.domain.usecase.ObterDicaIAUseCase;

public class RotaViewModelFactory implements ViewModelProvider.Factory {

    private final BuscarRotaRapidaUseCase    useCaseRapida;
    private final BuscarRotaEconomicaUseCase useCaseEconomica;
    private final BuscarRotaVerdeUseCase     useCaseVerde;
    private final ObterDicaIAUseCase         useCaseIA;

    public RotaViewModelFactory(BuscarRotaRapidaUseCase useCaseRapida,
                                 BuscarRotaEconomicaUseCase useCaseEconomica,
                                 BuscarRotaVerdeUseCase useCaseVerde,
                                 ObterDicaIAUseCase useCaseIA) {
        this.useCaseRapida    = useCaseRapida;
        this.useCaseEconomica = useCaseEconomica;
        this.useCaseVerde     = useCaseVerde;
        this.useCaseIA        = useCaseIA;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RotaViewModel.class)) {
            return (T) new RotaViewModel(useCaseRapida, useCaseEconomica, useCaseVerde, useCaseIA);
        }
        throw new IllegalArgumentException("ViewModel desconhecido: " + modelClass.getName());
    }
}
