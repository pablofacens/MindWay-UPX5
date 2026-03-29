package br.com.mindway.servlets;

import br.com.mindway.modelos.Clima;
import br.com.mindway.servicos.ServicoClima;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/clima")
public class ClimaServlet extends HttpServlet {

    private ServicoClima servicoClima = new ServicoClima();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        PrintWriter saida = response.getWriter();

        try {
            Clima clima;
            String cidade = request.getParameter("cidade");
            String lat = request.getParameter("lat");
            String lon = request.getParameter("lon");

            if (cidade != null && !cidade.trim().isEmpty()) {

                clima = servicoClima.buscarClimaPorCidade(cidade);
            } else if (lat != null && lon != null) {

                clima = servicoClima.buscarClimaPorCoordenadas(
                        Double.parseDouble(lat),
                        Double.parseDouble(lon));
            } else {
                response.setStatus(400);
                saida.print("{\"erro\": \"Informe a cidade ou as coordenadas (lat, lon)\"}");
                return;
            }

            String json = gson.toJson(clima);

            saida.print("{\"clima\":" + json + ", \"dica\":\"" + clima.getDica() + "\"}");

        } catch (Exception e) {
            response.setStatus(500);
            saida.print("{\"erro\": \"Erro ao buscar clima: " + e.getMessage() + "\"}");
        }
    }
}
