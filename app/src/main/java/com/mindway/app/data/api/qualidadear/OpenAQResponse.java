package com.mindway.app.data.api.qualidadear;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OpenAQResponse {

    @SerializedName("results")
    public List<Medicao> results;

    public static class Medicao {
        @SerializedName("parameter")
        public String parameter;

        @SerializedName("value")
        public double value;

        @SerializedName("unit")
        public String unit;

        @SerializedName("location")
        public String location;

        @SerializedName("date")
        public Date date;

        public static class Date {
            @SerializedName("utc")
            public String utc;

            @SerializedName("local")
            public String local;
        }
    }
}
