// Notification.java
package com.hlopg_backend.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "notifications")
@Data
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "owner_id")
    private Long ownerId;
    
    @Column(name = "type")
    private String type; // booking_request, payment_received, maintenance_alert, review_received, pg_approval
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "message")
    private String message;
    
    @Column(name = "hostel_id")
    private Long hostelId;
    
    @Column(name = "hostel_name")
    private String hostelName;
    
    @Column(name = "hostel_address")
    private String hostelAddress;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "user_name")
    private String userName;
    
    @Column(name = "user_email")
    private String userEmail;
    
    @Column(name = "user_phone")
    private String userPhone;
    
    @Column(name = "sharing_type")
    private String sharingType;
    
    @Column(name = "booking_id")
    private String bookingId;
    
    @Column(name = "is_read")
    private Boolean read = false;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    // PrePersist to set createdAt
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = new Date();
        }
        if (read == null) {
            read = false;
        }
    }
}