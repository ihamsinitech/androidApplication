package com.hlopg_backend.repository;

import com.hlopg_backend.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    // Find a specific like by user and hostel
    Optional<Like> findByUserIdAndHostelId(Long userId, Long hostelId);
    
    // Get all hostel IDs liked by a user
    @Query("SELECT l.hostelId FROM Like l WHERE l.userId = :userId")
    List<Long> findHostelIdsByUserId(@Param("userId") Long userId);
    
    // Delete a specific like
    void deleteByUserIdAndHostelId(Long userId, Long hostelId);
    
    // Check if a like exists
    boolean existsByUserIdAndHostelId(Long userId, Long hostelId);
    
    // Count likes for a hostel
    @Query("SELECT COUNT(l) FROM Like l WHERE l.hostelId = :hostelId")
    Long countByHostelId(@Param("hostelId") Long hostelId);
    
    // Count total likes by a user
    @Query("SELECT COUNT(l) FROM Like l WHERE l.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
}