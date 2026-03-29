package br.com.mindway.servlets;

import br.com.mindway.servicos.ServicoViaCep;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/cep")
public class CepServlet extends HttpServlet {

    private ServicoViaCep servicoViaCep = new ServicoViaCep();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        PrintWriter saida = response.getWriter();

        try {
            String cep = request.getParameter("cep");
            if (cep == null || cep.trim().isEmpty()) {
                response.setStatus(400);
                saida.print("{\"erro\": \"Informe o CEP\"}");
                return;
            }

            String resultado = servicoViaCep.buscarPorCep(cep);
            saida.print(resultado);

        } catch (Exception e) {
            response.setStatus(500);
            saida.print("{\"erro\": \"Erro ao buscar CEP: " + e.getMessage() + "\"}");
        }
    }
}
