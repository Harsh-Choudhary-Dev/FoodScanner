package com.foodScanner.scanner.controller;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.Scanner;

@RestController
public class ApiController {

    @GetMapping("/product_id")
    public String getFoodDetails(@RequestParam("productDetails") String product_id) throws FileNotFoundException {
        System.out.println(product_id);
        writeToFile("productId.txt",product_id);
        String jsonResults = null;
        while (jsonResults == null) {
            System.out.println("Waiting for results...");
            jsonResults = getJsonResult();
            try {
                Thread.sleep(1000); // Sleep for 1 second
            } catch (InterruptedException e) {
                System.err.println("Thread interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        // Results found
        System.out.println("Results found: " + jsonResults);
        deleteFile("productHealthDescription.txt");
        return jsonResults;
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

    public static String getJsonResult() throws FileNotFoundException {
        File file = new File("productHealthDescription.txt");

        if (!file.exists()) {
            System.err.println("File not found: " + file.getAbsolutePath());
            return null; // Or handle the case as needed
        }
        Scanner scanner = new Scanner(new File("productHealthDescription.txt"));

        String line = null;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            System.out.println(line);
        }
        return line;
    }

    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);

        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("File deleted successfully.");
            } else {
                System.err.println("Failed to delete the file.");
            }
            return deleted;
        } else {
            System.err.println("File not found: " + filePath);
            return false;
        }
    }
}
