package com.foodScanner.scanner.controller;

import com.foodScanner.scanner.service.ApiService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5501") // Replace with your allowed origin(s)

public class ApiController {
    @Autowired
    private ApiService service;

    @GetMapping("/product_id")
    public Map<String, Object> getFoodDetails(@RequestParam("productDetails") String product_id){
        return service.fetchProductDetails(product_id);
    }
}
