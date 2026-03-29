package br.com.mindway.servlets;

import br.com.mindway.servicos.ServicoBusca;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/busca")
public class BuscaServlet extends HttpServlet {

    private ServicoBusca servicoBusca = new ServicoBusca();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.setHeader("Access-Control-Allow-Origin", "*");

        PrintWriter saida = response.getWriter();

        try {
            String texto = request.getParameter("texto");

            if (texto == null || texto.trim().isEmpty()) {
                response.setStatus(400);
                saida.print("{\"erro\": \"Digite algo para buscar\"}");
                return;
            }

            String resultado = servicoBusca.buscarEndereco(texto);
            saida.print(resultado);

        } catch (Exception e) {
            response.setStatus(500);
            saida.print("{\"erro\": \"Erro ao buscar endereço: " + e.getMessage() + "\"}");
        }
    }
}
