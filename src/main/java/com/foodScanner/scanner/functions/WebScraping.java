package com.foodScanner.scanner.functions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.*;
import java.util.List;
import java.util.Scanner;


public class WebScraping {

    public static WebDriver driverConfig(){
        System.out.println();
        System.setProperty("webdriver.chrome.driver", "/home/harsh/programming/food scanner/backend/scanner/chromedriver");

        // Create Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-gpu");
//        options.addArguments("--headless");

        return new ChromeDriver(options);
    }

    public static JsonNode webScrap(String findProductNameQuery, WebDriver driver, int time) throws InterruptedException, JsonProcessingException {
        JsonNode jsonNode = null;
        try {
            driver.get("https://www.google.com/");
            WebElement searchBox = driver.findElement(By.className("gLFyf"));
            System.out.println(searchBox);
            searchBox.sendKeys(findProductNameQuery);
            searchBox.sendKeys(Keys.RETURN);
            Thread.sleep(time);
            String productName = driver.findElements(By.cssSelector("div.yuRUbf")).get(0).findElement(By.tagName("h3")).getText();
            System.out.println(productName);
            productName = GroqApi.getAIResponse(productName + "get the name of the product from this givvve me only name");
            List<WebElement> searchBox1 = driver.findElements(By.className("gLFyf"));
            searchBox1.get(1).clear();
            searchBox1.get(1).sendKeys(productName + " buy site:amazon.in");
            searchBox1.get(1).sendKeys(Keys.RETURN);
            Thread.sleep(time);
            String productAmazonLink = driver.findElements(By.cssSelector("div.yuRUbf")).get(0).findElement(By.tagName("a")).getAttribute("href");
            assert productAmazonLink != null;
            driver.get(productAmazonLink);
            System.out.println(productAmazonLink);
            Thread.sleep(time);
            scrollPageInLoop(driver, 4, 1000);
            String productDetails = driver.findElement(By.id("prodDetails")).getText();
            Thread.sleep(time);
        System.out.println(productDetails);
            String prompt = String.format("Given the following product details, analyze the ingredients in terms of health benefits and classify each ingredient as 'Good', 'Neutral', or 'Harmful', explaining the reason behind each classification. Then, rate the overall healthiness of the product on a scale of 1 to 10, with 1 representing the highest health benefits and 10 representing the lowest health benefits, and provide a detailed explanation for the rating. Ensure the response is strictly in JSON format with the given structure. Product Details: %s Output Format: { 'ingredients': [ { 'name': '[ingredient name]', 'classification': '[Good/Neutral/Harmful]', 'reason': '[explanation]' }, ... ], 'health_rating': { 'score': '[1-10]', 'explanation': '[explanation of the rating]' } }", productDetails.replaceAll("\n", "").replaceAll("\"",""));
            ObjectMapper objectMapper = new ObjectMapper();
            String ai_response = GroqApi.getAIResponse(prompt);
            System.out.println(ai_response);
            jsonNode = objectMapper.readTree(ai_response.replaceAll("```", "").replaceAll("```json", "").replaceAll("json", ""));
            System.out.println();
            System.out.println(jsonNode.toPrettyString());
        } catch (Exception e) {
            System.out.println("json not found");
        }
        // Pretty print
        return jsonNode;
    }


    public static String getProductIdFromFile() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("productId.txt"));
        String line = null;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            System.out.println(line);
        }
        return line;
    }

    public static void writeToFile(String filePath, String content) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(content);
            writer.newLine();
            System.out.println("Content written to file successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing the writer: " + e.getMessage());
            }
        }
    }

    public static void removeProductId() throws IOException {
        FileWriter writer = new FileWriter("productId.txt");
        writer.write("");
        System.out.println("File content cleared.");
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("hellow");
        WebDriver driver = driverConfig();
        int i = 0;
        int time = 0;
        while(true){
            Thread.sleep(10000);
            String product_id = getProductIdFromFile();
            System.out.println(product_id);
            if(product_id != null){
                if(i == 0){
                    time = 15000;
                }else {
                    time = 1000;
                }
                String findProductNameQuery = product_id+ " what is this product";
                System.out.println(product_id);
                JsonNode healthJson = webScrap(findProductNameQuery,driver,time);
                i++;
                writeToFile("productHealthDescription.txt", String.valueOf(healthJson));
                removeProductId();
            }
        }
    }
    public static void scrollPageInLoop(WebDriver driver, int times, int pixels) throws InterruptedException {
        // Cast the WebDriver to JavaScriptExecutor
        JavascriptExecutor js = (JavascriptExecutor) driver;

        for (int i = 0; i < times; i++) {
            // Scroll down by the specified pixel amount
            js.executeScript("window.scrollBy(0, arguments[0]);", pixels);
            System.out.println("Scrolled " + (i + 1) + " times");

            // Pause for 1 second to mimic natural scrolling
            Thread.sleep(1000);
        }
    }
}
