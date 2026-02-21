// src/main/java/com/hlopg_backend/repository/RoomRepository.java
package com.hlopg_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hlopg_backend.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Page<Room> findByHostelId(Long hostelId, Pageable pageable);
    Long countByHostelId(Long hostelId);
    Long countByHostelIdAndStatus(Long hostelId, String status);
}