package com.mindway.app.data.api.bicicleta;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CityBikesResponse {

    @SerializedName("network")
    public Network network;

    public static class Network {
        @SerializedName("id")
        public String id;

        @SerializedName("name")
        public String name;

        @SerializedName("stations")
        public List<Station> stations;
    }

    public static class Station {
        @SerializedName("id")
        public String id;

        @SerializedName("name")
        public String name;

        @SerializedName("latitude")
        public double latitude;

        @SerializedName("longitude")
        public double longitude;

        @SerializedName("free_bikes")
        public int freeBikes;

        @SerializedName("empty_slots")
        public int emptySlots;
    }
}
