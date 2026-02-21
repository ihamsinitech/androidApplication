package com.hlopg_backend.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hlopg_backend.model.OTP;
import com.hlopg_backend.repository.OTPRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OTPService {
    
    private final OTPRepository otpRepository;
    
    @Transactional
    public String generateOTP(String identifier, OTP.Purpose purpose) {
        String otpCode = String.format("%04d", new Random().nextInt(9999));
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);
        
        OTP otp = otpRepository.findByIdentifierAndPurpose(identifier, purpose)
                .map(existingOtp -> {
                    existingOtp.setOtpCode(otpCode);
                    existingOtp.setExpiresAt(expiresAt);
                    existingOtp.setVerifiedAt(null);
                    existingOtp.setAttempts(0);
                    return existingOtp;
                })
                .orElseGet(() -> OTP.builder()
                        .identifier(identifier)
                        .purpose(purpose)
                        .otpCode(otpCode)
                        .expiresAt(expiresAt)
                        .attempts(0)
                        .build());
        
        otpRepository.save(otp);
        
        // Print OTP to console (for development)
        System.out.println("OTP for " + identifier + ": " + otpCode);
        
        return otpCode;
    }
    
    // Add this method to fix the error
    @Transactional
    public void generateAndSendOTP(String identifier, OTP.Purpose purpose) {
        String otpCode = generateOTP(identifier, purpose);
        
        // In a real application, you would send the OTP via SMS or email
        // For now, just print it to console for development
        System.out.println("✅ OTP generated for " + identifier + ": " + otpCode);
        System.out.println("📱 In production, this would be sent via SMS to: " + identifier);
        
        // Uncomment when you implement actual SMS service
        // smsService.sendSMS(identifier, "Your OTP is: " + otpCode);
    }
    
    @Transactional
    public boolean verifyOTP(String identifier, String otpCode, OTP.Purpose purpose) {
        return otpRepository.findByIdentifierAndPurpose(identifier, purpose)
                .map(otp -> {
                    if (otp.getVerifiedAt() != null) {
                        throw new RuntimeException("OTP already verified");
                    }
                    
                    if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
                        throw new RuntimeException("OTP expired");
                    }
                    
                    if (otp.getAttempts() >= 5) {
                        throw new RuntimeException("Maximum OTP attempts exceeded");
                    }
                    
                    if (!otp.getOtpCode().equals(otpCode)) {
                        otp.setAttempts(otp.getAttempts() + 1);
                        otpRepository.save(otp);
                        throw new RuntimeException("Invalid OTP");
                    }
                    
                    otp.setVerifiedAt(LocalDateTime.now());
                    otpRepository.save(otp);
                    return true;
                })
                .orElseThrow(() -> new RuntimeException("OTP not found"));
    }
}