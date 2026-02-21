package com.hlopg_backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hlopg_backend.model.Owner;
import com.hlopg_backend.repository.OwnerRepository;

@Service
public class OwnerService {
    
    @Autowired
    private OwnerRepository ownerRepository;
    
    // Register a new owner
    public Owner registerOwner(Owner owner) {
        // Set userType if not already set
        if (owner.getUserType() == null) {
            owner.setUserType("OWNER");
        }
        return ownerRepository.save(owner);
    }
    
    // Find owner by email
    public Optional<Owner> findByEmail(String email) {
        return ownerRepository.findByEmail(email);
    }
    
    // Find owner by phone
    public Optional<Owner> findByPhone(String phone) {
        return ownerRepository.findByPhone(phone);
    }
    
    // Find owner by ID
    public Optional<Owner> findById(Long id) {
        return ownerRepository.findById(id);
    }
    
    // Update owner profile
    public Owner updateOwner(Long id, Owner updatedOwner) {
        Owner existingOwner = ownerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        // Update fields if provided
        if (updatedOwner.getName() != null) {
            existingOwner.setName(updatedOwner.getName());
        }
        if (updatedOwner.getEmail() != null) {
            existingOwner.setEmail(updatedOwner.getEmail());
        }
        if (updatedOwner.getPhone() != null) {
            existingOwner.setPhone(updatedOwner.getPhone());
        }
        if (updatedOwner.getPassword() != null) {
            existingOwner.setPassword(updatedOwner.getPassword());
        }
        
        return ownerRepository.save(existingOwner);
    }
    
    // Delete owner
    public void deleteOwner(Long id) {
        ownerRepository.deleteById(id);
    }
}