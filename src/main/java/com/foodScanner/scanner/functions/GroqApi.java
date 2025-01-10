package com.foodScanner.scanner.functions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public class GroqApi {

    public String getAIResponse(String dynamicContent) {
        String apiUrl = "https://api.groq.com/openai/v1/chat/completions";
        String apiKey = "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";  // Set your API Key here

        JSONObject message = null;
        try {
            // Create URL object from the API URL
            URL url = new URL(apiUrl);

            // Open a connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method to POST
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);

            // Enable input/output streams
            connection.setDoOutput(true);

            // Prepare the JSON payload
            String jsonPayload = "{\n" +
                    "  \"model\": \"llama-3.3-70b-versatile\",\n" +
                    "  \"messages\": [\n" +
                    "    {\n" +
                    "      \"role\": \"user\",\n" +
                    "      \"content\": \"" + dynamicContent + "\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            // Send the request payload
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the response from the server
            int status = connection.getResponseCode();
            if (status == 200) {
                // Read and print the response
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    JSONObject jsonResponse = new JSONObject(response.toString());

                    // Get the "choices" array
                    JSONArray choices = jsonResponse.getJSONArray("choices");

                    // Get the first choice and extract the "message"
                    JSONObject firstChoice = choices.getJSONObject(0);
                    message = firstChoice.getJSONObject("message");

                    // Print the content of the "message"
//                    System.out.println("Message: " + message.getString("content"));
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

            // Close the connection
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        assert message != null;
        return message.getString("content");
    }
}
