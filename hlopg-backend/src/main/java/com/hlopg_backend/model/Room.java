// src/main/java/com/hlopg_backend/model/Room.java
package com.hlopg_backend.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rooms")
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;
    
    @Column(name = "hostel_id")
    private Long hostelId;
    
    @Column(name = "room_number", nullable = false)
    private String roomNumber;
    
    @Column(name = "floor")
    private Integer floor = 1;
    
    @Column(name = "sharing_type")
    private String sharingType = "2-Sharing";
    
    @Column(name = "rent")
    private Integer rent = 5000;
    
    @Column(name = "status")
    private String status = "vacant";
    
    @Column(name = "tenant_name")
    private String tenantName;
    
    @Column(name = "tenant_phone")
    private String tenantPhone;
    
    @Column(name = "amenities", columnDefinition = "TEXT")
    private String amenities = "[]";
    
    @Column(name = "created_at")
    private Date createdAt;
    
    @Column(name = "updated_at")
    private Date updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}