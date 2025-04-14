package com.mindway.app.data.api.wikipedia;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class WikimediaImageResponse {

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

        @SerializedName("imageinfo")
        public List<ImageInfo> imageinfo;
    }

    public static class ImageInfo {
        @SerializedName("url")
        public String url;

        @SerializedName("thumburl")
        public String thumburl;

        @SerializedName("thumbwidth")
        public int thumbwidth;

        @SerializedName("thumbheight")
        public int thumbheight;
    }
}
