package com.mindway.app.ui.mapa;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.mindway.app.R;
import com.mindway.app.data.api.RetrofitClient;
import com.mindway.app.data.api.geocoding.NominatimResultado;
import com.mindway.app.data.model.EstacaoBicicleta;
import com.mindway.app.data.model.PontoApoio;
import com.mindway.app.data.model.Rota;
import com.mindway.app.data.model.TipoRota;
import com.mindway.app.data.repository.IARepositoryImpl;
import com.mindway.app.data.repository.RotaRepositoryImpl;
import com.mindway.app.data.repository.TransporteRepositoryImpl;
import com.mindway.app.domain.repository.IARepository;
import com.mindway.app.domain.repository.RotaRepository;
import com.mindway.app.domain.repository.TransporteRepository;
import com.mindway.app.domain.usecase.BuscarRotaEconomicaUseCase;
import com.mindway.app.domain.usecase.BuscarRotaRapidaUseCase;
import com.mindway.app.domain.usecase.BuscarRotaVerdeUseCase;
import com.mindway.app.domain.usecase.ObterDicaIAUseCase;
import com.mindway.app.ui.rota.RotaViewModelFactory;
import com.mindway.app.ui.rota.RotaViewModel;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapaActivity extends AppCompatActivity {

    private static final int      RC_LOCALIZACAO = 1001;
    private static final int      DEBOUNCE_MS    = 400;
    private static final int      MIN_CHARS      = 2;
    private static final GeoPoint CENTRO_SP      = new GeoPoint(-23.5505, -46.6333);

    // Views
    private MapView                   mapView;
    private LinearLayout              painelBusca;
    private TextInputLayout           layoutOrigem;
    private TextInputLayout           layoutDestino;
    private AutoCompleteTextView      campoOrigem;
    private AutoCompleteTextView      campoDestino;
    private MaterialButtonToggleGroup toggleTipoRota;
    private MaterialButton            btnBuscarRota;
    private android.widget.ProgressBar progresso;
    private TextView                  textoClima;
    private FloatingActionButton      fabLocalizacao;

    // ViewModels
    private MapaViewModel        mapaViewModel;
    private RotaViewModel        rotaViewModel;
    private RotaViewModelFactory rotaViewModelFactory;

    // Overlays
    private MyLocationNewOverlay myLocationOverlay;
    private FolderOverlay        overlayBicicletas;
    private FolderOverlay        overlayPontosApoio;
    private Polyline             overlayPolilinha;
    private Marker               marcadorOrigem;
    private Marker               marcadorDestino;

    // Coordenadas
    private double latOrigem = 0, lonOrigem = 0;
    private double latDestino = 0, lonDestino = 0;
    private double latGPS = 0, lonGPS = 0;

    // Estado
    private boolean origemGeocodificada       = false;
    private boolean destinoGeocodificado      = false;
    private boolean buscaPendente             = false;
    private boolean rotaExibida               = false;
    private String  textoOrigemGeocodificado  = null;
    private String  textoDestinoGeocodificado = null;

    // Autocomplete
    private final Handler                   handler          = new Handler(Looper.getMainLooper());
    private Runnable                        runnableOrigem;
    private Runnable                        runnableDestino;
    private SugestaoAdapter                 adapterOrigem;
    private SugestaoAdapter                 adapterDestino;
    private final HashMap<String, double[]> sugestoesOrigem  = new HashMap<>();
    private final HashMap<String, double[]> sugestoesDestino = new HashMap<>();

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_mapa);

        vincularViews();
        aplicarInsets();
        configurarMapa();
        configurarViewModels();
        configurarAutocompletar();
        configurarListeners();
        observarLiveData();
        adicionarPainelInfo();
        solicitarPermissaoLocalizacao();
    }

    @Override protected void onResume() { super.onResume(); mapView.onResume(); }
    @Override protected void onPause()  { super.onPause();  mapView.onPause();  }

    // -------------------------------------------------------------------------
    // Inicialização
    // -------------------------------------------------------------------------

    private void vincularViews() {
        mapView        = findViewById(R.id.mapa);
        painelBusca    = findViewById(R.id.painel_busca);
        layoutOrigem   = findViewById(R.id.layout_origem);
        layoutDestino  = findViewById(R.id.layout_destino);
        campoOrigem    = findViewById(R.id.campo_origem);
        campoDestino   = findViewById(R.id.campo_destino);
        toggleTipoRota = findViewById(R.id.toggle_tipo_rota);
        btnBuscarRota  = findViewById(R.id.btn_buscar_rota);
        progresso      = findViewById(R.id.progresso);
        textoClima     = findViewById(R.id.texto_clima);
        fabLocalizacao = findViewById(R.id.fab_minha_localizacao);
    }

    /** Aplica insets de nav bar E teclado ao painel para evitar sobreposição. */
    private void aplicarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(painelBusca, (v, insets) -> {
            Insets nav = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            float d = getResources().getDisplayMetrics().density;
            int h   = (int)(16 * d);
            int top = (int)(8  * d);
            // Quando teclado visível ime.bottom > nav.bottom, painel sobe
            v.setPadding(h, top, h, h + Math.max(nav.bottom, ime.bottom));
            return insets;
        });
    }

    private void configurarMapa() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(14.0);
        mapView.getController().setCenter(CENTRO_SP);
    }

    private void configurarViewModels() {
        RotaRepository       rotaRepo       = new RotaRepositoryImpl();
        TransporteRepository transporteRepo = new TransporteRepositoryImpl();
        IARepository         iaRepo         = new IARepositoryImpl();

        rotaViewModelFactory = new RotaViewModelFactory(
                new BuscarRotaRapidaUseCase(rotaRepo, transporteRepo),
                new BuscarRotaEconomicaUseCase(rotaRepo, transporteRepo),
                new BuscarRotaVerdeUseCase(rotaRepo, transporteRepo),
                new ObterDicaIAUseCase(iaRepo));

        rotaViewModel = new ViewModelProvider(this, rotaViewModelFactory).get(RotaViewModel.class);
        mapaViewModel = new ViewModelProvider(this).get(MapaViewModel.class);
    }

    private void adicionarPainelInfo() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_painel_info, PainelInfoFragment.newInstance())
                .commit();
    }

    // -------------------------------------------------------------------------
    // Autocomplete — SugestaoAdapter sem filtro interno
    // -------------------------------------------------------------------------

    private void configurarAutocompletar() {
        adapterOrigem  = new SugestaoAdapter(this);
        adapterDestino = new SugestaoAdapter(this);

        campoOrigem.setAdapter(adapterOrigem);
        campoDestino.setAdapter(adapterDestino);

        campoOrigem.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int i, int b, int c) {}
            @Override
            public void afterTextChanged(Editable s) {
                String texto = s.toString().trim();
                if (textoOrigemGeocodificado != null
                        && !texto.equals(textoOrigemGeocodificado)) {
                    origemGeocodificada = false;
                }
                handler.removeCallbacks(runnableOrigem);
                if (texto.length() >= MIN_CHARS) {
                    runnableOrigem = () -> buscarSugestoes(texto, true);
                    handler.postDelayed(runnableOrigem, DEBOUNCE_MS);
                } else {
                    sugestoesOrigem.clear();
                    adapterOrigem.setItens(new ArrayList<>());
                }
            }
        });

        campoDestino.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int i, int b, int c) {}
            @Override
            public void afterTextChanged(Editable s) {
                String texto = s.toString().trim();
                if (textoDestinoGeocodificado != null
                        && !texto.equals(textoDestinoGeocodificado)) {
                    destinoGeocodificado = false;
                }
                handler.removeCallbacks(runnableDestino);
                if (texto.length() >= MIN_CHARS) {
                    runnableDestino = () -> buscarSugestoes(texto, false);
                    handler.postDelayed(runnableDestino, DEBOUNCE_MS);
                } else {
                    sugestoesDestino.clear();
                    adapterDestino.setItens(new ArrayList<>());
                }
            }
        });

        campoOrigem.setOnItemClickListener((parent, view, pos, id) -> {
            String nome = (String) parent.getItemAtPosition(pos);
            double[] coords = sugestoesOrigem.get(nome);
            if (coords != null) {
                latOrigem = coords[0];
                lonOrigem = coords[1];
                textoOrigemGeocodificado = nome;
                origemGeocodificada = true;
                layoutOrigem.setError(null);
                colocarMarcadorOrigem(new GeoPoint(latOrigem, lonOrigem), nome);
                Log.d("MindWay", "Origem (autocomplete): " + latOrigem + ", " + lonOrigem);
            }
            ocultarTeclado();
        });

        campoDestino.setOnItemClickListener((parent, view, pos, id) -> {
            String nome = (String) parent.getItemAtPosition(pos);
            double[] coords = sugestoesDestino.get(nome);
            if (coords != null) {
                latDestino = coords[0];
                lonDestino = coords[1];
                textoDestinoGeocodificado = nome;
                destinoGeocodificado = true;
                layoutDestino.setError(null);
                colocarMarcadorDestino(new GeoPoint(latDestino, lonDestino), nome);
                Log.d("MindWay", "Destino (autocomplete): " + latDestino + ", " + lonDestino);
            }
            ocultarTeclado();
        });
    }

    private void buscarSugestoes(String texto, boolean ehOrigem) {
        RetrofitClient.getNominatimApi()
                .buscarEndereco(texto, "json", "br", 5)
                .enqueue(new Callback<List<NominatimResultado>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<NominatimResultado>> call,
                                           @NonNull Response<List<NominatimResultado>> response) {
                        if (!response.isSuccessful() || response.body() == null) return;
                        List<String> nomes = new ArrayList<>();
                        HashMap<String, double[]> mapa = ehOrigem ? sugestoesOrigem : sugestoesDestino;
                        mapa.clear();
                        for (NominatimResultado r : response.body()) {
                            mapa.put(r.nomeExibicao, new double[]{
                                    Double.parseDouble(r.lat),
                                    Double.parseDouble(r.lon)});
                            nomes.add(r.nomeExibicao);
                        }
                        SugestaoAdapter adapter = ehOrigem ? adapterOrigem : adapterDestino;
                        AutoCompleteTextView campo = ehOrigem ? campoOrigem : campoDestino;
                        adapter.setItens(nomes);
                        if (campo.hasFocus() && !nomes.isEmpty()) campo.showDropDown();
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<NominatimResultado>> call,
                                          @NonNull Throwable t) {
                        Log.e("MindWay", "Autocomplete falhou: " + t.getMessage());
                    }
                });
    }

    // -------------------------------------------------------------------------
    // Listeners
    // -------------------------------------------------------------------------

    private void configurarListeners() {
        campoOrigem.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT
                    || actionId == EditorInfo.IME_ACTION_DONE) {
                campoDestino.requestFocus();
                return true;
            }
            return false;
        });

        campoDestino.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_GO) {
                iniciarBuscaRota();
                return true;
            }
            return false;
        });

        btnBuscarRota.setOnClickListener(v -> iniciarBuscaRota());

        fabLocalizacao.setOnClickListener(v -> {
            double lat = latOrigem != 0 ? latOrigem : latGPS;
            double lon = lonOrigem != 0 ? lonOrigem : lonGPS;
            if (lat != 0) mapView.getController().animateTo(new GeoPoint(lat, lon));
        });
    }

    // -------------------------------------------------------------------------
    // LiveData
    // -------------------------------------------------------------------------

    private void observarLiveData() {
        // GPS apenas armazena lat/lon. NÃO preenche campo nem altera rotaExibida.
        mapaViewModel.localizacaoAtual.observe(this, location -> {
            if (location == null) return;
            latGPS = location.getLatitude();
            lonGPS = location.getLongitude();
            // Centraliza no GPS somente antes de uma rota ser exibida
            if (!rotaExibida) {
                mapView.getController().animateTo(new GeoPoint(latGPS, lonGPS));
            }
        });

        mapaViewModel.bicicletasNoMapa.observe(this, this::exibirEstacoesBicicleta);

        mapaViewModel.climaAtual.observe(this, clima -> {
            if (clima == null || textoClima == null) return;
            textoClima.setText(String.format("%s  %.0f°C  Umidade: %d%%",
                    clima.getDescricao(), clima.getTemperaturaC(), clima.getUmidade()));
            textoClima.setVisibility(View.VISIBLE);
        });

        rotaViewModel.carregando.observe(this, c ->
                progresso.setVisibility(Boolean.TRUE.equals(c) ? View.VISIBLE : View.GONE));

        rotaViewModel.erro.observe(this, msg -> {
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        rotaViewModel.rotas.observe(this, rotas -> {
            if (rotas != null && !rotas.isEmpty()) abrirSeletorRota();
        });

        rotaViewModel.rotaSelecionada.observe(this, rota -> {
            if (rota == null) return;
            rotaExibida = true;
            if (myLocationOverlay != null) myLocationOverlay.disableFollowLocation();
            desenharPolilinha(rota);
            exibirPontosApoio(rota);
        });
    }

    // -------------------------------------------------------------------------
    // Localização GPS
    // -------------------------------------------------------------------------

    private void solicitarPermissaoLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            ativarLocalizacao();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RC_LOCALIZACAO);
        }
    }

    private void ativarLocalizacao() {
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        mapView.getOverlays().add(myLocationOverlay);
        mapView.invalidate();
        mapaViewModel.iniciarRastreamentoLocalizacao();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_LOCALIZACAO
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ativarLocalizacao();
        } else {
            Toast.makeText(this, R.string.permissao_localizacao_negada, Toast.LENGTH_LONG).show();
        }
    }

    // -------------------------------------------------------------------------
    // Geocodificação — Origem
    // -------------------------------------------------------------------------

    private void geocodificarOrigem(String endereco) {
        layoutOrigem.setError(null);
        progresso.setVisibility(View.VISIBLE);
        Log.d("MindWay", "Geocodificando origem: " + endereco);

        RetrofitClient.getNominatimApi()
                .buscarEndereco(endereco, "json", "br", 1)
                .enqueue(new Callback<List<NominatimResultado>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<NominatimResultado>> call,
                                           @NonNull Response<List<NominatimResultado>> response) {
                        progresso.setVisibility(View.GONE);
                        if (response.isSuccessful()
                                && response.body() != null
                                && !response.body().isEmpty()) {
                            NominatimResultado r = response.body().get(0);
                            latOrigem = Double.parseDouble(r.lat);
                            lonOrigem = Double.parseDouble(r.lon);
                            textoOrigemGeocodificado = endereco;
                            origemGeocodificada = true;
                            colocarMarcadorOrigem(new GeoPoint(latOrigem, lonOrigem), r.nomeExibicao);
                            if (buscaPendente) continuarBusca();
                        } else {
                            layoutOrigem.setError(getString(R.string.origem_nao_encontrada));
                            buscaPendente = false;
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<NominatimResultado>> call,
                                          @NonNull Throwable t) {
                        progresso.setVisibility(View.GONE);
                        Toast.makeText(MapaActivity.this,
                                R.string.erro_sem_conexao, Toast.LENGTH_SHORT).show();
                        buscaPendente = false;
                    }
                });
    }

    private void colocarMarcadorOrigem(GeoPoint ponto, String titulo) {
        if (marcadorOrigem != null) mapView.getOverlays().remove(marcadorOrigem);
        marcadorOrigem = new Marker(mapView);
        marcadorOrigem.setPosition(ponto);
        marcadorOrigem.setTitle(titulo);
        marcadorOrigem.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(marcadorOrigem);
        mapView.invalidate();
    }

    // -------------------------------------------------------------------------
    // Geocodificação — Destino
    // -------------------------------------------------------------------------

    private void geocodificarDestino(String endereco) {
        layoutDestino.setError(null);
        progresso.setVisibility(View.VISIBLE);
        Log.d("MindWay", "Geocodificando destino: " + endereco);

        RetrofitClient.getNominatimApi()
                .buscarEndereco(endereco, "json", "br", 1)
                .enqueue(new Callback<List<NominatimResultado>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<NominatimResultado>> call,
                                           @NonNull Response<List<NominatimResultado>> response) {
                        progresso.setVisibility(View.GONE);
                        if (response.isSuccessful()
                                && response.body() != null
                                && !response.body().isEmpty()) {
                            NominatimResultado r = response.body().get(0);
                            latDestino = Double.parseDouble(r.lat);
                            lonDestino = Double.parseDouble(r.lon);
                            textoDestinoGeocodificado = endereco;
                            destinoGeocodificado = true;
                            colocarMarcadorDestino(new GeoPoint(latDestino, lonDestino), r.nomeExibicao);
                            if (buscaPendente) continuarBusca();
                        } else {
                            layoutDestino.setError(getString(R.string.destino_nao_encontrado));
                            buscaPendente = false;
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<NominatimResultado>> call,
                                          @NonNull Throwable t) {
                        progresso.setVisibility(View.GONE);
                        Toast.makeText(MapaActivity.this,
                                R.string.erro_sem_conexao, Toast.LENGTH_SHORT).show();
                        buscaPendente = false;
                    }
                });
    }

    private void colocarMarcadorDestino(GeoPoint ponto, String titulo) {
        if (marcadorDestino != null) mapView.getOverlays().remove(marcadorDestino);
        marcadorDestino = new Marker(mapView);
        marcadorDestino.setPosition(ponto);
        marcadorDestino.setTitle(titulo);
        marcadorDestino.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(marcadorDestino);

        if (origemGeocodificada) {
            List<GeoPoint> pts = new ArrayList<>();
            pts.add(new GeoPoint(latOrigem, lonOrigem));
            pts.add(ponto);
            mapView.zoomToBoundingBox(BoundingBox.fromGeoPoints(pts), true, 120);
        } else {
            mapView.getController().animateTo(ponto);
        }
        mapView.invalidate();
    }

    // -------------------------------------------------------------------------
    // Fluxo de busca
    // -------------------------------------------------------------------------

    private void iniciarBuscaRota() {
        rotaExibida = false;
        buscaPendente = true;
        continuarBusca();
    }

    private void continuarBusca() {
        if (!buscaPendente) return;

        String textoOrigem  = getText(campoOrigem);
        String textoDestino = getText(campoDestino);

        if (!destinoGeocodificado) {
            if (textoDestino.isEmpty()) {
                layoutDestino.setError(getString(R.string.informe_destino));
                buscaPendente = false;
                return;
            }
            geocodificarDestino(textoDestino);
            return;
        }

        if (!origemGeocodificada) {
            if (!textoOrigem.isEmpty()) {
                geocodificarOrigem(textoOrigem);
                return;
            }
            if (latGPS != 0) {
                latOrigem = latGPS;
                lonOrigem = lonGPS;
                origemGeocodificada = true;
            } else {
                layoutOrigem.setError(getString(R.string.aguarde_localizacao));
                buscaPendente = false;
                return;
            }
        }

        buscaPendente = false;
        ocultarTeclado();
        Log.d("MindWay", "Buscando rotas: origem=" + latOrigem + "," + lonOrigem
                + " destino=" + latDestino + "," + lonDestino
                + " tipo=" + tipoSelecionado());
        rotaViewModel.buscarRotas(latOrigem, lonOrigem, latDestino, lonDestino);
    }

    private TipoRota tipoSelecionado() {
        int id = toggleTipoRota.getCheckedButtonId();
        if (id == R.id.btn_tipo_economica) return TipoRota.ECONOMICA;
        if (id == R.id.btn_tipo_verde)     return TipoRota.VERDE;
        return TipoRota.RAPIDA;
    }

    // -------------------------------------------------------------------------
    // Mapa: polilinha
    // -------------------------------------------------------------------------

    private void desenharPolilinha(Rota rota) {
        if (overlayPolilinha != null) mapView.getOverlays().remove(overlayPolilinha);
        List<double[]> pontos = rota.getPolilinha();
        if (pontos == null || pontos.isEmpty()) return;

        overlayPolilinha = new Polyline(mapView);
        overlayPolilinha.setWidth(8f);
        overlayPolilinha.setColor(corParaTipo(rota));

        List<GeoPoint> geoPoints = new ArrayList<>();
        for (double[] p : pontos) geoPoints.add(new GeoPoint(p[0], p[1]));
        overlayPolilinha.setPoints(geoPoints);
        mapView.getOverlays().add(overlayPolilinha);

        mapView.zoomToBoundingBox(BoundingBox.fromGeoPoints(geoPoints), true, 100);
        mapView.invalidate();
    }

    private int corParaTipo(Rota rota) {
        TipoRota tipo = rota.getTipo();
        if (tipo == TipoRota.RAPIDA)    return ContextCompat.getColor(this, R.color.cor_rapida);
        if (tipo == TipoRota.ECONOMICA) return ContextCompat.getColor(this, R.color.cor_economica);
        return ContextCompat.getColor(this, R.color.cor_verde);
    }

    // -------------------------------------------------------------------------
    // Mapa: pontos de apoio + bikes da rota
    // -------------------------------------------------------------------------

    private void exibirPontosApoio(Rota rota) {
        if (overlayPontosApoio != null) mapView.getOverlays().remove(overlayPontosApoio);
        overlayPontosApoio = new FolderOverlay();

        // Bikes da rota verde
        if (rota.getBicicletas() != null) {
            for (EstacaoBicicleta e : rota.getBicicletas()) {
                Marker m = new Marker(mapView);
                m.setPosition(new GeoPoint(e.getLatitude(), e.getLongitude()));
                m.setTitle(e.getNome());
                m.setSubDescription("🚲 " + e.getBicicletasDisponiveis()
                        + getString(R.string.bikes_disponiveis));
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                overlayPontosApoio.add(m);
            }
        }

        // Pontos de apoio OSM (farmácias, bebedouros…)
        if (rota.getPontosApoio() != null) {
            for (PontoApoio p : rota.getPontosApoio()) {
                Marker m = new Marker(mapView);
                m.setPosition(new GeoPoint(p.getLatitude(), p.getLongitude()));
                m.setTitle(p.getNome());
                m.setSubDescription(iconePorTipo(p.getTipo()) + " " + p.getTipo());
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                overlayPontosApoio.add(m);
            }
        }

        mapView.getOverlays().add(overlayPontosApoio);
        mapView.invalidate();
    }

    private static String iconePorTipo(String amenity) {
        if (amenity == null) return "📍";
        switch (amenity) {
            case "pharmacy":       return "💊";
            case "drinking_water": return "💧";
            case "hospital":       return "🏥";
            case "toilets":        return "🚻";
            case "bench":          return "🪑";
            case "fountain":       return "⛲";
            default:               return "📍";
        }
    }

    // -------------------------------------------------------------------------
    // Mapa: estações de bicicleta (overlay global do MapaViewModel)
    // -------------------------------------------------------------------------

    private void exibirEstacoesBicicleta(List<EstacaoBicicleta> estacoes) {
        if (overlayBicicletas != null) mapView.getOverlays().remove(overlayBicicletas);
        overlayBicicletas = new FolderOverlay();
        for (EstacaoBicicleta e : estacoes) {
            Marker m = new Marker(mapView);
            m.setPosition(new GeoPoint(e.getLatitude(), e.getLongitude()));
            m.setTitle(e.getNome());
            m.setSubDescription(e.getBicicletasDisponiveis() + getString(R.string.bikes_disponiveis));
            overlayBicicletas.add(m);
        }
        mapView.getOverlays().add(overlayBicicletas);
        mapView.invalidate();
    }

    // -------------------------------------------------------------------------
    // Fragments
    // -------------------------------------------------------------------------

    private void abrirSeletorRota() {
        if (getSupportFragmentManager().findFragmentByTag("seletor_rota") == null) {
            SeletorRotaFragment.newInstance().show(getSupportFragmentManager(), "seletor_rota");
        }
    }

    public RotaViewModelFactory getRotaViewModelFactory() {
        return rotaViewModelFactory;
    }

    // -------------------------------------------------------------------------
    // Utilidades
    // -------------------------------------------------------------------------

    private String getText(AutoCompleteTextView campo) {
        return campo.getText() != null ? campo.getText().toString().trim() : "";
    }

    private void ocultarTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            View focus = getCurrentFocus();
            if (focus != null) imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
        }
    }

    // -------------------------------------------------------------------------
    // Adapter sem filtro interno — evita que o dropdown suma após exibir
    // -------------------------------------------------------------------------

    static class SugestaoAdapter extends ArrayAdapter<String> {

        private List<String> itens = new ArrayList<>();

        SugestaoAdapter(Context ctx) {
            super(ctx, android.R.layout.simple_dropdown_item_1line);
        }

        void setItens(List<String> novosItens) {
            itens = new ArrayList<>(novosItens);
            notifyDataSetChanged();
        }

        @Override public int    getCount()            { return itens.size(); }
        @Override public String getItem(int position) { return itens.get(position); }
        @Override public long   getItemId(int pos)    { return pos; }

        @Override
        public @NonNull Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults r = new FilterResults();
                    r.values = new ArrayList<>(itens);
                    r.count  = itens.size();
                    return r;
                }
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    // Não chamamos notifyDataSetChanged() aqui para evitar loop infinito.
                    // Os itens já estão corretos via setItens().
                }
            };
        }
    }
}
