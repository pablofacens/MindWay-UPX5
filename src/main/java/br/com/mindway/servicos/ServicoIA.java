package br.com.mindway.servicos;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ServicoIA {

    private static final String API_KEY = "SUA_CHAVE_GEMINI_AQUI";
    private static final String URL_BASE = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    private static final String PROMPT_SISTEMA = "Você é o assistente do MindWay, um aplicativo de mobilidade urbana. "
            + "REGRAS OBRIGATÓRIAS que você DEVE seguir:\n"
            + "1. NUNCA invente informações. Use SOMENTE os dados fornecidos no contexto.\n"
            + "2. Se não tiver um dado, diga 'essa informação não está disponível no momento'.\n"
            + "3. NUNCA crie horários de ônibus, preços ou informações que não estejam nos dados.\n"
            + "4. Responda SEMPRE em português brasileiro, de forma simples e direta.\n"
            + "5. Use emojis para deixar a resposta mais visual e amigável.\n"
            + "6. Seja breve — no máximo 3 parágrafos curtos.\n"
            + "7. Quando recomendar algo, JUSTIFIQUE com base nos dados reais fornecidos.\n"
            + "8. Se o usuário perguntar algo que está fora dos dados, diga que não tem essa informação.\n"
            + "9. NUNCA diga 'baseado nos meus conhecimentos' ou 'eu acredito'. Diga 'de acordo com os dados'.\n"
            + "10. Você é um assistente de MOBILIDADE. Não responda perguntas que não sejam sobre transporte, rotas ou mobilidade urbana.";

    public String analisarRota(String dadosRota, String dadosClima,
            String dadosElevacao, String dadosAr,
            String pontosApoio) throws Exception {

        StringBuilder contexto = new StringBuilder();
        contexto.append("DADOS REAIS DA ROTA (use SOMENTE estes dados para responder):\n\n");

        if (dadosRota != null && !dadosRota.isEmpty()) {
            contexto.append("📍 ROTA:\n").append(dadosRota).append("\n\n");
        }
        if (dadosClima != null && !dadosClima.isEmpty()) {
            contexto.append("☁️ CLIMA ATUAL:\n").append(dadosClima).append("\n\n");
        }
        if (dadosElevacao != null && !dadosElevacao.isEmpty()) {
            contexto.append("⛰️ ELEVAÇÃO:\n").append(dadosElevacao).append("\n\n");
        }
        if (dadosAr != null && !dadosAr.isEmpty()) {
            contexto.append("🌿 QUALIDADE DO AR:\n").append(dadosAr).append("\n\n");
        }
        if (pontosApoio != null && !pontosApoio.isEmpty()) {
            contexto.append("🏥 PONTOS DE APOIO NO CAMINHO:\n").append(pontosApoio).append("\n\n");
        }

        String pergunta = contexto.toString()
                + "\nCom base EXCLUSIVAMENTE nos dados acima, faça uma análise breve da rota. "
                + "Inclua: 1) Qual o melhor meio de transporte e por quê, "
                + "2) Algum cuidado especial (clima, elevação, ar), "
                + "3) Pontos de apoio relevantes no caminho. "
                + "Use SOMENTE os dados fornecidos, NÃO invente nada.";

        return enviarParaGemini(pergunta);
    }

    public String responderPergunta(String perguntaUsuario, String dadosContexto) throws Exception {
        String prompt = "DADOS REAIS DISPONÍVEIS (use SOMENTE estes para responder):\n"
                + dadosContexto + "\n\n"
                + "PERGUNTA DO USUÁRIO: " + perguntaUsuario + "\n\n"
                + "Responda SOMENTE com base nos dados acima. "
                + "Se a resposta não estiver nos dados, diga que essa informação não está disponível.";

        return enviarParaGemini(prompt);
    }

    public String recomendarTransporte(double distanciaKm, String clima,
            String elevacao, String qualidadeAr) throws Exception {
        String prompt = "DADOS REAIS:\n"
                + "- Distância: " + distanciaKm + " km\n"
                + "- Clima: " + (clima != null ? clima : "não disponível") + "\n"
                + "- Elevação: " + (elevacao != null ? elevacao : "não disponível") + "\n"
                + "- Qualidade do ar: " + (qualidadeAr != null ? qualidadeAr : "não disponível") + "\n\n"
                + "Com base EXCLUSIVAMENTE nesses dados reais, recomende o melhor meio de transporte "
                + "(a pé, bike, ônibus ou carro) e explique o porquê de forma breve. "
                + "NÃO invente dados que não foram fornecidos.";

        return enviarParaGemini(prompt);
    }

    private String enviarParaGemini(String mensagemUsuario) throws Exception {
        if (API_KEY.equals("SUA_CHAVE_GEMINI_AQUI")) {
            return gerarRespostaSemIA(mensagemUsuario);
        }

        String urlCompleta = URL_BASE + "?key=" + API_KEY;

        JsonObject corpo = new JsonObject();

        JsonObject systemInstruction = new JsonObject();
        JsonObject systemParts = new JsonObject();
        systemParts.addProperty("text", PROMPT_SISTEMA);
        JsonArray systemPartsArray = new JsonArray();
        systemPartsArray.add(systemParts);
        systemInstruction.add("parts", systemPartsArray);
        corpo.add("system_instruction", systemInstruction);

        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        content.addProperty("role", "user");
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        part.addProperty("text", mensagemUsuario);
        parts.add(part);
        content.add("parts", parts);
        contents.add(content);
        corpo.add("contents", contents);

        JsonObject generationConfig = new JsonObject();
        generationConfig.addProperty("temperature", 0.3); 
        generationConfig.addProperty("maxOutputTokens", 500);
        corpo.add("generationConfig", generationConfig);

        URL url = new URL(urlCompleta);
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
        conexao.setRequestMethod("POST");
        conexao.setRequestProperty("Content-Type", "application/json");
        conexao.setDoOutput(true);
        conexao.setConnectTimeout(15000);
        conexao.setReadTimeout(15000);

        try (OutputStream os = conexao.getOutputStream()) {
            os.write(corpo.toString().getBytes(StandardCharsets.UTF_8));
        }

        int codigo = conexao.getResponseCode();
        if (codigo != 200) {
            return "Desculpe, não consegui gerar a análise no momento. Tente novamente em alguns segundos.";
        }

        BufferedReader leitor = new BufferedReader(
                new InputStreamReader(conexao.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder resposta = new StringBuilder();
        String linha;
        while ((linha = leitor.readLine()) != null) {
            resposta.append(linha);
        }
        leitor.close();

        JsonObject respostaJson = JsonParser.parseString(resposta.toString()).getAsJsonObject();
        JsonArray candidates = respostaJson.getAsJsonArray("candidates");

        if (candidates != null && candidates.size() > 0) {
            JsonObject candidate = candidates.get(0).getAsJsonObject();
            JsonObject contentResp = candidate.getAsJsonObject("content");
            JsonArray partsResp = contentResp.getAsJsonArray("parts");
            return partsResp.get(0).getAsJsonObject().get("text").getAsString();
        }

        return "Não foi possível gerar a análise.";
    }

    private String gerarRespostaSemIA(String contexto) {
        StringBuilder resposta = new StringBuilder();
        resposta.append("🤖 **Análise da Rota** (modo offline)\n\n");

        if (contexto.contains("Distância")) {
            resposta.append("Com base nos dados coletados das APIs, ");
            if (contexto.contains("chuva") || contexto.contains("rain")) {
                resposta.append("está chovendo, então recomendo usar transporte público (ônibus ou metrô). ");
            } else {
                resposta.append("o clima está favorável para atividades ao ar livre. ");
            }
        }

        resposta.append("\n\n💡 *Para análises mais detalhadas com IA, configure a chave do Gemini em ServicoIA.java*");
        resposta.append("\n📝 *Cadastro grátis em: aistudio.google.com/app/apikey*");

        return resposta.toString();
    }
}
