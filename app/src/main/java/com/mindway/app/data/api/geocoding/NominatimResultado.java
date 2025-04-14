package com.mindway.app.data.api.geocoding;

import com.google.gson.annotations.SerializedName;

public class NominatimResultado {

    @SerializedName("lat")
    public String lat;

    @SerializedName("lon")
    public String lon;

    @SerializedName("display_name")
    public String nomeExibicao;
}
