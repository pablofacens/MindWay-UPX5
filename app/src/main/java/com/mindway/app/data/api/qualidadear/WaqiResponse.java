package com.mindway.app.data.api.qualidadear;

import com.google.gson.annotations.SerializedName;

public class WaqiResponse {

    @SerializedName("status")
    public String status;

    @SerializedName("data")
    public Data data;

    public static class Data {
        @SerializedName("aqi")
        public int aqi;

        @SerializedName("iaqi")
        public Iaqi iaqi;

        @SerializedName("city")
        public City city;
    }

    public static class Iaqi {
        @SerializedName("pm25")
        public Valor pm25;

        @SerializedName("pm10")
        public Valor pm10;

        @SerializedName("co")
        public Valor co;

        @SerializedName("no2")
        public Valor no2;

        @SerializedName("o3")
        public Valor o3;

        @SerializedName("so2")
        public Valor so2;
    }

    public static class Valor {
        @SerializedName("v")
        public double v;
    }

    public static class City {
        @SerializedName("name")
        public String name;
    }
}
