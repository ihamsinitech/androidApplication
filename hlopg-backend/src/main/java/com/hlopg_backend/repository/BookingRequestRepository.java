package com.hlopg_backend.repository;

import com.hlopg_backend.model.BookingRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRequestRepository extends JpaRepository<BookingRequest, Long> {

    List<BookingRequest> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
}
