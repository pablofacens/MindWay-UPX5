package com.mindway.app.domain.usecase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mindway.app.data.api.rota.OSRMResponse;
import com.mindway.app.data.model.EstacaoBicicleta;
import com.mindway.app.data.model.PontoApoio;
import com.mindway.app.data.model.Rota;
import com.mindway.app.data.model.TipoRota;
import com.mindway.app.domain.repository.RotaRepository;
import com.mindway.app.domain.repository.TransporteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BuscarRotaVerdeUseCase {

    private static final double CUSTO_BRL            = 0.0;
    private static final double EMISSAO_CO2_GRAMAS   = 0.0;
    private static final int    RAIO_ESTACOES_METROS = 2000;
    private static final int    RAIO_POI_METROS      = 1500;

    private final RotaRepository       rotaRepository;
    private final TransporteRepository transporteRepository;

    public BuscarRotaVerdeUseCase(RotaRepository rotaRepository,
                                   TransporteRepository transporteRepository) {
        this.rotaRepository       = rotaRepository;
        this.transporteRepository = transporteRepository;
    }

    public LiveData<Rota> executar(double latOrigem, double lonOrigem,
                                   double latDestino, double lonDestino) {
        MutableLiveData<Rota> resultado = new MutableLiveData<>();

        rotaRepository.buscarRota("bike", latOrigem, lonOrigem, latDestino, lonDestino,
                new RotaRepository.Callback() {
                    @Override
                    public void onSucesso(OSRMResponse resposta) {
                        if (resposta.routes == null || resposta.routes.isEmpty()) {
                            buscarRotaCaminhada(resultado, latOrigem, lonOrigem,
                                               latDestino, lonDestino);
                            return;
                        }
                        OSRMResponse.Route melhorRota = resposta.routes.get(0);
                        double distanciaKm = melhorRota.distance / 1000.0;
                        int tempoMinutos   = (int) Math.ceil(melhorRota.duration / 60.0);
                        List<double[]> polilinha = extrairPolilinha(melhorRota);

                        transporteRepository.buscarEstacoesTemBiciProximas(
                                latOrigem, lonOrigem, RAIO_ESTACOES_METROS,
                                new TransporteRepository.CallbackBicicletas() {
                                    @Override
                                    public void onSucesso(List<EstacaoBicicleta> estacoes) {
                                        double[] mid = BuscarRotaRapidaUseCase.meioRota(
                                                polilinha, latOrigem, lonOrigem, latDestino, lonDestino);
                                        transporteRepository.buscarPontosApoioProximos(
                                                mid[0], mid[1], RAIO_POI_METROS,
                                                new TransporteRepository.CallbackPontosApoio() {
                                                    @Override
                                                    public void onSucesso(List<PontoApoio> pontos) {
                                                        Rota rota = new Rota(
                                                                UUID.randomUUID().toString(),
                                                                TipoRota.VERDE, new ArrayList<>(),
                                                                distanciaKm, tempoMinutos,
                                                                CUSTO_BRL, EMISSAO_CO2_GRAMAS,
                                                                null, null,
                                                                estacoes, pontos, null);
                                                        rota.setPolilinha(polilinha);
                                                        resultado.postValue(rota);
                                                    }
                                                    @Override
                                                    public void onErro(Throwable e) {
                                                        Rota rota = new Rota(
                                                                UUID.randomUUID().toString(),
                                                                TipoRota.VERDE, new ArrayList<>(),
                                                                distanciaKm, tempoMinutos,
                                                                CUSTO_BRL, EMISSAO_CO2_GRAMAS,
                                                                null, null,
                                                                estacoes, new ArrayList<>(), null);
                                                        rota.setPolilinha(polilinha);
                                                        resultado.postValue(rota);
                                                    }
                                                });
                                    }
                                    @Override
                                    public void onErro(Throwable erro) {
                                        Rota rota = new Rota(UUID.randomUUID().toString(),
                                                TipoRota.VERDE, new ArrayList<>(),
                                                distanciaKm, tempoMinutos,
                                                CUSTO_BRL, EMISSAO_CO2_GRAMAS,
                                                null, null,
                                                new ArrayList<>(), new ArrayList<>(), null);
                                        rota.setPolilinha(polilinha);
                                        resultado.postValue(rota);
                                    }
                                });
                    }
                    @Override
                    public void onErro(Throwable erro) {
                        buscarRotaCaminhada(resultado, latOrigem, lonOrigem, latDestino, lonDestino);
                    }
                });

        return resultado;
    }

    private void buscarRotaCaminhada(MutableLiveData<Rota> resultado,
                                     double latOrigem, double lonOrigem,
                                     double latDestino, double lonDestino) {
        rotaRepository.buscarRota("foot", latOrigem, lonOrigem, latDestino, lonDestino,
                new RotaRepository.Callback() {
                    @Override
                    public void onSucesso(OSRMResponse resposta) {
                        if (resposta.routes == null || resposta.routes.isEmpty()) {
                            resultado.postValue(null);
                            return;
                        }
                        OSRMResponse.Route melhorRota = resposta.routes.get(0);
                        double distanciaKm = melhorRota.distance / 1000.0;
                        int tempoMinutos   = (int) Math.ceil(melhorRota.duration / 60.0);
                        List<double[]> polilinha = extrairPolilinha(melhorRota);
                        double[] mid = BuscarRotaRapidaUseCase.meioRota(
                                polilinha, latOrigem, lonOrigem, latDestino, lonDestino);

                        transporteRepository.buscarPontosApoioProximos(
                                mid[0], mid[1], RAIO_POI_METROS,
                                new TransporteRepository.CallbackPontosApoio() {
                                    @Override
                                    public void onSucesso(List<PontoApoio> pontos) {
                                        Rota rota = new Rota(UUID.randomUUID().toString(),
                                                TipoRota.VERDE, new ArrayList<>(),
                                                distanciaKm, tempoMinutos,
                                                CUSTO_BRL, EMISSAO_CO2_GRAMAS,
                                                null, null,
                                                new ArrayList<>(), pontos, null);
                                        rota.setPolilinha(polilinha);
                                        resultado.postValue(rota);
                                    }
                                    @Override
                                    public void onErro(Throwable e) {
                                        Rota rota = new Rota(UUID.randomUUID().toString(),
                                                TipoRota.VERDE, new ArrayList<>(),
                                                distanciaKm, tempoMinutos,
                                                CUSTO_BRL, EMISSAO_CO2_GRAMAS,
                                                null, null,
                                                new ArrayList<>(), new ArrayList<>(), null);
                                        rota.setPolilinha(polilinha);
                                        resultado.postValue(rota);
                                    }
                                });
                    }
                    @Override
                    public void onErro(Throwable erro) {
                        resultado.postValue(null);
                    }
                });
    }

    private static List<double[]> extrairPolilinha(OSRMResponse.Route rota) {
        List<double[]> pontos = new ArrayList<>();
        if (rota.geometry == null || rota.geometry.coordinates == null) return pontos;
        for (List<Double> coord : rota.geometry.coordinates) {
            if (coord.size() >= 2) pontos.add(new double[]{coord.get(1), coord.get(0)});
        }
        return pontos;
    }
}
