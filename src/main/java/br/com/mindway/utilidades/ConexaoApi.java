package br.com.mindway.utilidades;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ConexaoApi {

    public static String fazerRequisicaoGet(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

        conexao.setRequestMethod("GET");
        conexao.setRequestProperty("User-Agent", "MindWay/1.0 (Projeto Faculdade)");
        conexao.setRequestProperty("Accept", "application/json");
        conexao.setConnectTimeout(10000); 
        conexao.setReadTimeout(10000);    

        int codigoResposta = conexao.getResponseCode();
        if (codigoResposta != 200) {
            throw new Exception("Erro na API: código " + codigoResposta + " - URL: " + urlString);
        }

        BufferedReader leitor = new BufferedReader(
                new InputStreamReader(conexao.getInputStream(), StandardCharsets.UTF_8)
        );
        StringBuilder resposta = new StringBuilder();
        String linha;
        while ((linha = leitor.readLine()) != null) {
            resposta.append(linha);
        }
        leitor.close();
        conexao.disconnect();

        return resposta.toString();
    }
}
