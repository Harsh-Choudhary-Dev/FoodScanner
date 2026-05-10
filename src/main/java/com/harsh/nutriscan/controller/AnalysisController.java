package com.harsh.nutriscan.controller;

import com.harsh.nutriscan.service.ImageStorageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analysis")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class AnalysisController {

    private final ImageStorageService imageStorageService;

    @PostMapping("/image")
    public Map<String, Object> imageInput(@RequestParam("image") MultipartFile image) throws IOException {
         return imageStorageService.analyzeProductFromImage(image);
    }

}
