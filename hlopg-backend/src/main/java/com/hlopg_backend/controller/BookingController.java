// package com.hlopg_backend.controller;

// import java.util.ArrayList;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
// import java.util.UUID;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestHeader;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.hlopg_backend.model.Booking;
// import com.hlopg_backend.model.Hostel;
// import com.hlopg_backend.model.Notification;
// import com.hlopg_backend.model.User;
// import com.hlopg_backend.repository.BookingRepository;
// import com.hlopg_backend.repository.HostelRepository;
// import com.hlopg_backend.repository.NotificationRepository;
// import com.hlopg_backend.repository.UserRepository;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;

// @RestController
// @RequestMapping("/api/booking")
// // @CrossOrigin(origins = "*")
// public class BookingController {
    
//     @Autowired
//     private BookingRepository bookingRepository;
    
//     @Autowired
//     private HostelRepository hostelRepository;
    
//     @Autowired
//     private UserRepository userRepository;
    
//     @Autowired
//     private NotificationRepository notificationRepository;
    
//     @Value("${jwt.secret}")
//     private String jwtSecret;

//     // Create booking request
//     @PostMapping("/request")
//     public ResponseEntity<Map<String, Object>> createBookingRequest(
//             @RequestBody Map<String, Object> bookingRequest,
//             @RequestHeader("Authorization") String authHeader) {
        
//         Map<String, Object> response = new HashMap<>();
        
//         try {
//             System.out.println("📝 Received booking request: " + bookingRequest);
            
//             // Extract token and validate
//             String token = authHeader.replace("Bearer ", "");
            
//             Claims claims = Jwts.parserBuilder()
//                 .setSigningKey(jwtSecret.getBytes())
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody();
            
//             // Get user ID from token (Long)
//             String userIdStr = claims.getSubject();
//             Long userId = Long.parseLong(userIdStr);
            
//             // Verify user exists
//             Optional<User> userOptional = userRepository.findById(userId);
//             if (!userOptional.isPresent()) {
//                 response.put("success", false);
//                 response.put("message", "User not found");
//                 return ResponseEntity.badRequest().body(response);
//             }
            
//             User user = userOptional.get();
            
//             // Extract booking data from request
//             Long hostelId = Long.valueOf(bookingRequest.get("hostel_id").toString());
//             String sharingType = bookingRequest.get("sharing_type").toString();
            
//             // Verify hostel exists
//             Optional<Hostel> hostelOptional = hostelRepository.findById(hostelId);
//             if (!hostelOptional.isPresent()) {
//                 response.put("success", false);
//                 response.put("message", "Hostel not found");
//                 return ResponseEntity.badRequest().body(response);
//             }
            
//             Hostel hostel = hostelOptional.get();
            
//             // Generate unique booking ID
//             String generatedBookingId = "BR" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
//             // Create booking record
//             Booking booking = new Booking();
//             booking.setBookingId(generatedBookingId);
//             booking.setHostelId(hostelId);
//             booking.setUserId(userId);
//             booking.setUserName(bookingRequest.get("user_name").toString());
//             booking.setUserEmail(bookingRequest.get("user_email").toString());
//             booking.setUserPhone(bookingRequest.get("user_phone").toString());
//             booking.setSharingType(sharingType);
//             booking.setBookingDate(new Date());
//             booking.setStatus("pending");
//             booking.setCreatedAt(new Date());
            
//             bookingRepository.save(booking);
//             System.out.println("✅ Booking created: " + generatedBookingId);
            
//             // Create notification for owner
//             createOwnerNotification(hostel, user, booking);
            
//             response.put("success", true);
//             response.put("booking_id", generatedBookingId);
//             response.put("message", "Booking request created successfully");
            
//             return ResponseEntity.ok(response);
            
//         } catch (Exception e) {
//             e.printStackTrace();
//             response.put("success", false);
//             response.put("message", "Failed to create booking request: " + e.getMessage());
//             return ResponseEntity.badRequest().body(response);
//         }
//     }

//     // Helper method to create owner notification
//     // In BookingController.java - createOwnerNotification method
// private void createOwnerNotification(Hostel hostel, User user, Booking booking) {
//     try {
//         Notification notification = new Notification();
//         notification.setOwnerId(hostel.getOwnerId());
//         notification.setType("booking_request");
//         notification.setTitle("New Booking Request");
//         notification.setMessage(user.getName() + " wants to book " + hostel.getHostelName());
//         notification.setHostelId(hostel.getHostelId());
//         notification.setHostelName(hostel.getHostelName());
        
//         // Set hostel address
//         String address = "";
//         if (hostel.getAddress() != null && !hostel.getAddress().isEmpty()) {
//             address = hostel.getAddress();
//         } else if (hostel.getArea() != null && hostel.getCity() != null) {
//             address = hostel.getArea() + ", " + hostel.getCity();
//         }
//         notification.setHostelAddress(address);
        
//         notification.setUserId(user.getId());
//         notification.setUserName(user.getName());
//         notification.setUserEmail(user.getEmail());
//         notification.setUserPhone(user.getPhone());
//         notification.setSharingType(booking.getSharingType());
//         notification.setBookingId(booking.getBookingId());
//         notification.setRead(false);
//         notification.setCreatedAt(new Date());
        
//         notificationRepository.save(notification);
        
//         System.out.println("✅ Notification created for owner ID: " + hostel.getOwnerId());
//         System.out.println("📋 Notification details:");
//         System.out.println("   - User: " + user.getName());
//         System.out.println("   - Email: " + user.getEmail());
//         System.out.println("   - Phone: " + user.getPhone());
//         System.out.println("   - PG: " + hostel.getHostelName());
//         System.out.println("   - Address: " + address);
//         System.out.println("   - Sharing: " + booking.getSharingType());
        
//     } catch (Exception e) {
//         System.err.println("❌ Error creating notification: " + e.getMessage());
//         e.printStackTrace();
//     }
// }
//     // Get user bookings
//     @GetMapping("/user-bookings")
//     public ResponseEntity<Map<String, Object>> getUserBookings(@RequestHeader("Authorization") String authHeader) {
//         Map<String, Object> response = new HashMap<>();
        
//         try {
//             // Extract token and get user ID
//             String token = authHeader.replace("Bearer ", "");
            
//             Claims claims = Jwts.parserBuilder()
//                 .setSigningKey(jwtSecret.getBytes())
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody();
            
//             String userIdStr = claims.getSubject();
//             Long userId = Long.parseLong(userIdStr);
            
//             // Get bookings for this user
//             List<Booking> bookings = bookingRepository.findByUserId(userId);
            
//             // Format response
//             List<Map<String, Object>> formattedBookings = new ArrayList<>();
//             for (Booking booking : bookings) {
//                 Map<String, Object> bookingMap = new HashMap<>();
//                 bookingMap.put("bookingId", booking.getBookingId());
//                 bookingMap.put("hostelId", booking.getHostelId());
//                 bookingMap.put("userId", booking.getUserId());
//                 bookingMap.put("userName", booking.getUserName());
//                 bookingMap.put("userEmail", booking.getUserEmail());
//                 bookingMap.put("userPhone", booking.getUserPhone());
//                 bookingMap.put("sharingType", booking.getSharingType());
//                 bookingMap.put("bookingDate", booking.getBookingDate());
//                 bookingMap.put("status", booking.getStatus());
//                 bookingMap.put("createdAt", booking.getCreatedAt());
                
//                 // Get hostel details if needed
//                 Optional<Hostel> hostelOpt = hostelRepository.findById(booking.getHostelId());
//                 if (hostelOpt.isPresent()) {
//                     Hostel hostel = hostelOpt.get();
//                     bookingMap.put("hostelName", hostel.getHostelName());
//                     bookingMap.put("area", hostel.getArea());
//                     bookingMap.put("city", hostel.getCity());
//                     bookingMap.put("address", hostel.getAddress());
//                 }
                
//                 formattedBookings.add(bookingMap);
//             }
            
//             response.put("success", true);
//             response.put("bookings", formattedBookings);
            
//             return ResponseEntity.ok(response);
            
//         } catch (Exception e) {
//             e.printStackTrace();
//             response.put("success", false);
//             response.put("message", "Failed to fetch bookings: " + e.getMessage());
//             return ResponseEntity.badRequest().body(response);
//         }
//     }

//     // Get booking details
//     @GetMapping("/{bookingId}")
//     public ResponseEntity<Map<String, Object>> getBookingDetails(
//             @PathVariable String bookingId,
//             @RequestHeader("Authorization") String authHeader) {
        
//         Map<String, Object> response = new HashMap<>();
        
//         try {
//             // Find booking by ID
//             Optional<Booking> bookingOptional = bookingRepository.findByBookingId(bookingId);
//             if (!bookingOptional.isPresent()) {
//                 response.put("success", false);
//                 response.put("message", "Booking not found");
//                 return ResponseEntity.badRequest().body(response);
//             }
            
//             Booking booking = bookingOptional.get();
            
//             // Get hostel details
//             Optional<Hostel> hostelOptional = hostelRepository.findById(booking.getHostelId());
            
//             // Format response
//             Map<String, Object> bookingDetails = new HashMap<>();
//             bookingDetails.put("bookingId", booking.getBookingId());
//             bookingDetails.put("status", booking.getStatus());
//             bookingDetails.put("sharingType", booking.getSharingType());
//             bookingDetails.put("bookingDate", booking.getBookingDate());
//             bookingDetails.put("userName", booking.getUserName());
//             bookingDetails.put("userEmail", booking.getUserEmail());
//             bookingDetails.put("userPhone", booking.getUserPhone());
            
//             if (hostelOptional.isPresent()) {
//                 Hostel hostel = hostelOptional.get();
//                 bookingDetails.put("hostelName", hostel.getHostelName());
//                 bookingDetails.put("hostelAddress", hostel.getAddress());
//                 bookingDetails.put("hostelArea", hostel.getArea());
//                 bookingDetails.put("hostelCity", hostel.getCity());
//                 bookingDetails.put("hostelType", hostel.getPgType());
//             }
            
//             response.put("success", true);
//             response.put("data", bookingDetails);
            
//             return ResponseEntity.ok(response);
            
//         } catch (Exception e) {
//             e.printStackTrace();
//             response.put("success", false);
//             response.put("message", "Failed to get booking details");
//             return ResponseEntity.badRequest().body(response);
//         }
//     }

//     // Get booking status
//     @GetMapping("/status/{bookingId}")
//     public ResponseEntity<Map<String, Object>> getBookingStatus(@PathVariable String bookingId) {
//         Map<String, Object> response = new HashMap<>();
        
//         try {
//             Optional<Booking> bookingOptional = bookingRepository.findByBookingId(bookingId);
//             if (!bookingOptional.isPresent()) {
//                 response.put("success", false);
//                 response.put("message", "Booking not found");
//                 return ResponseEntity.badRequest().body(response);
//             }
            
//             Booking booking = bookingOptional.get();
            
//             response.put("success", true);
//             response.put("bookingId", booking.getBookingId());
//             response.put("status", booking.getStatus());
//             response.put("lastUpdated", booking.getCreatedAt());
            
//             return ResponseEntity.ok(response);
            
//         } catch (Exception e) {
//             response.put("success", false);
//             response.put("message", "Failed to get booking status");
//             return ResponseEntity.badRequest().body(response);
//         }
//     }
// }

package com.hlopg_backend.controller;

import com.hlopg_backend.model.BookingRequest;
import com.hlopg_backend.model.Hostel;
import com.hlopg_backend.repository.BookingRequestRepository;
import com.hlopg_backend.repository.HostelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    @Autowired
    private BookingRequestRepository bookingRepo;

    @Autowired
    private HostelRepository hostelRepo;

    /* ---------------- SEND BOOKING REQUEST ---------------- */
    @PostMapping("/request")
    public ResponseEntity<?> sendBookingRequest(@RequestBody Map<String, Object> req) {

        Long hostelId = Long.valueOf(req.get("hostelId").toString());

        Optional<Hostel> hostelOpt = hostelRepo.findById(hostelId);
        if (!hostelOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Hostel not found");
        }

        Hostel hostel = hostelOpt.get();

        BookingRequest br = new BookingRequest();
        br.setUserId(Long.valueOf(req.get("userId").toString()));
        br.setUserName(req.get("userName").toString());
        br.setUserEmail(req.get("userEmail").toString());
        br.setUserMobile(req.get("userMobile").toString());

        br.setHostelId(hostelId);
        br.setHostelName(hostel.getHostelName());
        br.setArea(hostel.getArea());
        br.setCity(hostel.getCity());
        br.setOwnerId(hostel.getOwnerId());

        bookingRepo.save(br);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Booking request sent successfully"
        ));
    }

    /* ---------------- OWNER NOTIFICATIONS ---------------- */
   @GetMapping("/owner-requests")
public ResponseEntity<?> getOwnerRequests(
        @RequestParam Long ownerId
) {
    System.out.println("OWNER ID RECEIVED = " + ownerId);

    List<BookingRequest> list =
            bookingRepo.findByOwnerIdOrderByCreatedAtDesc(ownerId);

    System.out.println("REQUEST COUNT = " + list.size());

    return ResponseEntity.ok(Map.of(
            "success", true,
            "requests", list
    ));
}



    /* ---------------- SINGLE REQUEST ---------------- */
    @GetMapping("/request/{id}")
    public ResponseEntity<?> getRequest(@PathVariable Long id) {
        return bookingRepo.findById(id)
                .map(r -> ResponseEntity.ok(Map.of("data", r)))
                .orElse(ResponseEntity.notFound().build());
    }

    /* ---------------- TOKEN PARSER ---------------- */
    private Long extractOwnerIdFromToken(String authHeader) {
        // hlopg_owner_3_timestamp
        String token = authHeader.replace("Bearer ", "");
        String[] parts = token.split("_");
        return Long.parseLong(parts[2]);
    }
}
