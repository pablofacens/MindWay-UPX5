package com.mindway.app.domain.usecase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mindway.app.data.api.rota.OSRMResponse;
import com.mindway.app.data.model.Parada;
import com.mindway.app.data.model.PontoApoio;
import com.mindway.app.data.model.Rota;
import com.mindway.app.data.model.TipoRota;
import com.mindway.app.domain.repository.RotaRepository;
import com.mindway.app.domain.repository.TransporteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BuscarRotaRapidaUseCase {

    private static final double TARIFA_ONIBUS_BRL     = 4.40;
    private static final double TARIFA_METRO_BRL      = 5.00;
    private static final double EMISSAO_CO2_G_POR_KM  = 120.0;
    private static final int    RAIO_PARADAS_METROS   = 500;
    private static final int    RAIO_POI_METROS       = 1500;

    private final RotaRepository       rotaRepository;
    private final TransporteRepository transporteRepository;

    public BuscarRotaRapidaUseCase(RotaRepository rotaRepository,
                                    TransporteRepository transporteRepository) {
        this.rotaRepository       = rotaRepository;
        this.transporteRepository = transporteRepository;
    }

    public LiveData<Rota> executar(double latOrigem, double lonOrigem,
                                   double latDestino, double lonDestino) {
        MutableLiveData<Rota> resultado = new MutableLiveData<>();

        rotaRepository.buscarRota("driving", latOrigem, lonOrigem, latDestino, lonDestino,
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

                        transporteRepository.buscarParadasTransitoProximas(
                                latOrigem, lonOrigem, RAIO_PARADAS_METROS,
                                new TransporteRepository.CallbackParadas() {
                                    @Override
                                    public void onSucesso(List<Parada> paradas) {
                                        double custoBRL = calcularCusto(paradas);
                                        double co2      = distanciaKm * EMISSAO_CO2_G_POR_KM;
                                        double[] mid    = meioRota(polilinha, latOrigem, lonOrigem,
                                                                    latDestino, lonDestino);
                                        transporteRepository.buscarPontosApoioProximos(
                                                mid[0], mid[1], RAIO_POI_METROS,
                                                new TransporteRepository.CallbackPontosApoio() {
                                                    @Override
                                                    public void onSucesso(List<PontoApoio> pontos) {
                                                        Rota rota = new Rota(
                                                                UUID.randomUUID().toString(),
                                                                TipoRota.RAPIDA, paradas,
                                                                distanciaKm, tempoMinutos,
                                                                custoBRL, co2,
                                                                null, null,
                                                                new ArrayList<>(), pontos, null);
                                                        rota.setPolilinha(polilinha);
                                                        resultado.postValue(rota);
                                                    }
                                                    @Override
                                                    public void onErro(Throwable e) {
                                                        Rota rota = new Rota(
                                                                UUID.randomUUID().toString(),
                                                                TipoRota.RAPIDA, paradas,
                                                                distanciaKm, tempoMinutos,
                                                                custoBRL, co2,
                                                                null, null,
                                                                new ArrayList<>(), new ArrayList<>(), null);
                                                        rota.setPolilinha(polilinha);
                                                        resultado.postValue(rota);
                                                    }
                                                });
                                    }
                                    @Override
                                    public void onErro(Throwable erro) {
                                        resultado.postValue(rotaSemDados(TipoRota.RAPIDA,
                                                distanciaKm, tempoMinutos,
                                                distanciaKm * EMISSAO_CO2_G_POR_KM, polilinha));
                                    }
                                });
                    }
                    @Override
                    public void onErro(Throwable erro) {
                        resultado.postValue(null);
                    }
                });

        return resultado;
    }

    private double calcularCusto(List<Parada> paradas) {
        boolean metro = false, onibus = false;
        for (Parada p : paradas) {
            if ("metro".equalsIgnoreCase(p.getTipo()))  metro  = true;
            if ("onibus".equalsIgnoreCase(p.getTipo())) onibus = true;
        }
        return (metro ? TARIFA_METRO_BRL : 0.0) + (onibus ? TARIFA_ONIBUS_BRL : 0.0);
    }

    private static Rota rotaSemDados(TipoRota tipo, double dist, int tempo,
                                     double co2, List<double[]> poly) {
        Rota r = new Rota(UUID.randomUUID().toString(), tipo,
                new ArrayList<>(), dist, tempo, 0.0, co2,
                null, null, new ArrayList<>(), new ArrayList<>(), null);
        r.setPolilinha(poly);
        return r;
    }

    static double[] meioRota(List<double[]> poly,
                              double latO, double lonO,
                              double latD, double lonD) {
        if (poly != null && !poly.isEmpty()) return poly.get(poly.size() / 2);
        return new double[]{(latO + latD) / 2, (lonO + lonD) / 2};
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
