package com.mindway.app.ui.mapa;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.mindway.app.R;
import com.mindway.app.data.model.EstacaoBicicleta;
import com.mindway.app.data.model.PontoApoio;
import com.mindway.app.data.model.QualidadeAr;
import com.mindway.app.data.model.Rota;
import com.mindway.app.data.model.TipoRota;
import com.mindway.app.data.repository.WikipediaRepositoryImpl;
import com.mindway.app.domain.repository.WikipediaRepository;
import com.mindway.app.ui.rota.RotaViewModel;

import java.util.List;

public class DetalheRotaFragment extends BottomSheetDialogFragment {

    private RotaViewModel rotaViewModel;
    private ParadaAdapter paradaAdapter;
    private final WikipediaRepository wikipediaRepository = new WikipediaRepositoryImpl();

    private ImageView        icTipo;
    private TextView         tituloDetalhe;
    private TextView         statDistancia;
    private TextView         statTempo;
    private TextView         statCusto;
    private TextView         statCo2;
    private MaterialCardView cardClima;
    private TextView         textoTempDetalhe;
    private TextView         textoClimaDetalhe;
    private RecyclerView     listaParadas;
    private TextView         textoSemParadas;
    private LinearLayout     listaPontosApoio;
    private MaterialCardView cardQualidadeAr;
    private View             pontoAqiDetalhe;
    private TextView         textoAqiDetalhe;
    private TextView         textoAqiClasse;
    private MaterialCardView cardDicaIA;
    private TextView         textoDicaIA;
    private TextView         textoCarregandoDica;

    public static DetalheRotaFragment newInstance() {
        return new DetalheRotaFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MapaActivity activity = (MapaActivity) context;
        rotaViewModel = new ViewModelProvider(activity, activity.getRotaViewModelFactory())
                .get(RotaViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detalhe_rota, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vincularViews(view);
        configurarRecyclerView();

        view.findViewById(R.id.btn_fechar).setOnClickListener(v -> dismiss());

        rotaViewModel.rotaSelecionada.observe(getViewLifecycleOwner(), this::preencherRota);

        rotaViewModel.dicaIA.observe(getViewLifecycleOwner(), dica -> {
            if (dica == null) return; // null = ainda carregando; não toca o loading
            textoCarregandoDica.setVisibility(View.GONE);
            if (dica.getTexto() != null && !dica.getTexto().isEmpty()) {
                textoDicaIA.setText(dica.getTexto());
                cardDicaIA.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetBehavior<FrameLayout> behavior = ((BottomSheetDialog) requireDialog()).getBehavior();
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void vincularViews(View view) {
        icTipo              = view.findViewById(R.id.ic_tipo_detalhe);
        tituloDetalhe       = view.findViewById(R.id.titulo_detalhe);
        statDistancia       = view.findViewById(R.id.stat_distancia);
        statTempo           = view.findViewById(R.id.stat_tempo);
        statCusto           = view.findViewById(R.id.stat_custo);
        statCo2             = view.findViewById(R.id.stat_co2);
        cardClima           = view.findViewById(R.id.card_clima);
        textoTempDetalhe    = view.findViewById(R.id.texto_temp_detalhe);
        textoClimaDetalhe   = view.findViewById(R.id.texto_clima_detalhe);
        listaParadas        = view.findViewById(R.id.lista_paradas);
        textoSemParadas     = view.findViewById(R.id.texto_sem_paradas);
        listaPontosApoio    = view.findViewById(R.id.lista_pontos_apoio);
        cardQualidadeAr     = view.findViewById(R.id.card_qualidade_ar);
        pontoAqiDetalhe     = view.findViewById(R.id.ponto_aqi_detalhe);
        textoAqiDetalhe     = view.findViewById(R.id.texto_aqi_detalhe);
        textoAqiClasse      = view.findViewById(R.id.texto_aqi_classe);
        cardDicaIA          = view.findViewById(R.id.card_dica_ia);
        textoDicaIA         = view.findViewById(R.id.texto_dica_ia);
        textoCarregandoDica = view.findViewById(R.id.texto_carregando_dica);
    }

    private void configurarRecyclerView() {
        paradaAdapter = new ParadaAdapter();
        listaParadas.setAdapter(paradaAdapter);
        listaParadas.setLayoutManager(new LinearLayoutManager(requireContext()));
        listaParadas.addItemDecoration(
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }

    private void preencherRota(Rota rota) {
        if (rota == null) return;

        // Cabeçalho
        icTipo.setImageResource(iconeParaTipo(rota.getTipo()));
        icTipo.setBackgroundColor(corParaTipo(rota.getTipo()));
        tituloDetalhe.setText(rota.getTipo().getNome());

        // Stats
        statDistancia.setText(String.format("%.1f", rota.getDistanciaKm()));
        statTempo.setText(String.valueOf(rota.getTempoMinutos()));
        statCusto.setText(rota.getCustoBRL() == 0
                ? getString(R.string.gratuito)
                : String.format("R$%.2f", rota.getCustoBRL()));
        statCo2.setText(String.format("%.0f", rota.getEmissaoCO2Gramas()));

        // Clima na rota
        if (rota.getClima() != null) {
            textoTempDetalhe.setText(String.format("%.0f°C", rota.getClima().getTemperaturaC()));
            textoClimaDetalhe.setText(rota.getClima().getDescricao());
            cardClima.setVisibility(View.VISIBLE);
        }

        // Paradas via RecyclerView
        if (rota.getParadas() != null && !rota.getParadas().isEmpty()) {
            paradaAdapter.setParadas(rota.getParadas());
            listaParadas.setVisibility(View.VISIBLE);
            textoSemParadas.setVisibility(View.GONE);
        } else {
            listaParadas.setVisibility(View.GONE);
            textoSemParadas.setVisibility(View.VISIBLE);
        }

        // Pontos de apoio (bikes + outros)
        listaPontosApoio.removeAllViews();
        boolean temPontos = false;

        if (rota.getBicicletas() != null) {
            for (EstacaoBicicleta e : rota.getBicicletas()) {
                listaPontosApoio.addView(criarLinha(
                        "🚲 " + e.getNome() + " — " + e.getBicicletasDisponiveis()
                                + getString(R.string.bikes_disponiveis)));
                temPontos = true;
            }
        }
        if (rota.getPontosApoio() != null) {
            for (PontoApoio p : rota.getPontosApoio()) {
                View linhaPOI = criarLinhaPOI(p);
                listaPontosApoio.addView(linhaPOI);
                temPontos = true;
            }
        }
        if (!temPontos) {
            listaPontosApoio.addView(criarLinha(getString(R.string.sem_pontos)));
        }

        // Qualidade do ar
        QualidadeAr qa = rota.getQualidadeAr();
        if (qa != null) {
            int aqi = qa.getIndiceAQI();
            ((android.graphics.drawable.GradientDrawable) pontoAqiDetalhe.getBackground())
                    .setColor(corAqi(aqi));
            textoAqiDetalhe.setText("AQI " + aqi);
            textoAqiClasse.setText(qa.getClassificacao());
            cardQualidadeAr.setVisibility(View.VISIBLE);
        }

        // DicaIA — mostra loading enquanto chega
        cardDicaIA.setVisibility(View.GONE);
        textoCarregandoDica.setVisibility(View.VISIBLE);
    }

    private TextView criarLinha(String texto) {
        TextView tv = new TextView(requireContext());
        tv.setText(texto);
        tv.setTextSize(13f);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 6, 0, 6);
        tv.setLayoutParams(p);
        return tv;
    }

    private View criarLinhaPOI(PontoApoio ponto) {
        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        containerParams.setMargins(0, 6, 0, 6);
        container.setLayoutParams(containerParams);

        ImageView imgView = new ImageView(requireContext());
        int sizePx = (int) (48 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(sizePx, sizePx);
        imgParams.setMarginEnd((int) (10 * getResources().getDisplayMetrics().density));
        imgView.setLayoutParams(imgParams);
        imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgView.setVisibility(View.GONE);
        container.addView(imgView);

        TextView tv = new TextView(requireContext());
        tv.setText("📍 " + ponto.getNome());
        tv.setTextSize(13f);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        container.addView(tv);

        wikipediaRepository.buscarImagemPOI(
                ponto.getNome(), ponto.getLatitude(), ponto.getLongitude(),
                new WikipediaRepository.Callback() {
                    @Override
                    public void onSucesso(String urlImagem, String descricao) {
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() -> {
                            imgView.setVisibility(View.VISIBLE);
                            Glide.with(requireContext())
                                    .load(urlImagem)
                                    .transform(new RoundedCorners(
                                            (int) (8 * getResources().getDisplayMetrics().density)))
                                    .into(imgView);
                            if (descricao != null && !descricao.isEmpty()) {
                                String textoCompleto = ponto.getNome();
                                tv.setText("📍 " + textoCompleto);
                            }
                        });
                    }

                    @Override
                    public void onErro(Throwable erro) {
                        // Sem imagem — mantém apenas o texto
                    }
                });

        return container;
    }

    private int corParaTipo(TipoRota tipo) {
        @ColorRes int resId;
        switch (tipo) {
            case RAPIDA:    resId = R.color.cor_rapida;    break;
            case ECONOMICA: resId = R.color.cor_economica; break;
            default:        resId = R.color.cor_verde;     break;
        }
        return ContextCompat.getColor(requireContext(), resId);
    }

    @DrawableRes
    private int iconeParaTipo(TipoRota tipo) {
        switch (tipo) {
            case RAPIDA:    return R.drawable.ic_rota_rapida;
            case ECONOMICA: return R.drawable.ic_rota_economica;
            default:        return R.drawable.ic_rota_verde;
        }
    }

    private int corAqi(int aqi) {
        @ColorRes int resId = aqi <= 50 ? R.color.aqi_bom
                : aqi <= 100 ? R.color.aqi_moderado : R.color.aqi_ruim;
        return ContextCompat.getColor(requireContext(), resId);
    }
}
