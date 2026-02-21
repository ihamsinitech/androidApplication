// package com.hlopg_backend.repository;

// import java.util.List;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import com.hlopg_backend.model.Notification;

// @Repository
// public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
//     // Find notifications by owner ID, ordered by most recent first
//     List<Notification> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
    
//     // Find unread notifications for an owner
//     List<Notification> findByOwnerIdAndReadFalse(Long ownerId);
    
//     // Count unread notifications for an owner
//     @Query("SELECT COUNT(n) FROM Notification n WHERE n.ownerId = :ownerId AND n.read = false")
//     Long countUnreadByOwnerId(@Param("ownerId") Long ownerId);
    
//     // Find notification by booking ID
//     Notification findByBookingId(String bookingId);
    
//     // Find notifications by user ID
//     List<Notification> findByUserId(Long userId);
// }

// NotificationRepository.java
package com.hlopg_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hlopg_backend.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Find notifications by owner ID, ordered by most recent first
    List<Notification> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
    
    // Find unread notifications for an owner
    List<Notification> findByOwnerIdAndReadFalse(Long ownerId);
    
    // Count unread notifications for an owner
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.ownerId = :ownerId AND n.read = false")
    Long countUnreadByOwnerId(@Param("ownerId") Long ownerId);
    
    // Find notification by booking ID
    Notification findByBookingId(String bookingId);
    
    // Find notifications by user ID
    List<Notification> findByUserId(Long userId);
}