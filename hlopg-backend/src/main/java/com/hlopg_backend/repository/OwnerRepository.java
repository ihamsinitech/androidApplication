package com.hlopg_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hlopg_backend.model.Owner;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findByEmail(String email);
    Optional<Owner> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}