// package com.hlopg_backend.service;

// import java.time.LocalDate;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.hlopg_backend.model.Payment;
// import com.hlopg_backend.model.User;
// import com.hlopg_backend.repository.PaymentRepository;
// import com.hlopg_backend.repository.UserRepository;

// @Service
// public class PaymentService {
    
//     @Autowired
//     private PaymentRepository paymentRepository;
    
//     @Autowired
//     private JWTService jwtService;
    
//     @Autowired
//     private UserRepository userRepository;
    
//     // Get owner from token
//     private User getOwnerFromToken(String token) {
//         if (token.startsWith("Bearer ")) {
//             token = token.substring(7);
//         }
//         String email = jwtService.extractEmail(token);
//         return userRepository.findByEmail(email)
//                 .orElseThrow(() -> new RuntimeException("User not found"));
//     }
    
//     // ==================== CORE METHODS ====================
    
//     // Get all payments for owner
//     public List<Payment> getPaymentsByOwner(String token) {
//         User owner = getOwnerFromToken(token);
//         return paymentRepository.findByOwnerId(owner.getId());
//     }
    
//     // Update payment status
//     public Payment updatePaymentStatus(String paymentId, String status, String token) {
//         User owner = getOwnerFromToken(token);
//         Payment payment = paymentRepository.findByPaymentId(paymentId);
        
//         if (payment == null) {
//             throw new RuntimeException("Payment not found");
//         }
        
//         if (!payment.getOwnerId().equals(owner.getId())) {
//             throw new RuntimeException("Unauthorized");
//         }
        
//         payment.setStatus(status);
//         return paymentRepository.save(payment);
//     }
    
//     // Create new payment
//     public Payment createPayment(Payment payment, String token) {
//         User owner = getOwnerFromToken(token);
//         payment.setOwnerId(owner.getId());
        
//         // Set default values
//         if (payment.getStatus() == null) {
//             payment.setStatus("pending");
//         }
//         if (payment.getPaymentDate() == null) {
//             payment.setPaymentDate(LocalDate.now());
//         }
        
//         return paymentRepository.save(payment);
//     }
    
//     // Get payment by ID
//     public Payment getPaymentById(String paymentId, String token) {
//         User owner = getOwnerFromToken(token);
//         Payment payment = paymentRepository.findByPaymentId(paymentId);
        
//         if (payment == null) {
//             throw new RuntimeException("Payment not found");
//         }
        
//         if (!payment.getOwnerId().equals(owner.getId())) {
//             throw new RuntimeException("Unauthorized");
//         }
        
//         return payment;
//     }
    
//     // Delete payment
//     public boolean deletePayment(String paymentId, String token) {
//         User owner = getOwnerFromToken(token);
//         Payment payment = paymentRepository.findByPaymentId(paymentId);
        
//         if (payment == null) {
//             throw new RuntimeException("Payment not found");
//         }
        
//         if (!payment.getOwnerId().equals(owner.getId())) {
//             throw new RuntimeException("Unauthorized");
//         }
        
//         paymentRepository.delete(payment);
//         return true;
//     }
    
//     // ==================== QUERY METHODS ====================
    
//     // Get payments by status
//     public List<Payment> getPaymentsByStatus(String status, String token) {
//         User owner = getOwnerFromToken(token);
//         return paymentRepository.findByOwnerIdAndStatus(owner.getId(), status);
//     }
    
//     // Get overdue payments
//     public List<Payment> getOverduePayments(String token) {
//         User owner = getOwnerFromToken(token);
//         return paymentRepository.findOverduePayments(owner.getId());
//     }
    
//     // Get recent payments (last 10)
//     public List<Payment> getRecentPayments(String token) {
//         User owner = getOwnerFromToken(token);
//         return paymentRepository.findRecentPayments(owner.getId());
//     }
    
//     // Get payment summary - FIXED VERSION
//     public Map<String, Object> getPaymentSummary(String token) {
//         User owner = getOwnerFromToken(token);
        
//         List<Payment> allPayments = paymentRepository.findByOwnerId(owner.getId());
//         List<Payment> pendingPayments = paymentRepository.findByOwnerIdAndStatus(owner.getId(), "pending");
//         List<Payment> paidPayments = paymentRepository.findByOwnerIdAndStatus(owner.getId(), "paid");
        
//         double totalAmount = allPayments.stream().mapToDouble(Payment::getAmount).sum();
//         double pendingAmount = pendingPayments.stream().mapToDouble(Payment::getAmount).sum();
//         double paidAmount = paidPayments.stream().mapToDouble(Payment::getAmount).sum();
        
//         // Create a map instead of anonymous object
//         Map<String, Object> summary = new HashMap<>();
//         summary.put("totalAmount", totalAmount);
//         summary.put("pendingAmount", pendingAmount);
//         summary.put("paidAmount", paidAmount);
//         summary.put("totalPayments", allPayments.size());
//         summary.put("pendingCount", pendingPayments.size());
//         summary.put("paidCount", paidPayments.size());
        
//         return summary;
//     }
    
//     // Mark overdue payments
//     public void markOverduePayments() {
//         List<Payment> pendingPayments = paymentRepository.findByStatus("pending");
//         LocalDate today = LocalDate.now();
        
//         for (Payment payment : pendingPayments) {
//             if (payment.getDueDate() != null && payment.getDueDate().isBefore(today)) {
//                 payment.setStatus("overdue");
//                 paymentRepository.save(payment);
//             }
//         }
//     }
    
//     // Search payments by tenant name
//     public List<Payment> searchPaymentsByTenant(String tenantName, String token) {
//         User owner = getOwnerFromToken(token);
//         return paymentRepository.findByOwnerIdAndTenantNameContainingIgnoreCase(owner.getId(), tenantName);
//     }
    
//     // Get payments by PG
//     public List<Payment> getPaymentsByPg(Long pgId, String token) {
//         User owner = getOwnerFromToken(token);
//         return paymentRepository.findByOwnerIdAndPgId(owner.getId(), pgId);
//     }
// }

package com.hlopg_backend.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hlopg_backend.model.Payment;
import com.hlopg_backend.model.User;
import com.hlopg_backend.repository.PaymentRepository;
import com.hlopg_backend.repository.UserRepository;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Get owner from CUSTOM TOKEN (hlopg_owner_{id}_{timestamp})
    private User getOwnerFromToken(String token) {
        try {
            System.out.println("🔑 Processing custom token: " + token);
            
            // Remove "Bearer " prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            System.out.println("📝 Token after cleanup: " + token);
            
            // Parse custom token format: hlopg_owner_{id}_{timestamp}
            // Example: hlopg_owner_5_1769837248486
            String[] parts = token.split("_");
            
            if (parts.length < 3) {
                throw new RuntimeException("Invalid token format. Expected: hlopg_owner_{id}_{timestamp}");
            }
            
            // parts[0] = "hlopg"
            // parts[1] = "owner" 
            // parts[2] = ownerId (e.g., "5")
            // parts[3] = timestamp (optional)
            
            try {
                Long ownerId = Long.parseLong(parts[2]);
                System.out.println("👤 Extracted owner ID: " + ownerId);
                
                // Find user by ID
                return userRepository.findById(ownerId)
                        .orElseThrow(() -> new RuntimeException("User not found with ID: " + ownerId));
                        
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid owner ID in token: " + parts[2]);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error getting owner from token: " + e.getMessage());
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }
    
    // Get all payments for owner
    public List<Payment> getPaymentsByOwner(String token) {
        try {
            System.out.println("📊 Fetching payments for owner with token: " + token);
            
            User owner = getOwnerFromToken(token);
            System.out.println("✅ Owner found: " + owner.getName() + " (ID: " + owner.getId() + ", Email: " + owner.getEmail() + ")");
            
            List<Payment> payments = paymentRepository.findByOwnerId(owner.getId());
            System.out.println("✅ Found " + payments.size() + " payments");
            
            return payments;
        } catch (Exception e) {
            System.err.println("❌ Error in getPaymentsByOwner: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    // Update payment status
    public Payment updatePaymentStatus(String paymentId, String status, String token) {
        try {
            System.out.println("🔄 Updating payment " + paymentId + " to status: " + status);
            
            User owner = getOwnerFromToken(token);
            Payment payment = paymentRepository.findByPaymentId(paymentId);
            
            if (payment == null) {
                throw new RuntimeException("Payment not found");
            }
            
            if (!payment.getOwnerId().equals(owner.getId())) {
                throw new RuntimeException("Unauthorized - Payment belongs to different owner");
            }
            
            payment.setStatus(status);
            Payment updated = paymentRepository.save(payment);
            
            System.out.println("✅ Payment status updated successfully");
            return updated;
        } catch (Exception e) {
            System.err.println("❌ Error updating payment status: " + e.getMessage());
            throw e;
        }
    }
    
    // Create new payment
    public Payment createPayment(Payment payment, String token) {
        try {
            System.out.println("➕ Creating new payment for tenant: " + payment.getTenantName());
            
            User owner = getOwnerFromToken(token);
            payment.setOwnerId(owner.getId());
            
            // Set default values
            if (payment.getStatus() == null || payment.getStatus().isEmpty()) {
                payment.setStatus("pending");
            }
            if (payment.getPaymentDate() == null) {
                payment.setPaymentDate(LocalDate.now());
            }
            if (payment.getPaymentId() == null) {
                // Generate payment ID
                payment.setPaymentId("PAY" + System.currentTimeMillis() + (int)(Math.random() * 1000));
            }
            
            Payment saved = paymentRepository.save(payment);
            System.out.println("✅ Payment created with ID: " + saved.getPaymentId() + ", Amount: ₹" + saved.getAmount());
            
            return saved;
        } catch (Exception e) {
            System.err.println("❌ Error creating payment: " + e.getMessage());
            throw e;
        }
    }
    
    // Get payment summary
    public Map<String, Object> getPaymentSummary(String token) {
        try {
            System.out.println("📈 Getting payment summary");
            
            User owner = getOwnerFromToken(token);
            
            List<Payment> allPayments = paymentRepository.findByOwnerId(owner.getId());
            List<Payment> pendingPayments = paymentRepository.findByOwnerIdAndStatus(owner.getId(), "pending");
            List<Payment> paidPayments = paymentRepository.findByOwnerIdAndStatus(owner.getId(), "paid");
            
            double totalAmount = allPayments.stream().mapToDouble(Payment::getAmount).sum();
            double pendingAmount = pendingPayments.stream().mapToDouble(Payment::getAmount).sum();
            double paidAmount = paidPayments.stream().mapToDouble(Payment::getAmount).sum();
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalAmount", totalAmount);
            summary.put("pendingAmount", pendingAmount);
            summary.put("paidAmount", paidAmount);
            summary.put("totalPayments", allPayments.size());
            summary.put("pendingCount", pendingPayments.size());
            summary.put("paidCount", paidPayments.size());
            
            System.out.println("✅ Summary calculated: " + summary);
            return summary;
        } catch (Exception e) {
            System.err.println("❌ Error getting summary: " + e.getMessage());
            throw e;
        }
    }
    
    // Test method to verify token parsing
    public Map<String, Object> testToken(String token) {
        try {
            System.out.println("🧪 Testing token: " + token);
            
            User owner = getOwnerFromToken(token);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("ownerId", owner.getId());
            result.put("ownerName", owner.getName());
            result.put("ownerEmail", owner.getEmail());
            result.put("message", "Token parsed successfully");
            
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("message", "Token parsing failed");
            
            return result;
        }
    }
}