package br.com.mindway.servicos;

import br.com.mindway.utilidades.ConexaoApi;

public class ServicoViaCep {

    private static final String URL_BASE = "https://viacep.com.br/ws/";

    public String buscarPorCep(String cep) throws Exception {

        String cepLimpo = cep.replaceAll("[^0-9]", "");

        if (cepLimpo.length() != 8) {
            return "{\"erro\": \"CEP deve ter 8 dígitos\"}";
        }

        String url = URL_BASE + cepLimpo + "/json/";
        return ConexaoApi.fazerRequisicaoGet(url);
    }

    public String buscarPorEndereco(String estado, String cidade, String rua) throws Exception {
        String url = URL_BASE + estado + "/" + cidade + "/" + rua + "/json/";
        return ConexaoApi.fazerRequisicaoGet(url);
    }
}
