package br.com.mindway.servicos;

import br.com.mindway.utilidades.ConexaoApi;

public class ServicoIBGE {

    private static final String URL_BASE = "https://servicodados.ibge.gov.br/api/v1";

    public String buscarMunicipio(String nomeCidade) throws Exception {
        String url = "https://servicodados.ibge.gov.br/api/v1/localidades/municipios";
        return ConexaoApi.fazerRequisicaoGet(url);
    }

    public String buscarIndicadores(String codigoIbge) throws Exception {

        String url = "https://servicodados.ibge.gov.br/api/v1/pesquisas/indicadores/29171/resultados/" + codigoIbge;
        return ConexaoApi.fazerRequisicaoGet(url);
    }

    public String buscarDetalhes(String codigoIbge) throws Exception {
        String url = URL_BASE + "/localidades/municipios/" + codigoIbge;
        return ConexaoApi.fazerRequisicaoGet(url);
    }

    public String listarEstados() throws Exception {
        String url = URL_BASE + "/localidades/estados?orderBy=nome";
        return ConexaoApi.fazerRequisicaoGet(url);
    }
}
