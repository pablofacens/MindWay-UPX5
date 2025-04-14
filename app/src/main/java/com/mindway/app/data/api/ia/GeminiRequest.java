package com.mindway.app.data.api.ia;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;

public class GeminiRequest {

    @SerializedName("contents")
    public List<Content> contents;

    public GeminiRequest(String prompt) {
        Part part = new Part(prompt);
        Content content = new Content(Collections.singletonList(part));
        this.contents = Collections.singletonList(content);
    }

    public static class Content {
        @SerializedName("parts")
        public List<Part> parts;

        public Content(List<Part> parts) {
            this.parts = parts;
        }
    }

    public static class Part {
        @SerializedName("text")
        public String text;

        public Part(String text) {
            this.text = text;
        }
    }
}
