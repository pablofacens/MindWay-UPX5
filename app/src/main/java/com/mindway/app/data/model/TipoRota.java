package com.mindway.app.data.model;

public enum TipoRota {

    RAPIDA("Rápida", "Menor tempo de deslocamento, priorizando metrô e ônibus expressos"),
    ECONOMICA("Econômica", "Menor custo de passagem, combinando diferentes modais de transporte"),
    VERDE("Verde", "Menor emissão de CO₂, priorizando bicicleta e caminhada");

    private final String nome;
    private final String descricao;

    TipoRota(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }
}
