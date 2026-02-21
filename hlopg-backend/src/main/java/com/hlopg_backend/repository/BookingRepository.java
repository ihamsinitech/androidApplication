// package com.hlopg_backend.repository;

// import java.util.List;
// import java.util.Optional;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

// import com.hlopg_backend.model.Booking;

// @Repository
// public interface BookingRepository extends JpaRepository<Booking, Integer> {
    
//     // Find booking by booking ID
//     Optional<Booking> findByBookingId(String bookingId);
    
//     // Find bookings by user ID
//     List<Booking> findByUserId(Integer userId);
    
//     // Find bookings by hostel ID
//     List<Booking> findByHostelId(Integer hostelId);
    
//     // Find bookings by status
//     List<Booking> findByStatus(String status);
    
//     // Find bookings by user ID and status
//     List<Booking> findByUserIdAndStatus(Integer userId, String status);
    
//     // Find bookings by hostel ID and status
//     List<Booking> findByHostelIdAndStatus(Integer hostelId, String status);
// }

// BookingRepository.java
package com.hlopg_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hlopg_backend.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    Optional<Booking> findByBookingId(String bookingId);
    
    List<Booking> findByUserId(Long userId);
}