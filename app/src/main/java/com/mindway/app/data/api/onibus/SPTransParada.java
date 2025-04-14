package com.mindway.app.data.api.onibus;

import com.google.gson.annotations.SerializedName;

public class SPTransParada {

    /** Código da parada */
    @SerializedName("cp")
    public int codigoParada;

    /** Nome da parada */
    @SerializedName("np")
    public String nomeParada;

    /** Endereço / logradouro */
    @SerializedName("ed")
    public String endereco;

    /** Latitude (projeção Google) */
    @SerializedName("py")
    public double latitude;

    /** Longitude (projeção Google) */
    @SerializedName("px")
    public double longitude;
}
