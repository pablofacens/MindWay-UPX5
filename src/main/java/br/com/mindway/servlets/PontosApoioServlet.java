package br.com.mindway.servlets;

import br.com.mindway.servicos.ServicoPontosApoio;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/apoio")
public class PontosApoioServlet extends HttpServlet {

    private ServicoPontosApoio servicoPontosApoio = new ServicoPontosApoio();

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
                saida.print("{\"erro\": \"Informe lat e lon\"}");
                return;
            }

            int raioMetros = (raio != null) ? Integer.parseInt(raio) : 1000;

            String resultado = servicoPontosApoio.buscarPontosApoio(
                    Double.parseDouble(lat),
                    Double.parseDouble(lon),
                    raioMetros);

            saida.print(resultado);

        } catch (Exception e) {
            response.setStatus(500);
            saida.print("{\"erro\": \"Erro ao buscar pontos de apoio: " + e.getMessage() + "\"}");
        }
    }
}
