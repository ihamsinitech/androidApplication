package com.hlopg_backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hlopg_backend.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    // Find payments by owner
    List<Payment> findByOwnerId(Long ownerId);
    
    // Find payment by payment ID
    Payment findByPaymentId(String paymentId);
    
    // Find payments by owner and status
    List<Payment> findByOwnerIdAndStatus(Long ownerId, String status);
    
    // Find payments by status
    List<Payment> findByStatus(String status);
    
    // Find payments by owner and PG
    List<Payment> findByOwnerIdAndPgId(Long ownerId, Long pgId);
    
    // Find payments by owner, PG and status
    List<Payment> findByOwnerIdAndPgIdAndStatus(Long ownerId, Long pgId, String status);
    
    // Find payments by tenant name (search)
    List<Payment> findByOwnerIdAndTenantNameContainingIgnoreCase(Long ownerId, String tenantName);
    
    // Find payments within date range
    @Query("SELECT p FROM Payment p WHERE p.ownerId = :ownerId AND p.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findByOwnerIdAndPaymentDateBetween(
            @Param("ownerId") Long ownerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    // Find payments by month and year
    List<Payment> findByOwnerIdAndMonthAndYear(Long ownerId, String month, Integer year);
    
    // Get total amount by owner and status
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.ownerId = :ownerId AND p.status = :status")
    Double getTotalAmountByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") String status);
    
    // Get total amount by owner
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.ownerId = :ownerId")
    Double getTotalAmountByOwnerId(@Param("ownerId") Long ownerId);
    
    // Get total amount by owner and PG
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.ownerId = :ownerId AND p.pgId = :pgId")
    Double getTotalAmountByOwnerIdAndPgId(@Param("ownerId") Long ownerId, @Param("pgId") Long pgId);
    
    // Find overdue payments
    @Query("SELECT p FROM Payment p WHERE p.ownerId = :ownerId AND p.status = 'pending' AND p.dueDate < CURRENT_DATE")
    List<Payment> findOverduePayments(@Param("ownerId") Long ownerId);
    
    // SIMPLE SOLUTION: Get recent payments without Pageable
    @Query(value = "SELECT * FROM payments WHERE owner_id = :ownerId ORDER BY created_at DESC LIMIT 10", nativeQuery = true)
    List<Payment> findRecentPayments(@Param("ownerId") Long ownerId);
    
    // Check if payment exists for tenant in month
    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.ownerId = :ownerId AND p.pgId = :pgId AND p.tenantName = :tenantName AND p.month = :month AND p.year = :year")
    boolean existsByOwnerIdAndPgIdAndTenantNameAndMonthAndYear(
            @Param("ownerId") Long ownerId,
            @Param("pgId") Long pgId,
            @Param("tenantName") String tenantName,
            @Param("month") String month,
            @Param("year") Integer year);
}