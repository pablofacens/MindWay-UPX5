package com.mindway.app.ui.mapa;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.mindway.app.R;
import com.mindway.app.data.model.ClimaInfo;
import com.mindway.app.data.model.Rota;
import com.mindway.app.data.model.TipoRota;
import com.mindway.app.ui.rota.RotaViewModel;

import java.util.List;

public class SeletorRotaFragment extends BottomSheetDialogFragment {

    private RotaViewModel  rotaViewModel;
    private MapaViewModel  mapaViewModel;

    private MaterialCardView cardRapida;
    private MaterialCardView cardEconomica;
    private MaterialCardView cardVerde;

    public static SeletorRotaFragment newInstance() {
        return new SeletorRotaFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MapaActivity activity = (MapaActivity) context;
        rotaViewModel = new ViewModelProvider(activity, activity.getRotaViewModelFactory())
                .get(RotaViewModel.class);
        mapaViewModel = new ViewModelProvider(activity).get(MapaViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seletor_rota, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardRapida    = view.findViewById(R.id.card_rota_rapida);
        cardEconomica = view.findViewById(R.id.card_rota_economica);
        cardVerde     = view.findViewById(R.id.card_rota_verde);

        // Todos escondidos até os dados chegarem
        cardRapida.setVisibility(View.GONE);
        cardEconomica.setVisibility(View.GONE);
        cardVerde.setVisibility(View.GONE);

        rotaViewModel.rotas.observe(getViewLifecycleOwner(), rotas -> {
            ClimaInfo clima = mapaViewModel.climaAtual.getValue();
            popularCards(rotas, clima);
        });

        mapaViewModel.climaAtual.observe(getViewLifecycleOwner(), clima -> {
            List<Rota> rotas = rotaViewModel.rotas.getValue();
            if (rotas != null) popularCards(rotas, clima);
        });
    }

    private void popularCards(List<Rota> rotas, ClimaInfo clima) {
        if (rotas == null) return;
        for (Rota rota : rotas) {
            MaterialCardView card = cardParaTipo(rota.getTipo());
            if (card == null) continue;
            preencherCard(card, rota, clima);
            card.setVisibility(View.VISIBLE);
        }
    }

    private void preencherCard(MaterialCardView card, Rota rota, ClimaInfo clima) {
        String sufixo = sufixoParaTipo(rota.getTipo());

        // Tempo
        int idTempo = getResId("tempo_" + sufixo);
        if (idTempo != 0) ((TextView) card.findViewById(idTempo))
                .setText(rota.getTempoMinutos() + " min");

        // Custo
        int idCusto = getResId("custo_" + sufixo);
        if (idCusto != 0) ((TextView) card.findViewById(idCusto))
                .setText(rota.getCustoBRL() == 0
                        ? getString(R.string.gratuito)
                        : String.format("R$ %.2f", rota.getCustoBRL()));

        // CO2
        int idCo2 = getResId("co2_" + sufixo);
        if (idCo2 != 0) ((TextView) card.findViewById(idCo2))
                .setText(rota.getEmissaoCO2Gramas() == 0
                        ? "0 g"
                        : String.format("%.0f g", rota.getEmissaoCO2Gramas()));

        // Clima
        int idClima = getResId("clima_" + sufixo);
        if (idClima != 0 && clima != null) {
            TextView tvClima = card.findViewById(idClima);
            tvClima.setText(String.format("%.0f°C %s",
                    clima.getTemperaturaC(), clima.getDescricao()));
            tvClima.setVisibility(View.VISIBLE);
        }

        // Botão Escolher
        int idBtn = getResId("btn_escolher_" + sufixo);
        if (idBtn != 0) card.findViewById(idBtn).setOnClickListener(v -> {
            if (clima != null) rota.setClima(clima);
            if (mapaViewModel.qualidadeArAtual.getValue() != null) {
                rota.setQualidadeAr(mapaViewModel.qualidadeArAtual.getValue());
            }
            rotaViewModel.selecionarRota(rota);
            dismiss();
            abrirDetalheRota();
        });
    }

    private void abrirDetalheRota() {
        if (getActivity() == null) return;
        if (getActivity().getSupportFragmentManager()
                .findFragmentByTag("detalhe_rota") == null) {
            DetalheRotaFragment.newInstance()
                    .show(getActivity().getSupportFragmentManager(), "detalhe_rota");
        }
    }

    @Nullable
    private MaterialCardView cardParaTipo(TipoRota tipo) {
        switch (tipo) {
            case RAPIDA:    return cardRapida;
            case ECONOMICA: return cardEconomica;
            case VERDE:     return cardVerde;
            default:        return null;
        }
    }

    private String sufixoParaTipo(TipoRota tipo) {
        switch (tipo) {
            case RAPIDA:    return "rapida";
            case ECONOMICA: return "economica";
            default:        return "verde";
        }
    }

    private int getResId(String name) {
        return requireContext().getResources()
                .getIdentifier(name, "id", requireContext().getPackageName());
    }
}
