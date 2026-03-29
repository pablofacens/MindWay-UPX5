package br.com.mindway.servlets;

import br.com.mindway.modelos.PontoTransporte;
import br.com.mindway.servicos.ServicoTransporte;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/transporte")
public class TransporteServlet extends HttpServlet {

    private ServicoTransporte servicoTransporte = new ServicoTransporte();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        PrintWriter saida = response.getWriter();

        try {
            String lat = request.getParameter("lat");
            String lon = request.getParameter("lon");
            String raio = request.getParameter("raio");

            if (lat == null || lon == null) {
                response.setStatus(400);
                saida.print("{\"erro\": \"Informe as coordenadas (lat, lon)\"}");
                return;
            }

            int raioMetros = 1000; 
            if (raio != null) {
                raioMetros = Integer.parseInt(raio);
            }

            List<PontoTransporte> pontos = servicoTransporte.buscarTransporteProximo(
                    Double.parseDouble(lat),
                    Double.parseDouble(lon),
                    raioMetros);

            saida.print(gson.toJson(pontos));

        } catch (Exception e) {
            response.setStatus(500);
            saida.print("{\"erro\": \"Erro ao buscar transporte: " + e.getMessage() + "\"}");
        }
    }
}
