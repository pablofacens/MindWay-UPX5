package com.mindway.app.data.api.wikipedia;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class WikipediaPageResponse {

    @SerializedName("query")
    public Query query;

    public static class Query {
        @SerializedName("pages")
        public Map<String, Page> pages;
    }

    public static class Page {
        @SerializedName("pageid")
        public int pageid;

        @SerializedName("title")
        public String title;

        @SerializedName("extract")
        public String extract;

        @SerializedName("thumbnail")
        public Thumbnail thumbnail;
    }

    public static class Thumbnail {
        @SerializedName("source")
        public String source;

        @SerializedName("width")
        public int width;

        @SerializedName("height")
        public int height;
    }
}
