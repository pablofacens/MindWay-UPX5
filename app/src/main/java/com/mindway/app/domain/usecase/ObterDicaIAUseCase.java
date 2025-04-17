package com.mindway.app.domain.usecase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mindway.app.data.model.DicaIA;
import com.mindway.app.data.model.Rota;
import com.mindway.app.domain.repository.IARepository;

public class ObterDicaIAUseCase {

    private final IARepository iaRepository;

    public ObterDicaIAUseCase(IARepository iaRepository) {
        this.iaRepository = iaRepository;
    }

    public LiveData<DicaIA> executar(Rota rota) {
        MutableLiveData<DicaIA> resultado = new MutableLiveData<>();

        iaRepository.gerarDica(construirPrompt(rota), new IARepository.Callback() {
            @Override
            public void onSucesso(DicaIA dica) {
                resultado.postValue(dica);
            }

            @Override
            public void onErro(Throwable erro) {
                // Retorna DicaIA vazia para que o fragment saiba que terminou (mesmo com erro)
                resultado.postValue(new DicaIA("", "", System.currentTimeMillis()));
            }
        });

        return resultado;
    }

    private String construirPrompt(Rota rota) {
        StringBuilder sb = new StringBuilder();

        sb.append("Voce e Luna, a assistente inteligente do MindWay.\n\n");
        sb.append("SOBRE O MINDWAY:\n");
        sb.append("MindWay e um app de mobilidade sustentavel em Sao Paulo que promove rotas de bicicleta, caminhada e transporte publico.\n\n");

        sb.append("SUA PERSONALIDADE:\n");
        sb.append("- Amigavel, animada e encorajadora\n");
        sb.append("- Use emojis naturalmente (mas nao exagere)\n");
        sb.append("- Respostas conversacionais e humanizadas\n");
        sb.append("- Tom casual e amigavel em portugues do Brasil\n");
        sb.append("- SEMPRE forneca numeros concretos e precisos\n");
        sb.append("- Destaque economia de CO2 quando relevante\n\n");

        sb.append("CO2 EVITADO:\n");
        sb.append("- Carro emite 120g por km\n");
        sb.append("- Bike/caminhada emite 0g\n");
        sb.append("- Transporte publico emite ~40g por km\n");
        sb.append("- CO2 evitado = (distancia km) x (120g - emissao do modal escolhido)\n\n");

        sb.append("=== ROTA ATUAL ===\n");
        sb.append(String.format("Tipo: %s (%s)\n", rota.getTipo().getNome(), rota.getTipo().getDescricao()));
        sb.append(String.format("Distancia: %.1f km\n", rota.getDistanciaKm()));
        sb.append(String.format("Tempo estimado: %d minutos\n", rota.getTempoMinutos()));
        sb.append(String.format("Custo: R$ %.2f\n", rota.getCustoBRL()));
        sb.append(String.format("Emissao CO2: %.0f g\n", rota.getEmissaoCO2Gramas()));

        if (rota.getClima() != null) {
            sb.append(String.format("\n=== CLIMA ATUAL ===\nTemperatura: %.0f°C\nCondicao: %s\n",
                    rota.getClima().getTemperaturaC(), rota.getClima().getDescricao()));
        }

        if (rota.getPontosApoio() != null && !rota.getPontosApoio().isEmpty()) {
            sb.append(String.format("\nPontos de apoio na rota: %d\n", rota.getPontosApoio().size()));
        }

        if (rota.getBicicletas() != null && !rota.getBicicletas().isEmpty()) {
            sb.append(String.format("Estacoes de bike proximas: %d\n", rota.getBicicletas().size()));
        }

        sb.append("\n=== INSTRUCAO ===\n");
        sb.append("Gere UMA dica objetiva (maximo 4-5 linhas) sobre essa rota.\n");
        sb.append("Use os dados REAIS fornecidos acima, nao invente numeros.\n");
        sb.append("Mencione CO2 evitado, pontos de apoio ou clima quando relevante.\n");
        sb.append("Responda SEMPRE em portugues do Brasil.\n");

        return sb.toString();
    }
}
