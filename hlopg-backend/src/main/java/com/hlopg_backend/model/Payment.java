package com.hlopg_backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "payment_id", unique = true, nullable = false)
    private String paymentId;
    
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
    
    @Column(name = "tenant_name", nullable = false)
    private String tenantName;
    
    @Column(name = "tenant_phone")
    private String tenantPhone;
    
    @Column(name = "tenant_email")
    private String tenantEmail;
    
    @Column(name = "pg_id")
    private Long pgId;
    
    @Column(name = "pg_name")
    private String pgName;
    
    @Column(name = "room_number")
    private String roomNumber;
    
    @Column(name = "amount", nullable = false)
    private Double amount;
    
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    
    @Column(name = "due_date")
    private LocalDate dueDate;
    
    @Column(name = "status", nullable = false)
    private String status = "pending"; // "paid", "pending", "overdue"
    
    @Column(name = "payment_method")
    private String paymentMethod; // "cash", "online", "bank_transfer", "upi"
    
    @Column(name = "sharing_type")
    private String sharingType; // "single", "double", "triple"
    
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Column(name = "receipt_url")
    private String receiptUrl;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "month")
    private String month; // e.g., "January 2024"
    
    @Column(name = "year")
    private Integer year;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        if (paymentId == null) {
            paymentId = "PAY" + System.currentTimeMillis() + (int)(Math.random() * 1000);
        }
        
        if (paymentDate == null) {
            paymentDate = LocalDate.now();
        }
        
        // Set month and year from payment date
        if (paymentDate != null) {
            month = paymentDate.getMonth().toString();
            year = paymentDate.getYear();
        }
        
        // Set due date if not provided (default to payment date + 30 days)
        if (dueDate == null && paymentDate != null) {
            dueDate = paymentDate.plusDays(30);
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        
        // Update month and year if payment date changed
        if (paymentDate != null) {
            month = paymentDate.getMonth().toString();
            year = paymentDate.getYear();
        }
        
        // Check if payment is overdue
        if (status.equals("pending") && dueDate != null && dueDate.isBefore(LocalDate.now())) {
            status = "overdue";
        }
    }
}