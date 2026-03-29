<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <!DOCTYPE html>
    <html lang="pt-BR">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>MindWay — Mobilidade Inteligente</title>
        <meta name="description"
            content="MindWay - Sistema inteligente de mobilidade urbana com IA. Rotas, bikes, ônibus, metrô, clima e qualidade do ar.">

        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
        <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap"
            rel="stylesheet">

        <link rel="stylesheet" href="css/estilo.css">
    </head>

    <body>

        <header class="barra-topo">
            <div class="logo">
                <span class="logo-icone">🌱</span>
                <h1>MindWay</h1>
                <span class="logo-subtitulo">Mobilidade Inteligente</span>
            </div>
            <div class="topo-badges">
                <span class="badge-api">10 APIs integradas</span>
                <span class="badge-ia">🤖 IA Integrada</span>
            </div>
        </header>

        <main class="conteudo-principal">

            <aside class="painel-lateral">

                <div class="secao-busca">
                    <h2>📍 Para onde você vai?</h2>

                    <div class="campo-busca">
                        <label for="campoCep">Buscar por CEP:</label>
                        <div class="campo-com-botao">
                            <input type="text" id="campoCep" placeholder="Ex: 01001-000" maxlength="9"
                                autocomplete="off">
                            <button class="botao-cep" onclick="buscarCep()">📮</button>
                        </div>
                    </div>

                    <div class="campo-busca">
                        <label for="campoOrigem">Saindo de:</label>
                        <input type="text" id="campoOrigem" placeholder="Ex: Av. Paulista, 1000" autocomplete="off">
                        <div id="sugestoesOrigem" class="lista-sugestoes"></div>
                    </div>

                    <div class="campo-busca">
                        <label for="campoDestino">Indo para:</label>
                        <input type="text" id="campoDestino" placeholder="Ex: Parque Ibirapuera" autocomplete="off">
                        <div id="sugestoesDestino" class="lista-sugestoes"></div>
                    </div>

                    <button id="botaoBuscar" class="botao-principal" onclick="buscarRota()">
                        🔍 Buscar Rota
                    </button>
                </div>

                <div class="secao-filtros" id="secaoFiltros" style="display:none;">
                    <h2>🛤️ Tipo de Rota</h2>
                    <div class="filtros-rota">
                        <button class="filtro-botao ativo" data-tipo="foot" onclick="trocarRota('foot', this)">
                            🚶 A pé
                        </button>
                        <button class="filtro-botao" data-tipo="bike" onclick="trocarRota('bike', this)">
                            🚴 Bike
                        </button>
                        <button class="filtro-botao" data-tipo="car" onclick="trocarRota('car', this)">
                            🚗 Carro
                        </button>
                    </div>
                </div>

                <div class="secao-info-rota" id="secaoInfoRota" style="display:none;">
                    <h2>📊 Detalhes da Rota</h2>
                    <div class="cards-info">
                        <div class="card-info">
                            <span class="card-icone">📏</span>
                            <span class="card-valor" id="infoDistancia">--</span>
                            <span class="card-label">Distância</span>
                        </div>
                        <div class="card-info">
                            <span class="card-icone">⏱️</span>
                            <span class="card-valor" id="infoDuracao">--</span>
                            <span class="card-label">Duração</span>
                        </div>
                        <div class="card-info card-verde">
                            <span class="card-icone">🌍</span>
                            <span class="card-valor" id="infoCo2">--</span>
                            <span class="card-label">CO₂ economizado</span>
                        </div>
                        <div class="card-info card-laranja">
                            <span class="card-icone">🔥</span>
                            <span class="card-valor" id="infoCalorias">--</span>
                            <span class="card-label">Calorias</span>
                        </div>
                    </div>
                </div>

                <div class="secao-ia" id="secaoIA" style="display:none;">
                    <h2>🤖 Análise Inteligente</h2>
                    <div class="ia-conteudo" id="iaConteudo">
                        <p class="ia-carregando">Analisando rota com IA...</p>
                    </div>
                    <div class="ia-aviso">
                        ⚠️ Baseado exclusivamente em dados reais das APIs
                    </div>
                </div>

                <div class="secao-elevacao" id="secaoElevacao" style="display:none;">
                    <h2>⛰️ Perfil de Elevação</h2>
                    <div class="elevacao-conteudo" id="elevacaoConteudo"></div>
                </div>

                <div class="secao-camadas">
                    <h2>📌 Mostrar no mapa</h2>
                    <div class="camadas-lista">
                        <label class="camada-item">
                            <input type="checkbox" id="checkBikes" onchange="alternarBikes()">
                            <span>🚲 Estações de Bike</span>
                        </label>
                        <label class="camada-item">
                            <input type="checkbox" id="checkTransporte" onchange="alternarTransporte()">
                            <span>🚌 Ônibus e Metrô</span>
                        </label>
                        <label class="camada-item">
                            <input type="checkbox" id="checkApoio" onchange="alternarPontosApoio()">
                            <span>🏥 Pontos de Apoio</span>
                        </label>
                    </div>

                    <div class="apoio-filtros" id="apoioFiltros" style="display:none;">
                        <label class="apoio-filtro"><input type="checkbox" checked data-apoio="bebedouros"> 💧
                            Bebedouros</label>
                        <label class="apoio-filtro"><input type="checkbox" checked data-apoio="banheiros"> 🚻
                            Banheiros</label>
                        <label class="apoio-filtro"><input type="checkbox" checked data-apoio="farmacias"> 💊
                            Farmácias</label>
                        <label class="apoio-filtro"><input type="checkbox" checked data-apoio="hospitais"> 🏥
                            Hospitais/UBS</label>
                        <label class="apoio-filtro"><input type="checkbox" checked data-apoio="delegacias"> 👮
                            Delegacias</label>
                        <label class="apoio-filtro"><input type="checkbox" checked data-apoio="bancos"> 🏦
                            Bancos/ATM</label>
                    </div>
                </div>

                <div class="secao-ar" id="secaoAr">
                    <h2>🌿 Qualidade do Ar</h2>
                    <div class="ar-conteudo" id="arConteudo">
                        <p class="ar-carregando">Carregando dados do ar...</p>
                    </div>
                </div>

                <div class="secao-clima" id="secaoClima">
                    <h2>☁️ Clima Atual</h2>
                    <div class="clima-conteudo" id="climaConteudo">
                        <p class="clima-carregando">Carregando clima...</p>
                    </div>
                </div>

                <div class="secao-chat">
                    <h2>💬 Pergunte à IA</h2>
                    <div class="chat-mensagens" id="chatMensagens">
                        <div class="chat-msg chat-ia">
                            Olá! 🌱 Sou o assistente do MindWay. Pergunte sobre sua rota!
                            <span class="chat-aviso">Respondo apenas com dados reais.</span>
                        </div>
                    </div>
                    <div class="chat-input">
                        <input type="text" id="chatPergunta" placeholder="Ex: Qual o melhor transporte?"
                            onkeypress="if(event.key==='Enter')enviarPergunta()">
                        <button onclick="enviarPergunta()">➤</button>
                    </div>
                </div>

            </aside>

            <div id="mapa" class="area-mapa"></div>

        </main>

        <footer class="rodape">
            <p>🌱 MindWay — Transformação Digital na Mobilidade Urbana | 10 APIs + IA | Projeto UPX 2026</p>
        </footer>

        <script src="js/mapa.js"></script>

    </body>

    </html>