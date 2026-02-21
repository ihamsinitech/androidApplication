// package com.hlopg_backend.model;

// import java.time.LocalDate;
// import java.time.LocalDateTime;

// import org.hibernate.annotations.CreationTimestamp;
// import org.hibernate.annotations.UpdateTimestamp;

// import com.fasterxml.jackson.annotation.JsonIgnore;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.EnumType;
// import jakarta.persistence.Enumerated;
// import jakarta.persistence.FetchType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.Table;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @Entity
// @Table(name = "bookings")
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class Booking {
    
//     @Id
//     @Column(name = "booking_id")
//     private String bookingId;
    
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "user_id", nullable = false)
//     @JsonIgnore
//     private User user;
    
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "hostel_id", nullable = false)
//     @JsonIgnore
//     private Hostel hostel;
    
//     @Column(nullable = false)
//     private String sharing;
    
//     @Enumerated(EnumType.STRING)
//     @Column(name = "price_type", nullable = false)
//     private PriceType priceType;
    
//     @Column(name = "num_days", nullable = false)
//     private Integer numDays;
    
//     @Column(nullable = false)
//     private LocalDate date;
    
//     @Column(name = "rent_amount", nullable = false)
//     private Double rentAmount;
    
//     @Column(nullable = false)
//     private Double deposit;
    
//     @Column(name = "total_amount", nullable = false)
//     private Double totalAmount;
    
//     @Enumerated(EnumType.STRING)
//     @Column(nullable = false)
//     private BookingStatus status;
    
//     @Column(name = "razorpay_order_id")
//     private String razorpayOrderId;
    
//     @Column(name = "razorpay_payment_id")
//     private String razorpayPaymentId;
    
//     @Column(name = "razorpay_signature")
//     private String razorpaySignature;
    
//     @CreationTimestamp
//     @Column(name = "created_at")
//     private LocalDateTime createdAt;
    
//     @UpdateTimestamp
//     @Column(name = "updated_at")
//     private LocalDateTime updatedAt;
    
//     public enum PriceType {
//         DAILY, MONTHLY
//     }
    
//     public enum BookingStatus {
//         PENDING_PAYMENT, CONFIRMED, CANCELLED, COMPLETED
//     }
// }
// Booking.java
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
@Table(name = "bookings")
@Data
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "booking_id", unique = true, nullable = false)
    private String bookingId;
    
    @Column(name = "hostel_id", nullable = false)
    private Long hostelId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "user_name", nullable = false)
    private String userName;
    
    @Column(name = "user_email", nullable = false)
    private String userEmail;
    
    @Column(name = "user_phone", nullable = false)
    private String userPhone;
    
    @Column(name = "sharing_type", nullable = false)
    private String sharingType;
    
    @Column(name = "booking_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date bookingDate;
    
    @Column(name = "status", nullable = false)
    private String status; // pending, confirmed, cancelled, completed
    
    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    // PrePersist to set createdAt
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = new Date();
        }
        if (bookingDate == null) {
            bookingDate = new Date();
        }
        if (status == null) {
            status = "pending";
        }
    }
}