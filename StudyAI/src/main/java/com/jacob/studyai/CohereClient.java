// File: src/main/java/com/jacob/studyai/CohereClient.java
package com.jacob.studyai;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Cohere client that splits definitions into batches
 * and concatenates the generated Q&A from each batch.
 */
public class CohereClient {
    // ← Replace with your real Cohere API key
    private static final String API_KEY = "z6LGNXudZRbSIvcIX1xsl7PFW9yB4s0tP9sX0XvH";
    private static final String API_URL = "https://api.cohere.com/v1/generate";

    private final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build();

    public CohereClient() { }

    /**
     * Splits the notes into batches of definitions, calls Cohere for each batch,
     * and returns the combined generated text.
     */
    public String generateQuestions(String notes) throws IOException {
        // 1) Split into individual definitions (by ". ")
        String[] defs = notes.split("\\.\\s+");
        List<String> batches = new ArrayList<>();
        int batchSize = 5;

        // 2) Group definitions into batches
        for (int i = 0; i < defs.length; i += batchSize) {
            int end = Math.min(defs.length, i + batchSize);
            String batchNotes = String.join(". ", Arrays.copyOfRange(defs, i, end)).trim() + ".";
            batches.add(batchNotes);
        }

        // 3) Call Cohere for each batch and collect responses
        StringBuilder allResponses = new StringBuilder();
        for (String batch : batches) {
            String prompt = "Below are several definitions, each separated by a period and space:\n\n"
                          + batch + "\n\n"
                          + "For each definition, output a numbered study question and answer pair in this exact format:\n"
                          + "1. <Question?> - <Answer.>\n"
                          + "2. <Question?> - <Answer.>\n"
                          + "…one per definition.";

            JSONObject payload = new JSONObject()
                .put("model",           "command")
                .put("prompt",          prompt)
                .put("max_tokens",      200)
                .put("temperature",     0.7)
                .put("num_generations", 1);

            RequestBody body = RequestBody.create(
                payload.toString(),
                MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type",  "application/json")
                .post(body)
                .build();

            // Execute and parse
            try (Response response = client.newCall(request).execute()) {
                String json = response.body() != null ? response.body().string() : "";
                if (response.code() != 200) {
                    throw new IOException("Cohere API error " + response.code() + ": " + json);
                }
                JSONObject root = new JSONObject(json);
                JSONArray gens = root.getJSONArray("generations");
                if (gens.isEmpty()) {
                    continue;
                }
                String text = gens.getJSONObject(0).getString("text");
                allResponses.append(text).append("\n");
            }
        }

        return allResponses.toString();
    }
}
