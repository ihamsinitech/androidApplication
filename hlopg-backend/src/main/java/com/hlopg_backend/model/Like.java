// package com.hlopg_backend.model;



// import java.util.Date;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;
// import jakarta.persistence.UniqueConstraint;
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @Entity
// @Data
// @AllArgsConstructor
// @NoArgsConstructor
// @Table(name = "likes", uniqueConstraints = {
//     @UniqueConstraint(columnNames = {"user_id", "hostel_id"})
// })
// public class Like {
    
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;
    
//     @Column(name = "user_id", nullable = false)
//     private Long userId;
    
//     @Column(name = "hostel_id", nullable = false)
//     private Long hostelId;
    
//     @Column(name = "created_at")
//     // @Temporal(TemporalType.TIMESTAMP)
//     private Date createdAt;

// }

package com.hlopg_backend.model;


import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "likes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "hostel_id"})
})
public class Like {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "hostel_id", nullable = false)
    private Long hostelId;
    
    @Column(name = "created_at")
    // @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    // Constructors
    public Like() {
        this.createdAt = new Date();
    }
    
    public Like(Long userId, Long hostelId) {
        this.userId = userId;
        this.hostelId = hostelId;
        this.createdAt = new Date();
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getHostelId() { return hostelId; }
    public void setHostelId(Long hostelId) { this.hostelId = hostelId; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}