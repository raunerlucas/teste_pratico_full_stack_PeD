package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Controller that handles requests to the API root path ({@code /api}).
 *
 * <p>Prevents the "No static resource api" error by providing a valid JSON response
 * instead of letting Spring try to resolve {@code /api/} as a static resource.</p>
 */
@RestController
@RequestMapping("/api")
public class ApiRootController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> apiInfo() {
        return ResponseEntity.ok(Map.of(
                "application", "Gestão Industrial API",
                "version", "1.0.0",
                "status", "running",
                "timestamp", Instant.now().toString(),
                "endpoints", Map.of(
                        "rawMaterials", "/api/raw-materials",
                        "products", "/api/products",
                        "production", "/api/production/optimize",
                        "docs", "/swagger-ui/index.html"
                )
        ));
    }
}

