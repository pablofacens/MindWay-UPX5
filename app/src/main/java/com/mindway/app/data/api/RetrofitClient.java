package com.mindway.app.data.api;

import com.mindway.app.BuildConfig;

import com.mindway.app.data.api.bicicleta.CityBikesApi;
import com.mindway.app.data.api.overpass.OverpassApi;
import com.mindway.app.data.api.clima.OpenWeatherApi;
import com.mindway.app.data.api.geocoding.NominatimApi;
import com.mindway.app.data.api.ia.GeminiApi;
import com.mindway.app.data.api.onibus.SPTransApi;
import com.mindway.app.data.api.qualidadear.WaqiApi;
import com.mindway.app.data.api.rota.OSRMApi;
import com.mindway.app.data.api.wikipedia.WikipediaApi;
import com.mindway.app.data.api.wikipedia.WikimediaApi;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitClient {

    private static final String BASE_URL_OPENWEATHER = "https://api.openweathermap.org/data/2.5/";
    private static final String BASE_URL_WAQI         = "https://api.waqi.info/";
    // SPTrans Olho Vivo só suporta HTTP — não utilizado ativamente (paradas retornam vazio
    // por limitação de API; veja TransporteRepositoryImpl).
    private static final String BASE_URL_SPTRANS     = "http://api.olhovivo.sptrans.com.br/v2.1/";
    private static final String BASE_URL_CITYBIKES   = "https://api.citybik.es/v2/";
    private static final String BASE_URL_OSRM        = "https://router.project-osrm.org/";
    private static final String BASE_URL_GEMINI      = "https://generativelanguage.googleapis.com/v1beta/";
    private static final String BASE_URL_NOMINATIM   = "https://nominatim.openstreetmap.org/";
    private static final String BASE_URL_OVERPASS    = "https://overpass-api.de/api/";
    private static final String BASE_URL_WIKIPEDIA   = "https://pt.wikipedia.org/w/";
    private static final String BASE_URL_WIKIMEDIA   = "https://commons.wikimedia.org/w/";

    private static OpenWeatherApi openWeatherApi;
    private static WaqiApi        waqiApi;
    private static SPTransApi     spTransApi;
    private static CityBikesApi   cityBikesApi;
    private static OSRMApi        osrmApi;
    private static GeminiApi      geminiApi;
    private static NominatimApi   nominatimApi;
    private static OverpassApi    overpassApi;
    private static WikipediaApi   wikipediaApi;
    private static WikimediaApi   wikimediaApi;

    private RetrofitClient() {}

    private static OkHttpClient buildHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // Log detalhado apenas em debug — evita exposição de dados em produção
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addInterceptor(logging);
        }
        return builder.build();
    }

    private static Retrofit buildRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(buildHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static OpenWeatherApi getOpenWeatherApi() {
        if (openWeatherApi == null) {
            openWeatherApi = buildRetrofit(BASE_URL_OPENWEATHER).create(OpenWeatherApi.class);
        }
        return openWeatherApi;
    }

    public static WaqiApi getWaqiApi() {
        if (waqiApi == null) {
            waqiApi = buildRetrofit(BASE_URL_WAQI).create(WaqiApi.class);
        }
        return waqiApi;
    }

    public static SPTransApi getSPTransApi() {
        if (spTransApi == null) {
            spTransApi = buildRetrofit(BASE_URL_SPTRANS).create(SPTransApi.class);
        }
        return spTransApi;
    }

    public static CityBikesApi getCityBikesApi() {
        if (cityBikesApi == null) {
            cityBikesApi = buildRetrofit(BASE_URL_CITYBIKES).create(CityBikesApi.class);
        }
        return cityBikesApi;
    }

    public static OSRMApi getOSRMApi() {
        if (osrmApi == null) {
            osrmApi = buildRetrofit(BASE_URL_OSRM).create(OSRMApi.class);
        }
        return osrmApi;
    }

    public static GeminiApi getGeminiApi() {
        if (geminiApi == null) {
            geminiApi = buildRetrofit(BASE_URL_GEMINI).create(GeminiApi.class);
        }
        return geminiApi;
    }

    public static NominatimApi getNominatimApi() {
        if (nominatimApi == null) {
            // Nominatim exige User-Agent identificando o app (política de uso aceitável)
            OkHttpClient clienteNominatim = new OkHttpClient.Builder()
                    .addInterceptor(chain -> chain.proceed(
                            chain.request().newBuilder()
                                    .header("User-Agent", "MindWayApp/1.0")
                                    .header("Accept-Language", "pt-BR")
                                    .build()))
                    .build();
            nominatimApi = new Retrofit.Builder()
                    .baseUrl(BASE_URL_NOMINATIM)
                    .client(clienteNominatim)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(NominatimApi.class);
        }
        return nominatimApi;
    }

    public static OverpassApi getOverpassApi() {
        if (overpassApi == null) {
            OkHttpClient clienteOverpass = new OkHttpClient.Builder()
                    .addInterceptor(chain -> chain.proceed(
                            chain.request().newBuilder()
                                    .header("User-Agent", "MindWayApp/1.0")
                                    .build()))
                    .build();
            overpassApi = new Retrofit.Builder()
                    .baseUrl(BASE_URL_OVERPASS)
                    .client(clienteOverpass)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(OverpassApi.class);
        }
        return overpassApi;
    }

    public static WikipediaApi getWikipediaApi() {
        if (wikipediaApi == null) {
            OkHttpClient clienteWikipedia = new OkHttpClient.Builder()
                    .addInterceptor(chain -> chain.proceed(
                            chain.request().newBuilder()
                                    .header("User-Agent", "MindWayApp/1.0")
                                    .build()))
                    .build();
            wikipediaApi = new Retrofit.Builder()
                    .baseUrl(BASE_URL_WIKIPEDIA)
                    .client(clienteWikipedia)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(WikipediaApi.class);
        }
        return wikipediaApi;
    }

    public static WikimediaApi getWikimediaApi() {
        if (wikimediaApi == null) {
            OkHttpClient clienteWikimedia = new OkHttpClient.Builder()
                    .addInterceptor(chain -> chain.proceed(
                            chain.request().newBuilder()
                                    .header("User-Agent", "MindWayApp/1.0")
                                    .build()))
                    .build();
            wikimediaApi = new Retrofit.Builder()
                    .baseUrl(BASE_URL_WIKIMEDIA)
                    .client(clienteWikimedia)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(WikimediaApi.class);
        }
        return wikimediaApi;
    }
}
