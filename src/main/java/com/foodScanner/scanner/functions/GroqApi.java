package com.foodScanner.scanner.functions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GroqApi {

    public static String getAIResponse(String prompt) {
        String apiUrl = "https://api.groq.com/openai/v1/chat/completions";
        String apiKey = ""; // Replace with your actual API key
        String jsonInputString = String.format("""
                    {
                        "model": "llama-3.3-70b-versatile",
                        "messages": [{
                            "role": "user",
                            "content": "%s"
                        }]
                    }
                """, prompt);

        String content = null;
        try {
            // Create URL object
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Configure the connection
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);

            // Write JSON input to the request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the full response
            int statusCode = connection.getResponseCode();
            BufferedReader reader;
            if (statusCode >= 200 && statusCode < 300) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = reader.readLine()) != null) {
                response.append(responseLine.trim());
            }
            JSONObject jsonResponse = new JSONObject(String.valueOf(response));
            System.out.println(response);
            System.out.println("---------------------------------------------");
            System.out.println(jsonResponse);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            JSONObject firstChoice = (choices).getJSONObject(0);
            JSONObject message = firstChoice.getJSONObject("message");
            content = message.getString("content");
            System.out.println(content);
            // Close the connection
            reader.close();
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

//    public static void main(String[] args) {
//        String info = "Technical Details\n" +
//                "Specialty No Artificial Flavour, No artificial colors, No Preservatives, Suitable for Vegetarians\n" +
//                "Weight 800 Grams\n" +
//                "Ingredient Type Food Safe\n" +
//                "Brand Bhikharam Chandmal\n" +
//                "Package Information Pouch\n" +
//                "Manufacturer Sun Shine Food Products (I) Private Limited\n" +
//                "Item model number BC-bhu-800X1\n" +
//                "Net Quantity 1000 gram\n" +
//                "Product Dimensions 33.3 x 24.5 x 3.5 cm; 800 g\n" +
//                "Ingredients \" Ingredients :- Tepary Beans Flour (47.5%), Edible Vegetable Oil (Peanut Oil and/or Cotton Seed Oil and/or Corn Oil), Chickpeas Pulse Flour (9%), Edible Common Salt, Red Chilly Powder, Black Pepper Powder, Clove Powder, Ginger Powder, Peepal, Cardamom Powder, Cinnamon Powder, Mace Powder, Nutmeg Powder. \"\n" +
//                "Additional Information\n" +
//                "ASIN B07H7TGGC7\n" +
//                "Customer Reviews 4.3\n" +
//                "1,646 ratings\n" +
//                "4.3 out of 5 stars\n" +
//                "Best Sellers Rank #5,484 in Grocery & Gourmet Foods (See Top 100 in Grocery & Gourmet Foods)\n" +
//                "#155 in Namkeen\n" +
//                "Date First Available 9 September 2018\n" +
//                "Manufacturer Sun Shine Food Products (I) Private Limited, SUN SHINE FOOD PRODUCTS (I) PVT.LTD. F-88-89,BICHHWAL INDUSTIRAL AREA, BIKANER, Rajasthan, India - 334006\n" +
//                "Packer SUN SHINE FOOD PRODUCTS (I) PVT.LTD. F-88-89,BICHHWAL INDUSTIRAL AREA, BIKANER, Rajasthan, India - 334006\n" +
//                "Item Weight 800 g\n" +
//                "Item Dimensions LxWxH 33.3 x 24.5 x 3.5 Centimeters\n" +
//                "Generic Name NAMKEEN\n" +
//                "Feedback\n" +
//                "Would you like to tell us about a lower price?";
//        getAIResponse(info.replaceAll("\n","").replaceAll("\"","") + "Given the following product details, analyze the ingredients in terms of health benefits and classify each ingredient as 'Good', 'Neutral', or 'Harmful', explaining the reason behind each classification. Then, rate the overall healthiness of the product on a scale of 1 to 10, with 1 representing the highest health benefits and 10 representing the lowest health benefits, and provide a detailed explanation for the rating. Ensure the response is strictly in JSON format with the given structure. Product Details: %s Output Format: { 'ingredients': [ { 'name': '[ingredient name]', 'classification': '[Good/Neutral/Harmful]', 'reason': '[explanation]' }, ... ], 'health_rating': { 'score': '[1-10]', 'explanation': '[explanation of the rating]' } }");
//    }
}
