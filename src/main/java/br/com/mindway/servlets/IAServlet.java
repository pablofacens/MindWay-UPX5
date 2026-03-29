package br.com.mindway.servlets;

import br.com.mindway.servicos.ServicoIA;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/ia/*")
public class IAServlet extends HttpServlet {

    private ServicoIA servicoIA = new ServicoIA();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        PrintWriter saida = response.getWriter();

        try {

            StringBuilder body = new StringBuilder();
            BufferedReader leitor = request.getReader();
            String linha;
            while ((linha = leitor.readLine()) != null) {
                body.append(linha);
            }

            JsonObject dados = JsonParser.parseString(body.toString()).getAsJsonObject();

            String pathInfo = request.getPathInfo();

            if ("/analise".equals(pathInfo)) {

                String dadosRota = dados.has("rota") ? dados.get("rota").getAsString() : "";
                String dadosClima = dados.has("clima") ? dados.get("clima").getAsString() : "";
                String dadosElevacao = dados.has("elevacao") ? dados.get("elevacao").getAsString() : "";
                String dadosAr = dados.has("qualidadeAr") ? dados.get("qualidadeAr").getAsString() : "";
                String pontosApoio = dados.has("pontosApoio") ? dados.get("pontosApoio").getAsString() : "";

                String analise = servicoIA.analisarRota(
                        dadosRota, dadosClima, dadosElevacao, dadosAr, pontosApoio);

                JsonObject resultado = new JsonObject();
                resultado.addProperty("analise", analise);
                saida.print(resultado.toString());

            } else if ("/pergunta".equals(pathInfo)) {

                String pergunta = dados.get("pergunta").getAsString();
                String contexto = dados.has("contexto") ? dados.get("contexto").getAsString() : "";

                String resposta_ia = servicoIA.responderPergunta(pergunta, contexto);

                JsonObject resultado = new JsonObject();
                resultado.addProperty("resposta", resposta_ia);
                saida.print(resultado.toString());

            } else {
                response.setStatus(404);
                saida.print("{\"erro\": \"Endpoint não encontrado. Use /api/ia/analise ou /api/ia/pergunta\"}");
            }

        } catch (Exception e) {
            response.setStatus(500);
            saida.print("{\"erro\": \"Erro na IA: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
