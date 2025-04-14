package com.mindway.app.data.api.overpass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OverpassResponse {

    @SerializedName("elements")
    public List<Element> elements;

    public static class Element {
        @SerializedName("lat")  public double lat;
        @SerializedName("lon")  public double lon;
        @SerializedName("tags") public Tags tags;

        public static class Tags {
            @SerializedName("amenity") public String amenity;
            @SerializedName("name")    public String name;
        }
    }
}
