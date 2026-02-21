package com.hlopg_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OTP {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String identifier;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Purpose purpose;
    
    @Column(name = "otp_code", nullable = false, length = 6)
    private String otpCode;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
    
    @Column(nullable = false)
    private Integer attempts = 0;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum Purpose {
        REGISTRATION, LOGIN, PASSWORD_RESET, EMAIL_VERIFICATION
    }
}