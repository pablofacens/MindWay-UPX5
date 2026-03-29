package br.com.mindway.servicos;

import br.com.mindway.utilidades.ConexaoApi;

public class ServicoWikipedia {

    public String buscarResumo(String nomeLocal) throws Exception {

        String nomeFormatado = nomeLocal.trim().replace(" ", "_");
        String url = "https://pt.wikipedia.org/api/rest_v1/page/summary/"
                + java.net.URLEncoder.encode(nomeFormatado, "UTF-8");

        try {
            return ConexaoApi.fazerRequisicaoGet(url);
        } catch (Exception e) {

            url = "https://en.wikipedia.org/api/rest_v1/page/summary/"
                    + java.net.URLEncoder.encode(nomeFormatado, "UTF-8");
            return ConexaoApi.fazerRequisicaoGet(url);
        }
    }

    public String buscarProximos(double latitude, double longitude, int raioMetros) throws Exception {

        String url = "https://pt.wikipedia.org/w/api.php"
                + "?action=query"
                + "&list=geosearch"
                + "&gscoord=" + latitude + "|" + longitude
                + "&gsradius=" + Math.min(raioMetros, 10000)
                + "&gslimit=10"
                + "&format=json";

        return ConexaoApi.fazerRequisicaoGet(url);
    }
}
