package br.com.mindway.servlets;

import br.com.mindway.modelos.EstacaoBike;
import br.com.mindway.servicos.ServicoBike;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/bikes")
public class BikeServlet extends HttpServlet {

    private ServicoBike servicoBike = new ServicoBike();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        PrintWriter saida = response.getWriter();

        try {
            String cidade = request.getParameter("cidade");
            if (cidade == null || cidade.trim().isEmpty()) {
                cidade = "São Paulo"; 
            }

            List<EstacaoBike> estacoes = servicoBike.buscarEstacoes(cidade);
            saida.print(gson.toJson(estacoes));

        } catch (Exception e) {
            response.setStatus(500);
            saida.print("{\"erro\": \"Erro ao buscar bikes: " + e.getMessage() + "\"}");
        }
    }
}
