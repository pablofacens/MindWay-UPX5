package com.mindway.app.data.api.ia;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GeminiResponse {

    @SerializedName("candidates")
    public List<Candidate> candidates;

    public static class Candidate {
        @SerializedName("content")
        public Content content;
    }

    public static class Content {
        @SerializedName("parts")
        public List<Part> parts;
    }

    public static class Part {
        @SerializedName("text")
        public String text;
    }

    /** Extrai o texto do primeiro candidato, ou null se ausente. */
    public String getTexto() {
        if (candidates == null || candidates.isEmpty()) return null;
        Candidate c = candidates.get(0);
        if (c.content == null || c.content.parts == null || c.content.parts.isEmpty()) return null;
        return c.content.parts.get(0).text;
    }
}
