package com.foodScanner.scanner.functions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class WebScraping {

    public static String rawProductDetails(String link, WebDriver driver)  {

        StringBuilder allText = new StringBuilder();
        try {
            driver.get(link);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> sections = driver.findElements(By.cssSelector("section"));
            List<WebElement> children = sections.get(8).findElements(By.xpath("./*"));
            for (WebElement child : children) {
                allText.append(child.getText()).append("\n");
            }

//            System.out.println("====================================");
//            System.out.println("All Child Text: \n" + allText);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            driver.quit();
        }

        return allText.toString();
    }
}
