package com.mindway.app.ui.mapa;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mindway.app.R;

public class PainelInfoFragment extends Fragment {

    private MapaViewModel mapaViewModel;

    private View     pontoAqi;
    private TextView textoAqi;
    private TextView textoTemperatura;
    private TextView textoBikes;
    private TextView textoVento;

    public static PainelInfoFragment newInstance() {
        return new PainelInfoFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapaViewModel = new ViewModelProvider(requireActivity()).get(MapaViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_painel_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textoTemperatura = view.findViewById(R.id.texto_temperatura);
        pontoAqi         = view.findViewById(R.id.ponto_aqi);
        textoAqi         = view.findViewById(R.id.texto_aqi);
        textoBikes       = view.findViewById(R.id.texto_bikes);
        textoVento       = view.findViewById(R.id.texto_vento);

        mapaViewModel.climaAtual.observe(getViewLifecycleOwner(), clima -> {
            if (clima == null) return;
            textoTemperatura.setText(String.format("%.0f°C", clima.getTemperaturaC()));
            textoVento.setText(String.format("%.0f km/h", clima.getVelocidadeVentoKmh()));
        });

        mapaViewModel.qualidadeArAtual.observe(getViewLifecycleOwner(), qa -> {
            if (qa == null) return;
            int aqi = qa.getIndiceAQI();
            ((GradientDrawable) pontoAqi.getBackground()).setColor(corAqi(aqi));
            textoAqi.setText(qa.getClassificacao());
        });

        mapaViewModel.estacaoProxima.observe(getViewLifecycleOwner(), estacao -> {
            if (estacao != null) {
                textoBikes.setText(String.valueOf(estacao.getBicicletasDisponiveis()));
            } else {
                textoBikes.setText(getString(R.string.sem_estacao));
            }
        });
    }

    private int corAqi(int aqi) {
        @ColorRes int resId = aqi <= 50 ? R.color.aqi_bom
                : aqi <= 100 ? R.color.aqi_moderado : R.color.aqi_ruim;
        return ContextCompat.getColor(requireContext(), resId);
    }
}
