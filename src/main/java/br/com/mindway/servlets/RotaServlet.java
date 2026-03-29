package br.com.mindway.servlets;

import br.com.mindway.modelos.Rota;
import br.com.mindway.servicos.ServicoRota;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/rota")
public class RotaServlet extends HttpServlet {

    private ServicoRota servicoRota = new ServicoRota();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        PrintWriter saida = response.getWriter();

        try {

            double latOri = Double.parseDouble(request.getParameter("latOri"));
            double lonOri = Double.parseDouble(request.getParameter("lonOri"));
            double latDest = Double.parseDouble(request.getParameter("latDest"));
            double lonDest = Double.parseDouble(request.getParameter("lonDest"));
            String tipo = request.getParameter("tipo");

            if ("todas".equals(tipo)) {

                Rota[] rotas = servicoRota.calcularTodasRotas(latOri, lonOri, latDest, lonDest);
                saida.print(gson.toJson(rotas));
            } else {

                if (tipo == null)
                    tipo = "foot";
                Rota rota = servicoRota.calcularRota(latOri, lonOri, latDest, lonDest, tipo);

                if (rota == null) {
                    response.setStatus(404);
                    saida.print("{\"erro\": \"Nenhuma rota encontrada\"}");
                    return;
                }

                saida.print(gson.toJson(rota));
            }

        } catch (NumberFormatException e) {
            response.setStatus(400);
            saida.print("{\"erro\": \"Coordenadas inválidas. Use números.\"}");
        } catch (Exception e) {
            response.setStatus(500);
            saida.print("{\"erro\": \"Erro ao calcular rota: " + e.getMessage() + "\"}");
        }
    }
}
