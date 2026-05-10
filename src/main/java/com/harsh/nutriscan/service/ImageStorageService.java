package com.harsh.nutriscan.service;


import com.harsh.nutriscan.client.PythonAiClient;
import com.harsh.nutriscan.entity.*;
import com.harsh.nutriscan.repository.NutrientRepository;
import com.harsh.nutriscan.repository.ProductRepository;
import com.harsh.nutriscan.repository.ProductScoreRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.*;

@Service
@AllArgsConstructor
public class ImageStorageService {

    private final PythonAiClient pythonAiClient;
    private final ProductRepository productRepository;
    private final ProductScoreRepository productScoreRepository;
    private final NutrientRepository nutrientRepository;

    public File saveToTempFolder(MultipartFile image)
            throws IOException {

        if (image == null || image.isEmpty()) {
            throw new RuntimeException("Image is empty");
        }

        String tempDir = System.getProperty("java.io.tmpdir");
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        File tempFile = new File(tempDir, fileName);
        image.transferTo(tempFile);
        System.out.println(tempFile.getAbsoluteFile());
        return tempFile;
    }

    public Map identifyProduct(Path imagePath){
        return pythonAiClient.identifyProduct(imagePath);
    }

    public Optional<Product> searchByProductName(String productName){
        return productRepository.findByProductNameContainingIgnoreCase(productName);
    }

    public Map<String, Object> analyzeProductFromImage(MultipartFile image) throws IOException {
        File file = saveToTempFolder(image);
        Map productName = identifyProduct(file.toPath());
        String product_name = productName.get("product_name").toString();
        Product products = searchByProductName(product_name).orElseThrow(()-> new RuntimeException("product not found"));

        Optional<ProductScore> productScore = productScoreRepository.findByProductId(products.getId());
        if(productScore.isEmpty()){
            double ingredientScore = calculateIngredientScore(products.getIngredients());
            System.out.println(ingredientScore);
            List<Nutrient> nutrients = extractNutritionKeys(products.getNutritionFact());
            System.out.println(nutrients);
            double nutrientScore = calculateNutrientScore(products.getNutritionFact(),nutrients);
            System.out.println(nutrientScore);
            ProductScore newProductScore = new ProductScore();
            newProductScore.setIngredientScore(BigDecimal.valueOf(ingredientScore));
            newProductScore.setNutritionScore(BigDecimal.valueOf(nutrientScore));
            newProductScore.setProduct(products);
            productScoreRepository.save(newProductScore);
            return Map.of(
                    "product_details",products,
                    "ingredientScore",ingredientScore,
                    "nutrientScore",nutrientScore
            );
        }
        return Map.of(
                "product_details",products,
                "ingredientScore",productScore.get().getIngredientScore(),
                "nutrientScore",productScore.get().getNutritionScore()
        );

    }

    public double calculateIngredientScore(Set<Ingredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return 50.0; // neutral default
        }
        double totalImpact = 0;
        for (Ingredient ingredient : ingredients) {

            if (ingredient.getImpactScore() != null) {
                totalImpact += ingredient.getImpactScore().doubleValue();
            }
        }
        double finalScore = 50 + totalImpact;
        if (finalScore > 100) {
            finalScore = 100;
        }

        if (finalScore < 0) {
            finalScore = 0;
        }
        return Math.round(finalScore * 10.0) / 10.0;
    }

    public List<Nutrient> extractNutritionKeys(NutritionFact nutritionFact) {
        List<String> keys = new ArrayList<>();
        if (nutritionFact == null) {
            return List.of();
        }
        Field[] fields = NutritionFact.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(nutritionFact);
                // Skip null values
                if (value == null) {
                    continue;
                }
                String fieldName = field.getName();
                // Skip unwanted fields
                if (
                        fieldName.equals("id") ||
                                fieldName.equals("product") ||
                                fieldName.equals("createdAt") ||
                                fieldName.equals("updatedAt")
                ) {
                    continue;
                }

                keys.add(fieldName);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return nutrientRepository.findByNameIn(keys);
    }

    public double calculateNutrientScore(NutritionFact nutritionFact, List<Nutrient> nutrients) {
        double score = 50.0;
        for (Nutrient nutrient : nutrients) {
            try {
                String fieldName = nutrient.getName();
                Field field = NutritionFact.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(nutritionFact);
                if (value == null) {continue;}
                double nutrientValue = ((BigDecimal) value).doubleValue();
                double impactScore = nutrient.getImpactScore().doubleValue();
                double normalizedValue = normalize(fieldName, nutrientValue);
                score += normalizedValue * impactScore;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (score > 100) {
            score = 100;
        }
        if (score < 0) {
            score = 0;
        }
        return Math.round(score * 100.0) / 100.0;
    }

    private double normalize(String fieldName, double value) {
        return switch (fieldName) {
            case "energy_kcal" -> value / 100.0;
            case "protein_g",
                 "carbohydrates_g",
                 "fat_g",
                 "saturated_fat_g",
                 "sugars_g",
                 "added_sugars_g",
                 "fiber_g"
                    -> value / 10.0;

            case "sodium_mg",
                 "cholesterol_mg",
                 "calcium_mg",
                 "iron_mg",
                 "potassium_mg",
                 "caffeine_mg"
                    -> value / 100.0;

            default -> value;
        };
    }


}
