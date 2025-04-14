package com.mindway.app.data.api.rota;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OSRMResponse {

    @SerializedName("code")
    public String code;

    @SerializedName("routes")
    public List<Route> routes;

    public static class Route {
        /** Distância total em metros */
        @SerializedName("distance")
        public double distance;

        /** Duração total em segundos */
        @SerializedName("duration")
        public double duration;

        @SerializedName("geometry")
        public Geometry geometry;
    }

    public static class Geometry {
        /** Lista de coordenadas [longitude, latitude] */
        @SerializedName("coordinates")
        public List<List<Double>> coordinates;

        @SerializedName("type")
        public String type;
    }
}
