package com.mindway.app.ui.mapa;

import android.annotation.SuppressLint;
import android.app.Application;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.mindway.app.AppConfig;
import com.mindway.app.data.api.RetrofitClient;
import com.mindway.app.data.api.clima.OpenWeatherApi;
import com.mindway.app.data.api.clima.OpenWeatherResponse;
import com.mindway.app.data.api.qualidadear.WaqiApi;
import com.mindway.app.data.api.qualidadear.WaqiResponse;
import com.mindway.app.data.model.ClimaInfo;
import com.mindway.app.data.model.EstacaoBicicleta;
import com.mindway.app.data.model.QualidadeAr;
import com.mindway.app.data.repository.TransporteRepositoryImpl;
import com.mindway.app.domain.repository.TransporteRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MapaViewModel extends AndroidViewModel {

    private static final int INTERVALO_LOCALIZACAO_MS = 10_000;
    private static final int RAIO_BICICLETAS_METROS   = 1_000;

    private final MutableLiveData<Location>               _localizacaoAtual  = new MutableLiveData<>();
    private final MutableLiveData<List<EstacaoBicicleta>> _bicicletasNoMapa  = new MutableLiveData<>();
    private final MutableLiveData<EstacaoBicicleta>       _estacaoProxima    = new MutableLiveData<>();
    private final MutableLiveData<ClimaInfo>              _climaAtual        = new MutableLiveData<>();
    private final MutableLiveData<QualidadeAr>            _qualidadeArAtual  = new MutableLiveData<>();

    public final LiveData<Location>               localizacaoAtual  = _localizacaoAtual;
    public final LiveData<List<EstacaoBicicleta>> bicicletasNoMapa  = _bicicletasNoMapa;
    public final LiveData<EstacaoBicicleta>       estacaoProxima    = _estacaoProxima;
    public final LiveData<ClimaInfo>              climaAtual        = _climaAtual;
    public final LiveData<QualidadeAr>            qualidadeArAtual  = _qualidadeArAtual;

    private final FusedLocationProviderClient fusedClient;
    private final TransporteRepository        transporteRepository;
    private final OpenWeatherApi              openWeatherApi;
    private final WaqiApi                   waqiApi;

    public MapaViewModel(@NonNull Application application) {
        super(application);
        fusedClient          = LocationServices.getFusedLocationProviderClient(application);
        transporteRepository = new TransporteRepositoryImpl();
        openWeatherApi       = RetrofitClient.getOpenWeatherApi();
        waqiApi              = RetrofitClient.getWaqiApi();
    }

    @SuppressLint("MissingPermission")
    public void iniciarRastreamentoLocalizacao() {
        LocationRequest request = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, INTERVALO_LOCALIZACAO_MS)
                .setMinUpdateIntervalMillis(5_000)
                .build();

        fusedClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());

        fusedClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) processarLocalizacao(location);
        });
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult result) {
            Location loc = result.getLastLocation();
            if (loc != null) processarLocalizacao(loc);
        }
    };

    private void processarLocalizacao(Location location) {
        _localizacaoAtual.postValue(location);
        carregarBicicletas(location.getLatitude(), location.getLongitude());
        carregarClima(location.getLatitude(), location.getLongitude());
        carregarQualidadeAr(location.getLatitude(), location.getLongitude());
    }

    private void carregarBicicletas(double lat, double lon) {
        transporteRepository.buscarEstacoesTemBiciProximas(lat, lon, RAIO_BICICLETAS_METROS,
                new TransporteRepository.CallbackBicicletas() {
                    @Override
                    public void onSucesso(List<EstacaoBicicleta> estacoes) {
                        _bicicletasNoMapa.postValue(estacoes);
                        if (!estacoes.isEmpty()) {
                            // Estação com mais bikes disponíveis como proxy de "mais próxima útil"
                            EstacaoBicicleta melhor = estacoes.get(0);
                            for (EstacaoBicicleta e : estacoes) {
                                if (e.getBicicletasDisponiveis() > melhor.getBicicletasDisponiveis()) {
                                    melhor = e;
                                }
                            }
                            _estacaoProxima.postValue(melhor);
                        }
                    }

                    @Override
                    public void onErro(Throwable erro) {
                        _bicicletasNoMapa.postValue(new ArrayList<>());
                    }
                });
    }

    private void carregarClima(double lat, double lon) {
        openWeatherApi.getClima(lat, lon, AppConfig.OPENWEATHER_API_KEY)
                .enqueue(new retrofit2.Callback<OpenWeatherResponse>() {
                    @Override
                    public void onResponse(Call<OpenWeatherResponse> call,
                                           Response<OpenWeatherResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            _climaAtual.postValue(mapearClima(response.body()));
                        }
                    }

                    @Override
                    public void onFailure(Call<OpenWeatherResponse> call, Throwable t) {}
                });
    }

    private void carregarQualidadeAr(double lat, double lon) {
        waqiApi.getQualidadeAr(lat, lon, "demo")
                .enqueue(new retrofit2.Callback<WaqiResponse>() {
                    @Override
                    public void onResponse(Call<WaqiResponse> call,
                                           Response<WaqiResponse> response) {
                        if (response.isSuccessful() && response.body() != null
                                && "ok".equals(response.body().status)
                                && response.body().data != null) {
                            _qualidadeArAtual.postValue(mapearQualidadeAr(response.body()));
                        }
                    }

                    @Override
                    public void onFailure(Call<WaqiResponse> call, Throwable t) {}
                });
    }

    private ClimaInfo mapearClima(OpenWeatherResponse r) {
        String descricao = (r.weather != null && !r.weather.isEmpty())
                ? r.weather.get(0).description : "";
        String icone = (r.weather != null && !r.weather.isEmpty())
                ? r.weather.get(0).icon : "";
        double tempC    = r.main != null ? r.main.temp : 0;
        int umidade     = r.main != null ? r.main.humidity : 0;
        double ventoKmh = r.wind != null ? r.wind.speed * 3.6 : 0;
        return new ClimaInfo(descricao, tempC, umidade, ventoKmh, icone);
    }

    private QualidadeAr mapearQualidadeAr(WaqiResponse r) {
        int aqi = r.data.aqi;
        double pm25 = 0, pm10 = 0;
        if (r.data.iaqi != null) {
            if (r.data.iaqi.pm25 != null) pm25 = r.data.iaqi.pm25.v;
            if (r.data.iaqi.pm10 != null) pm10 = r.data.iaqi.pm10.v;
        }
        String classificacao = aqi <= 50 ? "Boa" : aqi <= 100 ? "Moderada" : "Ruim";
        return new QualidadeAr(aqi, classificacao, pm25, pm10);
    }

    @Override
    protected void onCleared() {
        fusedClient.removeLocationUpdates(locationCallback);
        super.onCleared();
    }
}
