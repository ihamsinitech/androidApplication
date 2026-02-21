package com.hlopg_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hlopg_backend.model.Notification;
import com.hlopg_backend.repository.NotificationRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.*;

@RestController
@RequestMapping("/api/owner")
// @CrossOrigin(origins = "*")
public class NotificationController {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Get notifications for owner
    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> getOwnerNotifications(
            @RequestHeader("Authorization") String authHeader) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extract token and get owner ID
            String token = authHeader.replace("Bearer ", "");
            
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            String ownerIdStr = claims.getSubject();
            Long ownerId = Long.parseLong(ownerIdStr);
            
            // Get notifications for this owner, ordered by most recent first
            List<Notification> notifications = notificationRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId);
            
            // Format response
            List<Map<String, Object>> formattedNotifications = new ArrayList<>();
            for (Notification notification : notifications) {
                Map<String, Object> notifMap = new HashMap<>();
                notifMap.put("id", notification.getId());
                notifMap.put("type", notification.getType());
                notifMap.put("title", notification.getTitle());
                notifMap.put("message", notification.getMessage());
                notifMap.put("hostel_id", notification.getHostelId());
                notifMap.put("hostel_name", notification.getHostelName());
                notifMap.put("hostel_address", notification.getHostelAddress());
                notifMap.put("user_id", notification.getUserId());
                notifMap.put("user_name", notification.getUserName());
                notifMap.put("user_email", notification.getUserEmail());
                notifMap.put("user_phone", notification.getUserPhone());
                notifMap.put("sharing_type", notification.getSharingType());
                notifMap.put("booking_id", notification.getBookingId());
                notifMap.put("read", notification.getRead());
                notifMap.put("created_at", notification.getCreatedAt());
                
                formattedNotifications.add(notifMap);
            }
            
            response.put("success", true);
            response.put("data", formattedNotifications);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Failed to fetch notifications: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Mark notification as read
    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<Map<String, Object>> markNotificationAsRead(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Notification> notificationOptional = notificationRepository.findById(id);
            if (!notificationOptional.isPresent()) {
                response.put("success", false);
                response.put("message", "Notification not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            Notification notification = notificationOptional.get();
            notification.setRead(true);
            notificationRepository.save(notification);
            
            response.put("success", true);
            response.put("message", "Notification marked as read");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Failed to mark notification as read");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Mark all notifications as read for owner
    @PutMapping("/notifications/read-all")
    public ResponseEntity<Map<String, Object>> markAllNotificationsAsRead(
            @RequestHeader("Authorization") String authHeader) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extract token and get owner ID
            String token = authHeader.replace("Bearer ", "");
            
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            String ownerIdStr = claims.getSubject();
            Long ownerId = Long.parseLong(ownerIdStr);
            
            // Get all unread notifications for this owner
            List<Notification> unreadNotifications = notificationRepository.findByOwnerIdAndReadFalse(ownerId);
            
            // Mark all as read
            for (Notification notification : unreadNotifications) {
                notification.setRead(true);
            }
            notificationRepository.saveAll(unreadNotifications);
            
            response.put("success", true);
            response.put("message", "All notifications marked as read");
            response.put("count", unreadNotifications.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Failed to mark all notifications as read");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Count unread notifications
    @GetMapping("/notifications/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(
            @RequestHeader("Authorization") String authHeader) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extract token and get owner ID
            String token = authHeader.replace("Bearer ", "");
            
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            String ownerIdStr = claims.getSubject();
            Long ownerId = Long.parseLong(ownerIdStr);
            
            Long unreadCount = notificationRepository.countUnreadByOwnerId(ownerId);
            
            response.put("success", true);
            response.put("unread_count", unreadCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Failed to get unread count");
            return ResponseEntity.badRequest().body(response);
        }
    }
}