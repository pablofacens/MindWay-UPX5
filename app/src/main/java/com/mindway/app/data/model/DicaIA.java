package com.mindway.app.data.model;

public class DicaIA {

    private String texto;
    private String tipoRota;
    private long timestamp;

    public DicaIA() {}

    public DicaIA(String texto, String tipoRota, long timestamp) {
        this.texto = texto;
        this.tipoRota = tipoRota;
        this.timestamp = timestamp;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getTipoRota() {
        return tipoRota;
    }

    public void setTipoRota(String tipoRota) {
        this.tipoRota = tipoRota;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
