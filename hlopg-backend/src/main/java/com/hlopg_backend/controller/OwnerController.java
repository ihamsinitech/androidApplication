package com.hlopg_backend.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hlopg_backend.repository.HostelRepository;
import com.hlopg_backend.repository.UserRepository;

@RestController
@RequestMapping("/api/owner")
// @CrossOrigin(origins = "*")
public class OwnerController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HostelRepository hostelRepository;
    
    // @GetMapping("/pgs")
    // public ResponseEntity<?> getOwnerPGs(@RequestHeader("Authorization") String authHeader) {
    //     System.out.println("👑 GET /api/owner/pgs called");
        
    //     try {
    //         // Extract owner ID from token
    //         Long ownerId = extractOwnerIdFromToken(authHeader);
    //         System.out.println("Owner ID from token: " + ownerId);
            
    //         // Find owner
    //         Optional<User> ownerOpt = userRepository.findById(ownerId);
    //         if (ownerOpt.isEmpty()) {
    //             return ResponseEntity.badRequest().body(createErrorResponse("Owner not found"));
    //         }
            
    //         User owner = ownerOpt.get();
            
    //         // Verify user is an owner
    //         if (!"OWNER".equals(owner.getUserType())) {
    //             return ResponseEntity.status(403).body(createErrorResponse("User is not an owner"));
    //         }
            
    //         // Get hostels for this owner
    //         List<Hostel> hostels = hostelRepository.findByOwnerId(ownerId);
    //         System.out.println("Found " + hostels.size() + " hostels for owner " + ownerId);
            
    //         // Prepare response
    //         List<Map<String, Object>> pgList = new ArrayList<>();
            
    //         for (Hostel hostel : hostels) {
    //             Map<String, Object> pgData = new HashMap<>();
    //             pgData.put("id", hostel.getId());
    //             pgData.put("name", hostel.getName());
    //             pgData.put("location", hostel.getLocation());
    //             pgData.put("city", hostel.getCity());
    //             pgData.put("state", hostel.getState());
    //             pgData.put("price", hostel.getPrice());
    //             pgData.put("capacity", hostel.getCapacity());
    //             pgData.put("availableRooms", hostel.getAvailableRooms());
    //             pgData.put("amenities", hostel.getAmenities() != null ? 
    //                 Arrays.asList(hostel.getAmenities().split(",")) : new ArrayList<>());
    //             pgData.put("images", hostel.getImages() != null ? 
    //                 Arrays.asList(hostel.getImages().split(",")) : new ArrayList<>());
    //             pgData.put("status", hostel.getStatus());
    //             pgData.put("createdAt", hostel.getCreatedAt());
    //             pgData.put("rating", hostel.getRating() != null ? hostel.getRating() : 0.0);
                
    //             pgList.add(pgData);
    //         }
            
    //         // If no hostels found, return test data
    //         if (pgList.isEmpty()) {
    //             System.out.println("⚠️ No hostels found, returning test data");
    //             pgList = getTestPGsData();
    //         }
            
    //         // Create success response
    //         Map<String, Object> response = new HashMap<>();
    //         response.put("success", true);
    //         response.put("message", "PGs fetched successfully");
    //         response.put("data", pgList);
    //         response.put("total", pgList.size());
    //         response.put("owner", Map.of(
    //             "id", owner.getId(),
    //             "name", owner.getName(),
    //             "email", owner.getEmail()
    //         ));
            
    //         return ResponseEntity.ok(response);
            
    //     } catch (Exception e) {
    //         System.err.println("❌ Error in /api/owner/pgs: " + e.getMessage());
    //         e.printStackTrace();
            
    //         // Return test data on error
    //         return ResponseEntity.ok(getTestResponse());
    //     }
    // }
    
    // Helper method to extract owner ID from token
    private Long extractOwnerIdFromToken(String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("⚠️ No valid auth header");
                return 1L; // Default for testing
            }
            
            String token = authHeader.substring(7);
            System.out.println("🔑 Token received: " + token);
            
            // Parse token format: hlopg_owner_123_timestamp OR hlopg_123_timestamp
            String[] parts = token.split("_");
            
            if (parts.length >= 3 && parts[0].equals("hlopg")) {
                if (parts[1].equals("owner")) {
                    // Format: hlopg_owner_123_timestamp
                    return Long.parseLong(parts[2]);
                } else {
                    // Format: hlopg_123_timestamp
                    return Long.parseLong(parts[1]);
                }
            }
            
            System.out.println("⚠️ Invalid token format, using default ID 1");
            return 1L;
            
        } catch (Exception e) {
            System.err.println("❌ Error extracting owner ID: " + e.getMessage());
            return 1L;
        }
    }
    
    // Test data for development
    private List<Map<String, Object>> getTestPGsData() {
        List<Map<String, Object>> testPGs = new ArrayList<>();
        
        // Test PG 1
        Map<String, Object> pg1 = new HashMap<>();
        pg1.put("id", 1);
        pg1.put("name", "Elite PG for Boys");
        pg1.put("location", "Gachibowli, Hyderabad");
        pg1.put("city", "Hyderabad");
        pg1.put("state", "Telangana");
        pg1.put("price", 8000);
        pg1.put("capacity", 20);
        pg1.put("availableRooms", 5);
        pg1.put("amenities", Arrays.asList("WiFi", "AC", "Food", "Laundry", "Security"));
        pg1.put("images", Arrays.asList(
            "https://via.placeholder.com/300x200/4A90E2/FFFFFF?text=PG+1",
            "https://via.placeholder.com/300x200/4A90E2/FFFFFF?text=Room+1"
        ));
        pg1.put("status", "active");
        pg1.put("rating", 4.5);
        pg1.put("createdAt", "2024-01-15T10:30:00");
        testPGs.add(pg1);
        
        // Test PG 2
        Map<String, Object> pg2 = new HashMap<>();
        pg2.put("id", 2);
        pg2.put("name", "Luxury Girls PG");
        pg2.put("location", "Hitech City, Hyderabad");
        pg2.put("city", "Hyderabad");
        pg2.put("state", "Telangana");
        pg2.put("price", 12000);
        pg2.put("capacity", 15);
        pg2.put("availableRooms", 3);
        pg2.put("amenities", Arrays.asList("WiFi", "AC", "Food", "Gym", "Laundry", "Security"));
        pg2.put("images", Arrays.asList(
            "https://via.placeholder.com/300x200/50C878/FFFFFF?text=PG+2",
            "https://via.placeholder.com/300x200/50C878/FFFFFF?text=Room+2"
        ));
        pg2.put("status", "active");
        pg2.put("rating", 4.8);
        pg2.put("createdAt", "2024-01-20T14:45:00");
        testPGs.add(pg2);
        
        // Test PG 3
        Map<String, Object> pg3 = new HashMap<>();
        pg3.put("id", 3);
        pg3.put("name", "Royal PG");
        pg3.put("location", "Madhapur, Hyderabad");
        pg3.put("city", "Hyderabad");
        pg3.put("state", "Telangana");
        pg3.put("price", 10000);
        pg3.put("capacity", 25);
        pg3.put("availableRooms", 10);
        pg3.put("amenities", Arrays.asList("WiFi", "Food", "Laundry", "Parking"));
        pg3.put("images", Arrays.asList(
            "https://via.placeholder.com/300x200/FF6B6B/FFFFFF?text=PG+3",
            "https://via.placeholder.com/300x200/FF6B6B/FFFFFF?text=Room+3"
        ));
        pg3.put("status", "active");
        pg3.put("rating", 4.2);
        pg3.put("createdAt", "2024-01-25T09:15:00");
        testPGs.add(pg3);
        
        return testPGs;
    }
    
    private Map<String, Object> getTestResponse() {
        List<Map<String, Object>> testData = getTestPGsData();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Test PGs data");
        response.put("data", testData);
        response.put("total", testData.size());
        response.put("owner", Map.of(
            "id", 1,
            "name", "Test Owner",
            "email", "owner@test.com"
        ));
        
        return response;
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        return error;
    }
}