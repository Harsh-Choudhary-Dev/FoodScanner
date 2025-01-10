package com.foodScanner.scanner.functions;

import jakarta.persistence.Entity;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public class GeminiApi {

    public String getAIResponse() {
        String apiKey = "AIzaSyB0XXIkY4KKNumMSy57IWfRtdU_D5QyplU";
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro:generateContent?key=" + apiKey;
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            String jsonPayload = "{\n" +
                    "  \"contents\": [\n" +
                    "    {\n" +
                    "      \"role\": \"user\",\n" +
                    "      \"parts\": [\n" +
                    "        {\n" +
                    "          \"text\": \"Hii\"\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"generationConfig\": {\n" +
                    "    \"temperature\": 1,\n" +
                    "    \"topK\": 40,\n" +
                    "    \"topP\": 0.95,\n" +
                    "    \"maxOutputTokens\": 8192,\n" +
                    "    \"responseMimeType\": \"text/plain\"\n" +
                    "  }\n" +
                    "}";

            // Send the request payload
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int status = connection.getResponseCode();
            if (status == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    System.out.println("Response: " + response);
                }
            } else {
                System.out.println("Error: " + status);
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    String inputLine;
                    StringBuilder errorResponse = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        errorResponse.append(inputLine);
                    }
                    System.out.println("Error Response: " + errorResponse);
                }
            }
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(response);
    }

}
