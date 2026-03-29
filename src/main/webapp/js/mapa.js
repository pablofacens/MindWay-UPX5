

let mapa;
let marcadorOrigem = null;
let marcadorDestino = null;
let linhaRota = null;
let marcadoresBikes = [];
let marcadoresTransporte = [];
let marcadoresApoio = [];
let timerBusca = null;

let origemLat = null, origemLon = null;
let destinoLat = null, destinoLon = null;

let ultimaRotaDados = null;
let ultimoClimaDados = null;
let ultimoArDados = null;
let ultimoElevacaoDados = null;
let ultimoApoioDados = null;

const CORES_ROTA = {
    foot: '#4CAF50',
    bike: '#2196F3',
    car: '#FF5722'
};

function iniciarMapa() {
    mapa = L.map('mapa', {
        center: [-23.5505, -46.6333],
        zoom: 13,
        zoomControl: true
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© <a href="https://openstreetmap.org">OpenStreetMap</a> | MindWay 🌱',
        maxZoom: 19
    }).addTo(mapa);

    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            function (pos) {
                let lat = pos.coords.latitude;
                let lon = pos.coords.longitude;
                mapa.setView([lat, lon], 14);
                carregarClima(lat, lon);
                carregarQualidadeAr(lat, lon);
            },
            function () {
                carregarClima(-23.5505, -46.6333);
                carregarQualidadeAr(-23.5505, -46.6333);
            }
        );
    } else {
        carregarClima(-23.5505, -46.6333);
        carregarQualidadeAr(-23.5505, -46.6333);
    }
}

document.addEventListener('DOMContentLoaded', function () {
    iniciarMapa();
    configurarBuscaAutocomplete();
});

function configurarBuscaAutocomplete() {
    document.getElementById('campoOrigem').addEventListener('input', function () {
        buscarSugestoes(this.value, 'sugestoesOrigem', 'origem');
    });
    document.getElementById('campoDestino').addEventListener('input', function () {
        buscarSugestoes(this.value, 'sugestoesDestino', 'destino');
    });
    document.addEventListener('click', function (e) {
        if (!e.target.closest('.campo-busca')) fecharSugestoes();
    });
}

function buscarSugestoes(texto, containerId, tipo) {
    clearTimeout(timerBusca);
    let container = document.getElementById(containerId);

    if (texto.length < 3) {
        container.classList.remove('ativa');
        container.innerHTML = '';
        return;
    }

    timerBusca = setTimeout(function () {
        let url = 'https://nominatim.openstreetmap.org/search'
            + '?q=' + encodeURIComponent(texto)
            + '&format=json&limit=5&countrycodes=br&addressdetails=1';

        fetch(url, { headers: { 'Accept-Language': 'pt-BR' } })
            .then(function (r) { return r.json(); })
            .then(function (resultados) { mostrarSugestoes(resultados, containerId, tipo); })
            .catch(function (erro) { console.error('Erro busca:', erro); });
    }, 400);
}

function mostrarSugestoes(resultados, containerId, tipo) {
    let container = document.getElementById(containerId);
    container.innerHTML = '';

    if (resultados.length === 0) {
        container.classList.remove('ativa');
        return;
    }

    resultados.forEach(function (r) {
        let item = document.createElement('div');
        item.className = 'sugestao-item';
        item.textContent = r.display_name;
        item.addEventListener('click', function () {
            selecionarEndereco(r, tipo);
            container.classList.remove('ativa');
        });
        container.appendChild(item);
    });
    container.classList.add('ativa');
}

function selecionarEndereco(resultado, tipo) {
    let lat = parseFloat(resultado.lat);
    let lon = parseFloat(resultado.lon);
    let nome = resultado.display_name.split(',')[0];

    if (tipo === 'origem') {
        origemLat = lat; origemLon = lon;
        document.getElementById('campoOrigem').value = nome;
        if (marcadorOrigem) mapa.removeLayer(marcadorOrigem);
        marcadorOrigem = L.marker([lat, lon], {
            icon: criarIcone('🟢')
        }).addTo(mapa).bindPopup('<b>📍 Origem:</b> ' + nome);
    } else {
        destinoLat = lat; destinoLon = lon;
        document.getElementById('campoDestino').value = nome;
        if (marcadorDestino) mapa.removeLayer(marcadorDestino);
        marcadorDestino = L.marker([lat, lon], {
            icon: criarIcone('🔴')
        }).addTo(mapa).bindPopup('<b>🏁 Destino:</b> ' + nome);
    }
    mapa.setView([lat, lon], 15);
}

function fecharSugestoes() {
    document.querySelectorAll('.lista-sugestoes').forEach(function (el) {
        el.classList.remove('ativa');
    });
}

function criarIcone(emoji) {
    return L.divIcon({
        html: '<div style="font-size:28px;text-align:center;">' + emoji + '</div>',
        iconSize: [36, 36], iconAnchor: [18, 36], popupAnchor: [0, -36],
        className: 'marcador-custom'
    });
}

function buscarCep() {
    let cep = document.getElementById('campoCep').value.replace(/[^0-9]/g, '');
    if (cep.length !== 8) {
        alert('CEP inválido! Use 8 dígitos.');
        return;
    }

    fetch('https://viacep.com.br/ws/' + cep + '/json/')
        .then(function (r) { return r.json(); })
        .then(function (dados) {
            if (dados.erro) {
                alert('CEP não encontrado!');
                return;
            }

            let endereco = dados.logradouro + ', ' + dados.bairro + ', ' + dados.localidade + ', ' + dados.uf;
            document.getElementById('campoDestino').value = endereco;

            let url = 'https://nominatim.openstreetmap.org/search'
                + '?q=' + encodeURIComponent(endereco)
                + '&format=json&limit=1&countrycodes=br';

            fetch(url)
                .then(function (r) { return r.json(); })
                .then(function (resultados) {
                    if (resultados.length > 0) {
                        selecionarEndereco(resultados[0], 'destino');
                    }
                });
        })
        .catch(function (erro) {
            alert('Erro ao buscar CEP: ' + erro.message);
        });
}

function buscarRota() {
    if (!origemLat || !destinoLat) {
        alert('Selecione a origem e o destino primeiro!');
        return;
    }

    let botao = document.getElementById('botaoBuscar');
    botao.disabled = true;
    botao.textContent = '⏳ Calculando...';

    calcularEMostrarRota('foot');
}

function calcularEMostrarRota(tipo) {
    let url = 'https://router.project-osrm.org/route/v1/' + tipo + '/'
        + origemLon + ',' + origemLat + ';'
        + destinoLon + ',' + destinoLat
        + '?overview=full&geometries=geojson';

    fetch(url)
        .then(function (r) { return r.json(); })
        .then(function (dados) {
            if (dados.routes && dados.routes.length > 0) {
                let rota = dados.routes[0];
                desenharRota(rota, tipo);
                mostrarInfoRota(rota, tipo);

                let distKm = (rota.distance / 1000).toFixed(1);
                let durMin = Math.round(rota.duration / 60);
                ultimaRotaDados = 'Tipo: ' + tipo + ', Distância: ' + distKm + ' km, Duração: ' + durMin + ' min';

                let centroLat = (origemLat + destinoLat) / 2;
                let centroLon = (origemLon + destinoLon) / 2;
                carregarElevacao(rota.geometry.coordinates);
                carregarClima(centroLat, centroLon);
                carregarQualidadeAr(centroLat, centroLon);

                setTimeout(function () { solicitarAnaliseIA(); }, 2500);
            } else {
                alert('Nenhuma rota encontrada!');
            }
        })
        .catch(function (erro) {
            console.error('Erro rota:', erro);
            alert('Erro ao calcular a rota.');
        })
        .finally(function () {
            let botao = document.getElementById('botaoBuscar');
            botao.disabled = false;
            botao.textContent = '🔍 Buscar Rota';
        });
}

function desenharRota(rota, tipo) {
    if (linhaRota) mapa.removeLayer(linhaRota);

    let coordenadas = rota.geometry.coordinates.map(function (c) {
        return [c[1], c[0]];
    });

    linhaRota = L.polyline(coordenadas, {
        color: CORES_ROTA[tipo] || '#4CAF50',
        weight: 6, opacity: 0.8, smoothFactor: 1
    }).addTo(mapa);

    mapa.fitBounds(linhaRota.getBounds(), { padding: [50, 50] });

    document.getElementById('secaoFiltros').style.display = 'block';
    document.getElementById('secaoInfoRota').style.display = 'block';
}

function mostrarInfoRota(rota, tipo) {
    let distKm = (rota.distance / 1000).toFixed(1);
    document.getElementById('infoDistancia').textContent = distKm + ' km';

    let durMin = Math.round(rota.duration / 60);
    if (durMin >= 60) {
        document.getElementById('infoDuracao').textContent = Math.floor(durMin / 60) + 'h ' + (durMin % 60) + 'min';
    } else {
        document.getElementById('infoDuracao').textContent = durMin + ' min';
    }

    let co2Carro = distKm * 120;
    let co2Eco = tipo === 'car' ? 0 : co2Carro;
    document.getElementById('infoCo2').textContent = co2Eco > 1000
        ? (co2Eco / 1000).toFixed(1) + ' kg' : Math.round(co2Eco) + ' g';

    let cal = tipo === 'foot' ? distKm * 50 : (tipo === 'bike' ? distKm * 40 : 0);
    document.getElementById('infoCalorias').textContent = Math.round(cal) + ' kcal';
}

function trocarRota(tipo, botao) {
    document.querySelectorAll('.filtro-botao').forEach(function (b) { b.classList.remove('ativo'); });
    botao.classList.add('ativo');
    calcularEMostrarRota(tipo);
}

function alternarBikes() {
    if (document.getElementById('checkBikes').checked) {
        carregarBikes();
    } else {
        marcadoresBikes.forEach(function (m) { mapa.removeLayer(m); });
        marcadoresBikes = [];
    }
}

function carregarBikes() {
    fetch('https://api.citybik.es/v2/networks')
        .then(function (r) { return r.json(); })
        .then(function (dados) {
            let redesBR = dados.networks.filter(function (n) { return n.location.country === 'BR'; });
            if (redesBR.length === 0) return;
            return fetch('https://api.citybik.es/v2/networks/' + redesBR[0].id);
        })
        .then(function (r) { return r.json(); })
        .then(function (dados) {
            dados.network.stations.forEach(function (est) {
                let bikes = est.free_bikes || 0;
                let vagas = est.empty_slots || 0;
                let temBike = bikes > 0;

                let m = L.marker([est.latitude, est.longitude], {
                    icon: L.divIcon({
                        html: '<div style="font-size:22px;">🚲</div>',
                        iconSize: [28, 28], iconAnchor: [14, 28], className: 'marcador-custom'
                    })
                }).addTo(mapa);

                m.bindPopup(
                    '<div class="popup-marcador">'
                    + '<h3>🚲 ' + est.name + '</h3>'
                    + '<p>Bikes: <b>' + bikes + '</b> | Vagas: <b>' + vagas + '</b></p>'
                    + '<span class="' + (temBike ? 'tag-bikes' : 'tag-bikes tag-sem-bike') + '">'
                    + (temBike ? bikes + ' bikes' : 'Sem bikes') + '</span></div>'
                );
                marcadoresBikes.push(m);
            });
        })
        .catch(function (e) { console.error('Erro bikes:', e); });
}

function alternarTransporte() {
    if (document.getElementById('checkTransporte').checked) {
        carregarTransporte();
    } else {
        marcadoresTransporte.forEach(function (m) { mapa.removeLayer(m); });
        marcadoresTransporte = [];
    }
}

function carregarTransporte() {
    let c = mapa.getCenter();
    let query = '[out:json][timeout:10];'
        + '(node["highway"="bus_stop"](around:2000,' + c.lat + ',' + c.lng + ');'
        + 'node["railway"="station"](around:5000,' + c.lat + ',' + c.lng + ');'
        + 'node["station"="subway"](around:5000,' + c.lat + ',' + c.lng + '););'
        + 'out body 50;';

    fetch('https://overpass-api.de/api/interpreter?data=' + encodeURIComponent(query))
        .then(function (r) { return r.json(); })
        .then(function (dados) {
            dados.elements.forEach(function (el) {
                let nome = (el.tags && el.tags.name) || 'Parada';
                let emoji = '🚌';
                if (el.tags && el.tags.station === 'subway') emoji = '🚇';
                else if (el.tags && el.tags.railway === 'station') emoji = '🚆';

                let m = L.marker([el.lat, el.lon], {
                    icon: L.divIcon({
                        html: '<div style="font-size:20px;">' + emoji + '</div>',
                        iconSize: [26, 26], iconAnchor: [13, 26], className: 'marcador-custom'
                    })
                }).addTo(mapa);

                m.bindPopup('<div class="popup-marcador"><h3>' + emoji + ' ' + nome + '</h3></div>');
                marcadoresTransporte.push(m);
            });
        })
        .catch(function (e) { console.error('Erro transporte:', e); });
}

function alternarPontosApoio() {
    let check = document.getElementById('checkApoio');
    let filtros = document.getElementById('apoioFiltros');

    if (check.checked) {
        filtros.style.display = 'flex';
        carregarPontosApoio();
    } else {
        filtros.style.display = 'none';
        marcadoresApoio.forEach(function (m) { mapa.removeLayer(m); });
        marcadoresApoio = [];
    }
}

function carregarPontosApoio() {
    let c = mapa.getCenter();

    let query = '[out:json][timeout:15];'
        + '('
        + 'node["amenity"="drinking_water"](around:2000,' + c.lat + ',' + c.lng + ');'
        + 'node["amenity"="toilets"](around:2000,' + c.lat + ',' + c.lng + ');'
        + 'node["amenity"="pharmacy"](around:2000,' + c.lat + ',' + c.lng + ');'
        + 'node["amenity"="hospital"](around:3000,' + c.lat + ',' + c.lng + ');'
        + 'node["amenity"="clinic"](around:3000,' + c.lat + ',' + c.lng + ');'
        + 'node["amenity"="police"](around:3000,' + c.lat + ',' + c.lng + ');'
        + 'node["amenity"="atm"](around:2000,' + c.lat + ',' + c.lng + ');'
        + ');'
        + 'out body 80;';

    fetch('https://overpass-api.de/api/interpreter?data=' + encodeURIComponent(query))
        .then(function (r) { return r.json(); })
        .then(function (dados) {

            marcadoresApoio.forEach(function (m) { mapa.removeLayer(m); });
            marcadoresApoio = [];

            let contagem = { bebedouros: 0, banheiros: 0, farmacias: 0, hospitais: 0, delegacias: 0, bancos: 0 };

            dados.elements.forEach(function (el) {
                if (!el.tags || !el.tags.amenity) return;
                let amenity = el.tags.amenity;
                let nome = el.tags.name || getNomePadrao(amenity);
                let info = getInfoApoio(amenity);

                if (!info) return;
                contagem[info.categoria]++;

                let m = L.marker([el.lat, el.lon], {
                    icon: L.divIcon({
                        html: '<div style="font-size:20px;">' + info.emoji + '</div>',
                        iconSize: [26, 26], iconAnchor: [13, 26], className: 'marcador-custom'
                    })
                }).addTo(mapa);

                m.bindPopup(
                    '<div class="popup-marcador">'
                    + '<h3>' + info.emoji + ' ' + nome + '</h3>'
                    + '<p>' + info.descricao + '</p></div>'
                );

                m._categoria = info.categoria;
                marcadoresApoio.push(m);
            });

            ultimoApoioDados = JSON.stringify(contagem);
        })
        .catch(function (e) { console.error('Erro pontos de apoio:', e); });
}

function getNomePadrao(amenity) {
    var nomes = {
        'drinking_water': 'Bebedouro', 'toilets': 'Banheiro Público',
        'pharmacy': 'Farmácia', 'hospital': 'Hospital', 'clinic': 'Clínica/UBS',
        'police': 'Delegacia', 'atm': 'Caixa Eletrônico'
    };
    return nomes[amenity] || 'Ponto de Apoio';
}

function getInfoApoio(amenity) {
    var info = {
        'drinking_water': { emoji: '💧', categoria: 'bebedouros', descricao: 'Água potável gratuita' },
        'toilets': { emoji: '🚻', categoria: 'banheiros', descricao: 'Banheiro público' },
        'pharmacy': { emoji: '💊', categoria: 'farmacias', descricao: 'Farmácia — medicamentos' },
        'hospital': { emoji: '🏥', categoria: 'hospitais', descricao: 'Hospital — emergência 24h' },
        'clinic': { emoji: '🏥', categoria: 'hospitais', descricao: 'Clínica/UBS — atendimento' },
        'police': { emoji: '👮', categoria: 'delegacias', descricao: 'Delegacia de polícia' },
        'atm': { emoji: '🏦', categoria: 'bancos', descricao: 'Caixa eletrônico 24h' }
    };
    return info[amenity] || null;
}

function carregarElevacao(coordenadas) {

    let passo = Math.max(1, Math.floor(coordenadas.length / 30));
    let pontosSelecionados = [];
    for (let i = 0; i < coordenadas.length; i += passo) {
        pontosSelecionados.push(coordenadas[i]);
    }

    let locations = pontosSelecionados.map(function (c) {
        return c[1] + ',' + c[0]; 
    }).join('|');

    let url = 'https://api.open-elevation.com/api/v1/lookup?locations=' + locations;

    fetch(url)
        .then(function (r) { return r.json(); })
        .then(function (dados) {
            if (!dados.results || dados.results.length === 0) return;

            let elevacoes = dados.results.map(function (r) { return r.elevation; });
            let maxElev = Math.max.apply(null, elevacoes);
            let minElev = Math.min.apply(null, elevacoes);

            let ganho = 0, perda = 0;
            for (let i = 1; i < elevacoes.length; i++) {
                let dif = elevacoes[i] - elevacoes[i - 1];
                if (dif > 0) ganho += dif;
                else perda += Math.abs(dif);
            }

            let dificuldade, classe, dica;
            if (ganho < 50) {
                dificuldade = 'Fácil'; classe = 'dificuldade-facil';
                dica = '✅ Terreno plano, ótimo para caminhada ou bike.';
            } else if (ganho < 150) {
                dificuldade = 'Moderado'; classe = 'dificuldade-moderado';
                dica = '⚠️ Algumas subidas. Leve água e vá no seu ritmo.';
            } else {
                dificuldade = 'Difícil'; classe = 'dificuldade-dificil';
                dica = '🔴 Muitas subidas! Considere transporte público em parte.';
            }

            ultimoElevacaoDados = 'Ganho: ' + Math.round(ganho) + 'm, Perda: ' + Math.round(perda)
                + 'm, Max: ' + Math.round(maxElev) + 'm, Min: ' + Math.round(minElev)
                + 'm, Dificuldade: ' + dificuldade;

            let barrasHtml = '';
            for (let i = 0; i < elevacoes.length; i++) {
                let altura = ((elevacoes[i] - minElev) / (maxElev - minElev + 1)) * 100;
                barrasHtml += '<div class="elevacao-ponto" style="height:' + Math.max(4, altura) + '%;" '
                    + 'title="' + Math.round(elevacoes[i]) + 'm"></div>';
            }

            document.getElementById('elevacaoConteudo').innerHTML =
                '<div class="elevacao-barra">' + barrasHtml + '</div>'
                + '<div class="elevacao-info">'
                + '<div class="elevacao-dado"><strong>⬆ ' + Math.round(ganho) + 'm</strong>Subida</div>'
                + '<div class="elevacao-dado"><strong>⬇ ' + Math.round(perda) + 'm</strong>Descida</div>'
                + '<div class="elevacao-dado"><strong>🔝 ' + Math.round(maxElev) + 'm</strong>Máxima</div>'
                + '</div>'
                + '<div class="elevacao-dificuldade ' + classe + '">' + dica + '</div>';

            document.getElementById('secaoElevacao').style.display = 'block';
        })
        .catch(function (e) {
            console.error('Erro elevação:', e);
        });
}

function carregarQualidadeAr(lat, lon) {

    let url = 'https://api.waqi.info/feed/geo:' + lat + ';' + lon + '/?token=demo';

    fetch(url)
        .then(function (r) { return r.json(); })
        .then(function (dados) {
            if (dados.status !== 'ok') {
                document.getElementById('arConteudo').innerHTML =
                    '<p style="font-size:13px;color:#666;">Dados de qualidade do ar não disponíveis nesta região.</p>';
                return;
            }

            let aqi = dados.data.aqi;
            let estacao = dados.data.city ? dados.data.city.name : 'Estação local';

            let classe, texto;
            if (aqi <= 50) {
                classe = 'ar-bom';
                texto = '<b>Boa</b> — Ar limpo ✅<br>Ótimo para atividades ao ar livre!';
            } else if (aqi <= 100) {
                classe = 'ar-moderado';
                texto = '<b>Moderada</b> — Aceitável ⚠️<br>Pessoas sensíveis podem ter leve desconforto.';
            } else {
                classe = 'ar-ruim';
                texto = '<b>Ruim</b> — Alerta 🔴<br>Prefira transporte fechado (ônibus, metrô).';
            }

            ultimoArDados = 'AQI: ' + aqi + ', Qualidade: ' + (aqi <= 50 ? 'Boa' : aqi <= 100 ? 'Moderada' : 'Ruim');

            document.getElementById('arConteudo').innerHTML =
                '<div class="ar-index">'
                + '<div class="ar-numero ' + classe + '">' + aqi + '</div>'
                + '<div class="ar-descricao">'
                + '<p>' + texto + '</p>'
                + '<p style="font-size:11px;color:#888;margin-top:4px;">📍 ' + estacao + '</p>'
                + '</div></div>';
        })
        .catch(function (e) {
            console.error('Erro ar:', e);
            document.getElementById('arConteudo').innerHTML =
                '<p style="font-size:13px;color:#666;">Não foi possível carregar dados do ar.</p>';
        });
}

function carregarClima(lat, lon) {
    let apiKey = 'SUA_CHAVE_AQUI';

    if (apiKey === 'SUA_CHAVE_AQUI') {
        document.getElementById('climaConteudo').innerHTML =
            '<p style="font-size:13px;color:#1565C0;">Configure a chave OpenWeather para ver o clima.</p>'
            + '<p style="font-size:11px;color:#64B5F6;margin-top:4px;">Cadastro grátis em openweathermap.org/api</p>';
        ultimoClimaDados = 'Clima não disponível (chave não configurada)';
        return;
    }

    let url = 'https://api.openweathermap.org/data/2.5/weather'
        + '?lat=' + lat + '&lon=' + lon
        + '&appid=' + apiKey + '&units=metric&lang=pt_br';

    fetch(url)
        .then(function (r) { return r.json(); })
        .then(function (d) {
            let temp = Math.round(d.main.temp);
            let desc = d.weather[0].description;
            let icone = d.weather[0].icon;
            let umidade = d.main.humidity;
            let vento = (d.wind.speed * 3.6).toFixed(0);

            let dica = '';
            if (desc.includes('chuva') || desc.includes('rain'))
                dica = '⚠️ Está chovendo! Prefira transporte público.';
            else if (temp > 30) dica = '☀️ Está quente! Leve água e use protetor.';
            else if (temp < 15) dica = '🧥 Está frio! Vista um casaco.';
            else dica = '✅ Clima bom para sair!';

            ultimoClimaDados = 'Temp: ' + temp + '°C, ' + desc + ', Umidade: ' + umidade
                + '%, Vento: ' + vento + 'km/h';

            document.getElementById('climaConteudo').innerHTML =
                '<div class="clima-info">'
                + '<div class="clima-icone"><img src="https://openweathermap.org/img/wn/' + icone + '@2x.png" alt="' + desc + '"></div>'
                + '<div class="clima-dados">'
                + '<div class="clima-temp">' + temp + '°C</div>'
                + '<div class="clima-desc">' + desc + '</div>'
                + '<div class="clima-extra"><span>💧 ' + umidade + '%</span><span>💨 ' + vento + ' km/h</span></div>'
                + '</div></div>'
                + '<div class="clima-dica">' + dica + '</div>';
        })
        .catch(function (e) {
            console.error('Erro clima:', e);
            ultimoClimaDados = 'Erro ao carregar clima';
        });
}

function solicitarAnaliseIA() {
    document.getElementById('secaoIA').style.display = 'block';
    document.getElementById('iaConteudo').innerHTML = '<p class="ia-carregando">🤖 Analisando rota com dados reais...</p>';

    let contexto = montarContextoIA();

    fetch('/MindWayWeb/api/ia/analise', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            rota: ultimaRotaDados || '',
            clima: ultimoClimaDados || '',
            elevacao: ultimoElevacaoDados || '',
            qualidadeAr: ultimoArDados || '',
            pontosApoio: ultimoApoioDados || ''
        })
    })
        .then(function (r) { return r.json(); })
        .then(function (dados) {
            document.getElementById('iaConteudo').innerHTML =
                '<div style="white-space:pre-wrap;">' + (dados.analise || dados.erro) + '</div>';
        })
        .catch(function () {

            gerarAnaliseLocal();
        });
}

function gerarAnaliseLocal() {
    let analise = '🤖 <b>Análise da Rota</b>\n\n';

    if (ultimaRotaDados) {
        analise += '📍 <b>Rota:</b> ' + ultimaRotaDados + '\n';
    }

    if (ultimoClimaDados && ultimoClimaDados.includes('chuva')) {
        analise += '\n⚠️ <b>Atenção:</b> Está chovendo! Recomendo transporte público (ônibus ou metrô).\n';
    } else if (ultimoClimaDados && ultimoClimaDados.includes('Temp')) {
        analise += '\n☁️ <b>Clima:</b> ' + ultimoClimaDados + '\n';
    }

    if (ultimoElevacaoDados) {
        analise += '\n⛰️ <b>Terreno:</b> ' + ultimoElevacaoDados + '\n';
    }

    if (ultimoArDados) {
        analise += '\n🌿 <b>Qualidade do Ar:</b> ' + ultimoArDados + '\n';
    }

    if (ultimoApoioDados) {
        analise += '\n🏥 <b>Pontos de apoio próximos:</b> ' + ultimoApoioDados + '\n';
    }

    analise += '\n💡 <i>Para análises mais detalhadas, configure a chave do Gemini.</i>';

    document.getElementById('iaConteudo').innerHTML =
        '<div style="white-space:pre-wrap;">' + analise + '</div>';
}

function montarContextoIA() {
    let contexto = '';
    if (ultimaRotaDados) contexto += 'ROTA: ' + ultimaRotaDados + '\n';
    if (ultimoClimaDados) contexto += 'CLIMA: ' + ultimoClimaDados + '\n';
    if (ultimoElevacaoDados) contexto += 'ELEVAÇÃO: ' + ultimoElevacaoDados + '\n';
    if (ultimoArDados) contexto += 'QUALIDADE DO AR: ' + ultimoArDados + '\n';
    if (ultimoApoioDados) contexto += 'PONTOS DE APOIO: ' + ultimoApoioDados + '\n';
    return contexto;
}

function enviarPergunta() {
    let input = document.getElementById('chatPergunta');
    let pergunta = input.value.trim();
    if (!pergunta) return;

    let chatDiv = document.getElementById('chatMensagens');

    chatDiv.innerHTML += '<div class="chat-msg chat-usuario">' + pergunta + '</div>';
    input.value = '';

    chatDiv.innerHTML += '<div class="chat-msg chat-ia" id="iaDigitando">🤖 Analisando...</div>';
    chatDiv.scrollTop = chatDiv.scrollHeight;

    let contexto = montarContextoIA();

    fetch('/MindWayWeb/api/ia/pergunta', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pergunta: pergunta, contexto: contexto })
    })
        .then(function (r) { return r.json(); })
        .then(function (dados) {
            let digitando = document.getElementById('iaDigitando');
            if (digitando) digitando.remove();

            chatDiv.innerHTML += '<div class="chat-msg chat-ia">'
                + (dados.resposta || dados.erro)
                + '<span class="chat-aviso">Baseado em dados reais das APIs</span></div>';
            chatDiv.scrollTop = chatDiv.scrollHeight;
        })
        .catch(function () {
            let digitando = document.getElementById('iaDigitando');
            if (digitando) digitando.remove();

            let resposta = '🤖 Desculpe, o assistente IA não está configurado ainda. ';
            if (contexto) {
                resposta += 'Mas aqui estão os dados disponíveis:\n\n' + contexto;
            } else {
                resposta += 'Calcule uma rota primeiro para eu poder ajudar!';
            }

            chatDiv.innerHTML += '<div class="chat-msg chat-ia">' + resposta
                + '<span class="chat-aviso">Modo offline — configure a chave Gemini para respostas inteligentes</span></div>';
            chatDiv.scrollTop = chatDiv.scrollHeight;
        });
}
