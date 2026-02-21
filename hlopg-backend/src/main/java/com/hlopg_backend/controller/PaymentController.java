// package com.hlopg_backend.controller;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestHeader;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.hlopg_backend.model.Payment;
// import com.hlopg_backend.service.PaymentService;

// @RestController
// @RequestMapping("/api/payments")
// public class PaymentController {
    
//     @Autowired
//     private PaymentService paymentService;
    
//     private String extractToken(String authHeader) {
//         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//             throw new RuntimeException("Invalid authorization header");
//         }
//         return authHeader.substring(7);
//     }
    
//     // Get all payments for owner
//     @GetMapping("/owner")
//     public ResponseEntity<?> getOwnerPayments(@RequestHeader("Authorization") String authHeader) {
//         try {
//             String token = extractToken(authHeader);
//             List<Payment> payments = paymentService.getPaymentsByOwner(token);
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("data", payments);
//             response.put("message", "Payments fetched successfully");
            
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             return errorResponse(e.getMessage());
//         }
//     }
    
//     // Update payment status
//     @PutMapping("/{paymentId}/status")
//     public ResponseEntity<?> updatePaymentStatus(
//             @PathVariable String paymentId,
//             @RequestBody Map<String, String> request,
//             @RequestHeader("Authorization") String authHeader) {
        
//         try {
//             String token = extractToken(authHeader);
//             String status = request.get("status");
            
//             Payment payment = paymentService.updatePaymentStatus(paymentId, status, token);
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("data", payment);
//             response.put("message", "Payment status updated");
            
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             return errorResponse(e.getMessage());
//         }
//     }
    
//     // Create new payment
//     @PostMapping("/create")
//     public ResponseEntity<?> createPayment(
//             @RequestBody Payment payment,
//             @RequestHeader("Authorization") String authHeader) {
        
//         try {
//             String token = extractToken(authHeader);
//             Payment createdPayment = paymentService.createPayment(payment, token);
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("data", createdPayment);
//             response.put("message", "Payment created successfully");
            
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             return errorResponse(e.getMessage());
//         }
//     }
    
//     // Get payment summary
//     @GetMapping("/summary")
//     public ResponseEntity<?> getPaymentSummary(@RequestHeader("Authorization") String authHeader) {
//         try {
//             String token = extractToken(authHeader);
//             Object summary = paymentService.getPaymentSummary(token);
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("data", summary);
//             response.put("message", "Payment summary fetched");
            
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             return errorResponse(e.getMessage());
//         }
//     }
    
//     // Get payments by status
//     @GetMapping("/status/{status}")
//     public ResponseEntity<?> getPaymentsByStatus(
//             @PathVariable String status,
//             @RequestHeader("Authorization") String authHeader) {
        
//         try {
//             String token = extractToken(authHeader);
//             List<Payment> payments = paymentService.getPaymentsByStatus(status, token);
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("data", payments);
//             response.put("message", status + " payments fetched");
            
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             return errorResponse(e.getMessage());
//         }
//     }
    
//     // Get overdue payments
//     @GetMapping("/overdue")
//     public ResponseEntity<?> getOverduePayments(@RequestHeader("Authorization") String authHeader) {
//         try {
//             String token = extractToken(authHeader);
//             List<Payment> payments = paymentService.getOverduePayments(token);
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("data", payments);
//             response.put("message", "Overdue payments fetched");
            
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             return errorResponse(e.getMessage());
//         }
//     }
    
//     // Get recent payments
//     @GetMapping("/recent")
//     public ResponseEntity<?> getRecentPayments(@RequestHeader("Authorization") String authHeader) {
//         try {
//             String token = extractToken(authHeader);
//             List<Payment> payments = paymentService.getRecentPayments(token);
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("data", payments);
//             response.put("message", "Recent payments fetched");
            
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             return errorResponse(e.getMessage());
//         }
//     }
    
//     // Test endpoint
//     @GetMapping("/test")
//     public ResponseEntity<?> test() {
//         Map<String, Object> response = new HashMap<>();
//         response.put("success", true);
//         response.put("message", "Payments API is working");
//         return ResponseEntity.ok(response);
//     }
    
//     private ResponseEntity<Map<String, Object>> errorResponse(String message) {
//         Map<String, Object> error = new HashMap<>();
//         error.put("success", false);
//         error.put("message", message);
//         return ResponseEntity.badRequest().body(error);
//     }
// }
package com.hlopg_backend.controller;

import com.hlopg_backend.model.Payment;
import com.hlopg_backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    // Get all payments for owner
    @GetMapping("/owner")
    public ResponseEntity<?> getOwnerPayments(@RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("💰 GET /api/payments/owner");
            System.out.println("🔑 Authorization Header: " + authHeader);
            
            if (authHeader == null || authHeader.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(errorResponse("Authorization header is required"));
            }
            
            // Extract token
            String token = authHeader;
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
            
            System.out.println("📝 Token to process: " + token);
            
            List<Payment> payments = paymentService.getPaymentsByOwner(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", payments);
            response.put("message", "Payments fetched successfully");
            response.put("count", payments.size());
            
            System.out.println("✅ Returning " + payments.size() + " payments");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error in /owner: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(400).body(errorResponse(e.getMessage()));
        }
    }
    
    // Test token parsing
    @GetMapping("/test-token")
    public ResponseEntity<?> testToken(@RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("🧪 GET /api/payments/test-token");
            System.out.println("🔑 Auth Header: " + authHeader);
            
            if (authHeader == null || authHeader.trim().isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "No authorization header",
                    "hasHeader", false
                ));
            }
            
            // Extract token
            String token = authHeader;
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
            
            System.out.println("📝 Token: " + token);
            System.out.println("📝 Token length: " + token.length());
            System.out.println("📝 Token contains dots: " + token.contains("."));
            
            Map<String, Object> testResult = paymentService.testToken(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("tokenTest", testResult);
            response.put("originalHeader", authHeader);
            response.put("processedToken", token);
            response.put("isBearer", authHeader.startsWith("Bearer "));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error in test-token: " + e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("error", "TOKEN_TEST_ERROR");
            
            return ResponseEntity.status(400).body(response);
        }
    }
    
    // Simple test endpoint
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Payments API is working");
        response.put("timestamp", System.currentTimeMillis());
        response.put("endpoints", List.of(
            "/api/payments/owner - GET (with Authorization header)",
            "/api/payments/test-token - GET (test token parsing)",
            "/api/payments/test - GET (simple test)"
        ));
        return ResponseEntity.ok(response);
    }
    
    // Update payment status
    @PutMapping("/{paymentId}/status")
    public ResponseEntity<?> updatePaymentStatus(
            @PathVariable String paymentId,
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            System.out.println("🔄 PUT /api/payments/" + paymentId + "/status");
            System.out.println("📝 Request: " + request);
            
            if (authHeader == null || authHeader.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(errorResponse("Authorization header is required"));
            }
            
            String token = authHeader;
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
            
            String status = request.get("status");
            
            if (status == null || (!status.equals("paid") && !status.equals("pending"))) {
                return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Status must be 'paid' or 'pending'")
                );
            }
            
            Payment payment = paymentService.updatePaymentStatus(paymentId, status, token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", payment);
            response.put("message", "Payment status updated to " + status);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error updating status: " + e.getMessage());
            return ResponseEntity.status(400).body(errorResponse(e.getMessage()));
        }
    }
    
    // Create new payment
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(
            @RequestBody Payment payment,
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            System.out.println("➕ POST /api/payments/create");
            System.out.println("📝 Payment data - Tenant: " + payment.getTenantName() + ", Amount: " + payment.getAmount());
            
            if (authHeader == null || authHeader.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(errorResponse("Authorization header is required"));
            }
            
            String token = authHeader;
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
            
            // Validate
            if (payment.getTenantName() == null || payment.getTenantName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Tenant name is required")
                );
            }
            
            if (payment.getAmount() == null || payment.getAmount() <= 0) {
                return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Valid amount is required")
                );
            }
            
            Payment createdPayment = paymentService.createPayment(payment, token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", createdPayment);
            response.put("message", "Payment created successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error creating payment: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }
    
    // Get payment summary
    @GetMapping("/summary")
    public ResponseEntity<?> getPaymentSummary(@RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("📊 GET /api/payments/summary");
            
            if (authHeader == null || authHeader.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(errorResponse("Authorization header is required"));
            }
            
            String token = authHeader;
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
            
            Map<String, Object> summary = paymentService.getPaymentSummary(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", summary);
            response.put("message", "Payment summary fetched successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error getting summary: " + e.getMessage());
            return ResponseEntity.status(400).body(errorResponse(e.getMessage()));
        }
    }
    
    private Map<String, Object> errorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        return error;
    }
}
