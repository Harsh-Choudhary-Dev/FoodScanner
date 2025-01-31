//package com.foodScanner.scanner.service;
//import com.foodScanner.scanner.functions.ScrapLinks;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import java.util.HashMap;
//import java.util.Map;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//@Data
//public class ApiService {
//
//    private final ScrapLinks scrapLinks;
//
//    @Autowired
//    public ApiService(ScrapLinks scrapLinks) {
//        this.scrapLinks = scrapLinks;  // Constructor injection
//    }
//
//    public Map<String, Object> fetchProductDetails(String product_id) {
//        Map<String, Object> productDetails = new HashMap<>();
//
//        assert scrapLinks != null;
//        String productDescription = scrapLinks.getProductDescription(product_id);
//
//        productDetails.put("product_id", product_id);
//        productDetails.put("description", productDescription);
//
//        return productDetails;
//    }
//
//}
