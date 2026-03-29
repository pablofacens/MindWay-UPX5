# MindWay

MindWay é um sistema integrado de mobilidade urbana que reune informações de diversas fontes de dados públicos de transporte, clima e infraestrutura. O objetivo é fornecer uma visão centralizada para ajudar no planejamento de deslocamentos na cidade.

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

O projeto consome as seguintes bibliotecas e recursos externos (APIS):

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
- Maven 3+.
