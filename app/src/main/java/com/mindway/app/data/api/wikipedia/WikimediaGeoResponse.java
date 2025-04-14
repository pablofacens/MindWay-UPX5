package com.mindway.app.data.api.wikipedia;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WikimediaGeoResponse {

    @SerializedName("query")
    public Query query;

    public static class Query {
        @SerializedName("geosearch")
        public List<GeoResult> geosearch;
    }

    public static class GeoResult {
        @SerializedName("pageid")
        public int pageid;

        @SerializedName("title")
        public String title;

        @SerializedName("lat")
        public double lat;

        @SerializedName("lon")
        public double lon;

        @SerializedName("dist")
        public double dist;
    }
}
