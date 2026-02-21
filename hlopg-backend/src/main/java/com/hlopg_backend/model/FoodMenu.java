package com.hlopg_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "food_menus")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodMenu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id", nullable = false, unique = true)
    private Hostel hostel;
    
    @Column(columnDefinition = "json")
    private String breakfast; // JSON: {"monday": "Idli", "tuesday": "Dosa"...}
    
    @Column(columnDefinition = "json")
    private String lunch; // JSON format
    
    @Column(columnDefinition = "json")
    private String dinner; // JSON format
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
