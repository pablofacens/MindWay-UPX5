package com.mindway.app.data.api.wikipedia;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WikipediaSearchResponse {

    @SerializedName("query")
    public Query query;

    public static class Query {
        @SerializedName("search")
        public List<SearchResult> search;
    }

    public static class SearchResult {
        @SerializedName("pageid")
        public int pageid;

        @SerializedName("title")
        public String title;

        @SerializedName("snippet")
        public String snippet;
    }
}
