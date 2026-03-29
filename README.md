# MindWay

MindWay é um sistema integrado de mobilidade urbana que consolida informações de diversas fontes de dados públicos de transporte, clima e infraestrutura. O objetivo é fornecer uma visão centralizada para ajudar no planejamento de deslocamentos na cidade.

## Funcionalidades

- Rotas multimodais (a pé, bicicleta e carro) calculadas dinamicamente.
- Exibição de estações de compartilhamento de bicicletas com disponibilidade em tempo real.
- Mapeamento de paradas de transporte público (ônibus, trem, metrô).
- Consulta de dados meteorológicos e qualidade do ar por região.
- Estimativas de impacto ambiental do deslocamento (economia de emissões de CO₂ e gasto calórico).
- Geração de resumos e recomendação de percursos (via integração com LLM baseada em dados reais).

## Stack Tecnológico

- **Backend:** Java 17, Servlets, JSP, Maven
- **Frontend:** HTML, CSS, JavaScript (Vanilla), Leaflet.js
- **Formatos de Transporte:** JSON (via Gson)

## APIs e Serviços Utilizados

O projeto consome as seguintes bibliotecas e recursos externos gratuitos e/ou públicos:

- **Nominatim** (Geocodificação e endereços)
- **OSRM** (Cálculo de rotas de deslocamento)
- **CityBikes** (Status das estações de bicicleta)
- **Overpass API / OpenStreetMap** (Mapeamento de locais e transporte)
- **Open-Elevation** (Perfil topográfico da rota)
- **OpenWeatherMap** (Condições climáticas)
- **WAQI** (Qualidade do ar)
- **IBGE e ViaCEP** (Dados demográficos e logradouros locais)
- **Google Gemini** (Motor de inferência contextual)

## Requisitos

- Java 17+ (JDK)
- Apache Tomcat 9 ou superior
- Maven 3+

## Como Executar

1. Clone este repositório para a sua máquina local.
2. Reconheça o projeto utilizando a sua IDE de preferência (IntelliJ IDEA, Eclipse, NetBeans, etc) apontando para o arquivo `pom.xml`.
3. Resolva as dependências e faça o build utilizando o Maven:
   ```bash
   mvn clean install
   ```
4. Suba a aplicação no seu servidor Apache Tomcat configurado.
5. Em seu navegador, abra o painel inicial acessando `http://localhost:8080/MindWayWeb/` (ajuste a porta e o contexto confome a configuração do container).

## Configuração

A aplicação funcionará via fallback mode se as chaves das APIs privadas não estiverem presentes, porém certas funções poderão ser limitadas. 

Para habilitar a integração completa:

- **OpenWeatherMap:** Substitua a constante da variável `apiKey` dentro do arquivo `src/main/webapp/js/mapa.js` com a sua key.
- **Gemini:** Modifique a propriedade `API_KEY` na classe `src/main/java/br/com/mindway/servicos/ServicoIA.java`.
