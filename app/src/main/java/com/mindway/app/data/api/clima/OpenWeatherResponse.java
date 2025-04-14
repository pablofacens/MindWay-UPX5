package com.mindway.app.data.api.clima;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OpenWeatherResponse {

    @SerializedName("weather")
    public List<Weather> weather;

    @SerializedName("main")
    public Main main;

    @SerializedName("wind")
    public Wind wind;

    public static class Weather {
        @SerializedName("description")
        public String description;

        @SerializedName("icon")
        public String icon;
    }

    public static class Main {
        @SerializedName("temp")
        public double temp;

        @SerializedName("humidity")
        public int humidity;
    }

    public static class Wind {
        // API retorna m/s com units=metric; conversão para km/h ocorre no repositório
        @SerializedName("speed")
        public double speed;
    }
}
