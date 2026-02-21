package com.hlopg_backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
// @CrossOrigin(origins = "*")
public class ReviewController {
    
    @GetMapping("/owner")
    public ResponseEntity<?> getOwnerReviews(@RequestHeader("Authorization") String authHeader) {
        System.out.println("⭐ GET /api/reviews/owner");
        
        List<Map<String, Object>> reviews = new ArrayList<>();
        
        String[] comments = {
            "Great PG, clean facilities and friendly staff.",
            "Good location, but WiFi could be better.",
            "Excellent food quality and maintenance.",
            "Affordable and well-maintained rooms.",
            "Owner is very cooperative and helpful.",
            "Need more parking space.",
            "Best PG in this area, highly recommended!"
        };
        
        String[] tenants = {"Rahul Sharma", "Priya Patel", "Amit Kumar", "Sneha Reddy", "Vikram Singh", "Anjali Mehta", "Rajesh Kumar"};
        
        for (int i = 0; i < 7; i++) {
            Map<String, Object> review = new HashMap<>();
            review.put("id", i + 1);
            review.put("tenantName", tenants[i]);
            review.put("pgName", "PG " + ((i % 3) + 1));
            review.put("rating", 4 + (i % 2));
            review.put("comment", comments[i]);
            review.put("date", "2024-01-" + (15 + i));
            review.put("response", i % 3 == 0 ? "Thank you for your feedback!" : null);
            review.put("responseDate", i % 3 == 0 ? "2024-01-" + (16 + i) : null);
            
            reviews.add(review);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("reviews", reviews);
        response.put("averageRating", 4.3);
        response.put("totalReviews", reviews.size());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestBody Map<String, Object> request, 
                                       @RequestHeader("Authorization") String authHeader) {
        System.out.println("📝 POST /api/reviews/add: " + request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Review submitted successfully");
        response.put("reviewId", "REV" + System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}