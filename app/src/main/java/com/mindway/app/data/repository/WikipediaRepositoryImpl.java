package com.mindway.app.data.repository;

import android.util.Log;

import com.mindway.app.data.api.RetrofitClient;
import com.mindway.app.data.api.wikipedia.WikimediaApi;
import com.mindway.app.data.api.wikipedia.WikimediaGeoResponse;
import com.mindway.app.data.api.wikipedia.WikimediaImageResponse;
import com.mindway.app.data.api.wikipedia.WikipediaApi;
import com.mindway.app.data.api.wikipedia.WikipediaPageResponse;
import com.mindway.app.data.api.wikipedia.WikipediaSearchResponse;
import com.mindway.app.domain.repository.WikipediaRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class WikipediaRepositoryImpl implements WikipediaRepository {

    private static final String TAG = "MindWay-Wikipedia";
    private static final int RAIO_BUSCA_METROS = 150;
    private static final long CACHE_EXPIRACAO_MS = 3600_000;

    private final WikimediaApi wikimediaApi;
    private final WikipediaApi wikipediaApi;

    private final Map<String, CacheEntry> cache = new HashMap<>();

    public WikipediaRepositoryImpl() {
        this.wikimediaApi = RetrofitClient.getWikimediaApi();
        this.wikipediaApi = RetrofitClient.getWikipediaApi();
    }

    @Override
    public void buscarImagemPOI(String nome, double lat, double lon, Callback callback) {
        String chaveCache = nome.toLowerCase().trim();
        CacheEntry cached = cache.get(chaveCache);
        if (cached != null && !cached.expirado()) {
            if (cached.url != null) {
                callback.onSucesso(cached.url, cached.descricao);
            } else {
                callback.onErro(new Exception("Sem imagem (cache)"));
            }
            return;
        }

        new Thread(() -> {
            try {
                String url = tentarWikimediaPorCoordenadas(lat, lon);
                if (url != null) {
                    Log.d(TAG, "Imagem encontrada via Wikimedia Commons: " + nome);
                    cache.put(chaveCache, new CacheEntry(url, null));
                    callback.onSucesso(url, null);
                    return;
                }

                String[] resultado = tentarWikipediaPorNome(nome);
                if (resultado != null) {
                    Log.d(TAG, "Imagem encontrada via Wikipedia: " + nome);
                    cache.put(chaveCache, new CacheEntry(resultado[0], resultado[1]));
                    callback.onSucesso(resultado[0], resultado[1]);
                    return;
                }

                Log.d(TAG, "Nenhuma imagem encontrada para: " + nome);
                cache.put(chaveCache, new CacheEntry(null, null));
                callback.onErro(new Exception("Sem imagem disponível"));

            } catch (Exception e) {
                Log.e(TAG, "Erro ao buscar imagem: " + e.getMessage());
                callback.onErro(e);
            }
        }).start();
    }

    private String tentarWikimediaPorCoordenadas(double lat, double lon) {
        try {
            String coordenadas = lat + "|" + lon;
            Call<WikimediaGeoResponse> call = wikimediaApi.buscarPorCoordenadas(
                    coordenadas, RAIO_BUSCA_METROS);
            Response<WikimediaGeoResponse> response = call.execute();

            if (!response.isSuccessful() || response.body() == null
                    || response.body().query == null
                    || response.body().query.geosearch == null
                    || response.body().query.geosearch.isEmpty()) {
                return null;
            }

            int pageId = response.body().query.geosearch.get(0).pageid;

            Call<WikimediaImageResponse> imgCall = wikimediaApi.buscarImageInfo(pageId);
            Response<WikimediaImageResponse> imgResp = imgCall.execute();

            if (!imgResp.isSuccessful() || imgResp.body() == null
                    || imgResp.body().query == null
                    || imgResp.body().query.pages == null) {
                return null;
            }

            for (WikimediaImageResponse.Page page : imgResp.body().query.pages.values()) {
                if (page.imageinfo != null && !page.imageinfo.isEmpty()) {
                    WikimediaImageResponse.ImageInfo info = page.imageinfo.get(0);
                    return info.thumburl != null ? info.thumburl : info.url;
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Wikimedia Commons falhou: " + e.getMessage());
        }
        return null;
    }

    private String[] tentarWikipediaPorNome(String nome) {
        try {
            String nomeLimpo = limparNomePOI(nome);
            if (nomeLimpo.length() < 3) return null;

            Call<WikipediaSearchResponse> call = wikipediaApi.buscarArtigos(nomeLimpo, 3);
            Response<WikipediaSearchResponse> response = call.execute();

            if (!response.isSuccessful() || response.body() == null
                    || response.body().query == null
                    || response.body().query.search == null
                    || response.body().query.search.isEmpty()) {
                return null;
            }

            List<WikipediaSearchResponse.SearchResult> resultados = response.body().query.search;
            WikipediaSearchResponse.SearchResult melhor = encontrarMelhorMatch(resultados, nome);
            if (melhor == null) return null;

            Call<WikipediaPageResponse> pageCall = wikipediaApi.buscarThumbnail(melhor.pageid);
            Response<WikipediaPageResponse> pageResp = pageCall.execute();

            if (!pageResp.isSuccessful() || pageResp.body() == null
                    || pageResp.body().query == null
                    || pageResp.body().query.pages == null) {
                return null;
            }

            for (WikipediaPageResponse.Page page : pageResp.body().query.pages.values()) {
                if (page.thumbnail != null && page.thumbnail.source != null
                        && page.thumbnail.width >= 100) {
                    String descricao = page.extract != null
                            ? page.extract.substring(0, Math.min(200, page.extract.length()))
                            : null;
                    return new String[]{page.thumbnail.source, descricao};
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Wikipedia falhou: " + e.getMessage());
        }
        return null;
    }

    private WikipediaSearchResponse.SearchResult encontrarMelhorMatch(
            List<WikipediaSearchResponse.SearchResult> resultados, String nomeOriginal) {

        String nomeNorm = nomeOriginal.toLowerCase().trim();
        WikipediaSearchResponse.SearchResult melhor = null;
        double melhorScore = 0;

        for (WikipediaSearchResponse.SearchResult r : resultados) {
            double score = calcularSimilaridade(nomeNorm, r.title.toLowerCase());
            if (score > melhorScore) {
                melhorScore = score;
                melhor = r;
            }
        }

        return melhorScore >= 0.3 ? melhor : null;
    }

    private double calcularSimilaridade(String s1, String s2) {
        if (s1.equals(s2)) return 1.0;
        if (s1.contains(s2) || s2.contains(s1)) return 0.8;

        String[] palavras1 = s1.split("\\s+");
        String[] palavras2 = s2.split("\\s+");

        int intersecao = 0;
        for (String p1 : palavras1) {
            for (String p2 : palavras2) {
                if (p1.equals(p2)) {
                    intersecao++;
                    break;
                }
            }
        }

        int uniao = palavras1.length + palavras2.length - intersecao;
        return uniao > 0 ? (double) intersecao / uniao : 0;
    }

    private String limparNomePOI(String nome) {
        String[] ignorar = {"de", "da", "do", "das", "dos", "o", "a", "os", "as",
                "em", "no", "na", "nos", "nas", "para", "por", "com", "sem"};

        String limpo = nome.replaceAll("[^\\w\\sáàâãéèêíïóôõöúçñ]", "");
        StringBuilder resultado = new StringBuilder();
        for (String palavra : limpo.split("\\s+")) {
            boolean ignorada = false;
            for (String ig : ignorar) {
                if (ig.equalsIgnoreCase(palavra)) {
                    ignorada = true;
                    break;
                }
            }
            if (!ignorada) {
                if (resultado.length() > 0) resultado.append(" ");
                resultado.append(palavra);
            }
        }
        return resultado.toString().trim();
    }

    private static class CacheEntry {
        final String url;
        final String descricao;
        final long timestamp;

        CacheEntry(String url, String descricao) {
            this.url = url;
            this.descricao = descricao;
            this.timestamp = System.currentTimeMillis();
        }

        boolean expirado() {
            return System.currentTimeMillis() - timestamp > CACHE_EXPIRACAO_MS;
        }
    }
}
