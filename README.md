**MindWay** 

O projeto utiliza dados em tempo real e geolocalização para mapear paradas de ônibus, estações de metrô/trem, além de pontos de interesse como farmácias e bebedouros.

## 🚀 Funcionalidades

- **Mapeamento de Transporte Público**: Localização de pontos de ônibus, estações de metrô e CPTM nas proximidades utilizando a Overpass API (OpenStreetMap).
- **Integração com CityBikes**: Consulta em tempo real de estações de bicicletas (como as redes Tembici/Itaú), mostrando disponibilidade de bikes e vagas.
- **Pontos de Apoio**: Busca por amenidades essenciais como:
  - Farmácias
  - Bebedouros
  - Banheiros públicos
  - Hospitais
  - Bancos e fontes
- **Navegação em Mapa**: Visualização interativa através do OpenStreetMap (OsmDroid).
- **Cálculo de Rotas**: Detalhamento de trajetos para facilitar o deslocamento urbano.
- **Cadastro e Sincronização**: Sistema de autenticação de usuários via **Firebase Auth**, permitindo salvar pontos favoritos e histórico de rotas com sincronização na nuvem via **Firestore**.
- **Assistente de IA (Luna)**: Chat inteligente integrado que utiliza a API do Gemini para analisar rotas, fornecer recomendações de segurança e tirar dúvidas sobre o trajeto em tempo real.

## 🛠 Tecnologias Utilizadas

- **Linguagem**: Java / Kotlin
- **Mapas**: [OsmDroid](https://github.com/osmdroid/osmdroid)
- **Rede/API**: [Retrofit 2](https://square.github.io/retrofit/)
- **Backend/Auth**: [Firebase](https://firebase.google.com/) (Authentication & Firestore)
- **Localização**: Google Play Services Location
- **Fontes de Dados**:
  - [Overpass API](https://wiki.openstreetmap.org/wiki/Overpass_API) (OpenStreetMap)
  - [CityBikes API](https://citybik.es/)
  - [Google Gemini API](https://ai.google.dev/) (Inteligência Artificial)

## 🔧 Como Executar

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/MindWay.git
   ```
2. Abra o projeto no **Android Studio**;
3. Certifique-se de ter o SDK do Android configurado corretamente;
4. Sincronize o Gradle;
5. Ajustar as API;
6. Execute o aplicativo em um emulador ou dispositivo físico.

---

MindWay - Mobilidade Inteligente
