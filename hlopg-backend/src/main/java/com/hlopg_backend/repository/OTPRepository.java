package com.hlopg_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import com.hlopg_backend.model.OTP;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findByIdentifierAndPurpose(String identifier, OTP.Purpose purpose);
    
    @Modifying
    @Query("UPDATE OTP o SET o.attempts = o.attempts + 1 WHERE o.id = :id")
    void incrementAttempts(@Param("id") Long id);
    
    @Modifying
    @Query("DELETE FROM OTP o WHERE o.expiresAt < :now")
    void deleteExpiredOTPs(@Param("now") LocalDateTime now);
}
