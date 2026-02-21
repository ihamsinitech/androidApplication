package com.hlopg_backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
// @CrossOrigin(origins = "*")
public class DashboardController {
    
    @GetMapping("/owner")
    public ResponseEntity<?> getOwnerDashboard(@RequestHeader("Authorization") String authHeader) {
        System.out.println("📊 GET /api/dashboard/owner");
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalPGs", 5);
        dashboard.put("totalBookings", 23);
        dashboard.put("totalRevenue", 125000);
        dashboard.put("activeMembers", 18);
        dashboard.put("pendingRequests", 7);
        dashboard.put("vacantRooms", 12);
        dashboard.put("monthlyGrowth", 25);
        dashboard.put("occupancyRate", 78);
        
        // Monthly revenue data
        List<Map<String, Object>> monthlyRevenue = new ArrayList<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"};
        int[] revenues = {95000, 105000, 115000, 120000, 125000, 130000};
        
        for (int i = 0; i < months.length; i++) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", months[i]);
            monthData.put("revenue", revenues[i]);
            monthlyRevenue.add(monthData);
        }
        dashboard.put("monthlyRevenue", monthlyRevenue);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("dashboard", dashboard);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user")
    public ResponseEntity<?> getUserDashboard(@RequestHeader("Authorization") String authHeader) {
        System.out.println("📊 GET /api/dashboard/user");
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("bookedPGs", 2);
        dashboard.put("totalSpent", 25000);
        dashboard.put("pendingPayments", 1);
        dashboard.put("likedPGs", 5);
        dashboard.put("daysInCurrentPG", 45);
        dashboard.put("nextPaymentDate", "2024-02-05");
        dashboard.put("currentPGBalance", 5000);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("dashboard", dashboard);
        
        return ResponseEntity.ok(response);
    }
}