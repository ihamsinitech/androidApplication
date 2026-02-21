package com.hlopg_backend.model;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hostels")
public class Hostel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hostel_id")
    private Long hostelId;
    
    @Column(name = "hostel_name", nullable = false)
    private String hostelName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "pg_type")
    private String pgType;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "area")
    private String area;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "state")
    private String state;
    
    @Column(name = "pincode")
    private String pincode;
    
    @Column(name = "price")
    private Integer price;
    
    @Column(name = "rent")
    private Integer rent;
    
    @Column(name = "sharing_data", columnDefinition = "TEXT")
    private String sharingData;
    
    @Column(name = "rules", columnDefinition = "TEXT")
    private String rules;
    
    @Column(name = "facilities", columnDefinition = "TEXT")
    private String facilities;
    
    @Column(name = "food_menu", columnDefinition = "TEXT")
    private String foodMenu;
    
    @Column(name = "images", columnDefinition = "TEXT")
    private String images;
    
    // ✅ CHANGE: Remove foreign key reference, just store owner ID as Long
    @Column(name = "owner_id")
    private Long ownerId;
    
    @Column(name = "owner_name")
    private String ownerName;
    
    @Column(name = "status")
    private String status = "ACTIVE";
    
    @Column(name = "rating")
    private Double rating = 4.5;
    
    @Column(name = "reviews")
    private Integer reviews = 0;
    
    @Column(name = "total_rooms")
    private Integer totalRooms = 20;
    
    @Column(name = "occupied_rooms")
    private Integer occupiedRooms = 0;
    
    @Column(name = "vacant_rooms")
    private Integer vacantRooms = 20;
    
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @Column(name = "updated_at")
@Temporal(TemporalType.TIMESTAMP)
private Date updatedAt;
    
    // PrePersist to set createdAt
    @jakarta.persistence.PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
    
    // Helper methods to handle JSON strings
    public Map<String, Object> getSharingDataAsMap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(sharingData, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
    
    public List<String> getImagesAsList() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(images, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Arrays.asList("/uploads/default_pg.jpg");
        }
    }

    // ===== FLOORS & ROOMS =====
@Column(name = "num_floors")
private Integer numFloors;

@Column(name = "rooms_per_floor")
private Integer roomsPerFloor;

@Column(name = "starting_room_number")
private Integer startingRoomNumber;

// ===== PAYMENT =====
@Column(name = "advance_amount")
private Integer advanceAmount;

@Column(name = "is_edited")
private Boolean isEdited = false;


}