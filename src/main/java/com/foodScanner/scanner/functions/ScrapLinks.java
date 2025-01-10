package com.foodScanner.scanner.functions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component()
@Scope("prototype")
public class ScrapLinks {

    @Autowired
    private  WebScraping webScraping;
    @Autowired
    private GroqApi groqApi;


    public String getProductDescription(String prod_id) {
        System.setProperty("webdriver.chrome.driver", "/home/harsh/programming/food scanner/backend/scanner/chromedriver");

        // Create Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-gpu");
//        options.addArguments("--headless"); // Optional: Run in headless mode

        // Initialize WebDriver
        WebDriver driver = new ChromeDriver(options);

        String indegriendDescription = null;
        try {
            // Define the search query dynamically
            String searchQuery = prod_id +" site:bigbasket.com";
            String googleSearchUrl = "https://www.google.com/search?q=" + searchQuery.replace(" ", "+");

            // Open Google Search
            driver.get(googleSearchUrl);

            // Wait for results to load (optional if results are already rendered quickly)
            Thread.sleep(2000);

            // Locate search results container
            List<WebElement> searchResults = driver.findElements(By.cssSelector("div.yuRUbf"));

            // Loop through each result and print the title and link
            for (WebElement result : searchResults) {
                // Extract the title
                String title = result.findElement(By.tagName("h3")).getText();

                // Extract the link
                String link = result.findElement(By.tagName("a")).getAttribute("href");

                System.out.println("Title: " + title);
                System.out.println("Link: " + link);
                String rawProductDetails = WebScraping.rawProductDetails(link, driver) + " Extract the list of ingredients mentioned in the following product details. Ignore any promotional or irrelevant information. Provide only the ingredients in a clean, bullet-point format.";
//                System.out.println("---------------------------------");
                String ingredients = groqApi.getAIResponse(rawProductDetails.replaceAll("\n", " ")) + " Analyze the following list of ingredients and provide:\n" +
                        "1. A short explanation for each ingredient (its purpose or role in the product).\n" +
                        "2. An assessment of how healthy this product is for human consumption.\n" +
                        "3. A brief explanation of how these ingredients may affect human health.";
//                System.out.println(ingredients);
//                System.out.println("---------------- indegriendDescription -----------------");

                indegriendDescription = groqApi.getAIResponse(ingredients.replaceAll("\n", " "));

//                System.out.println(indegriendDescription);
                break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Quit the browser
            driver.quit();
        }
        return indegriendDescription;
    }
}
