

package com.hlopg_backend.controller;

import com.hlopg_backend.service.HostelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hlopg_backend.model.Hostel;
import com.hlopg_backend.model.Like;
import com.hlopg_backend.repository.HostelRepository;
import com.hlopg_backend.repository.LikeRepository;

@RestController
@RequestMapping("/api/hostel")
// @CrossOrigin(origins = "*")
public class HostelController {
    
    @Autowired
    private HostelService hostelService;

    @Autowired
    private LikeRepository likeRepository;

    
     @Autowired
    private HostelRepository hostelRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // GET all hostels (for home page)
    @GetMapping("/gethostels")
    public ResponseEntity<?> getHostels(
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String area,
        @RequestParam(required = false) Integer minPrice,
        @RequestParam(required = false) Integer maxPrice
    ) {
        try {
            List<Hostel> hostels = hostelService.getAllHostels(city, area, minPrice, maxPrice);
            List<Map<String, Object>> responseList = new ArrayList<>();
            
            for (Hostel hostel : hostels) {
                responseList.add(convertToResponseMap(hostel));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Hostels fetched successfully");
            response.put("hostels", responseList);
            response.put("total", responseList.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("Failed to fetch hostels: " + e.getMessage()));
        }
    }
    
    // GET owner's PGs
    @GetMapping("/owner/pgs")
    public ResponseEntity<?> getOwnerPGs(@RequestHeader("Authorization") String authHeader) {
        try {
            Long ownerId = extractOwnerIdFromToken(authHeader);
            List<Hostel> hostels = hostelService.getHostelsByOwnerId(ownerId);
            List<Map<String, Object>> responseList = new ArrayList<>();
            
            for (Hostel hostel : hostels) {
                responseList.add(convertToResponseMap(hostel));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "PGs fetched successfully");
            response.put("data", responseList);
            response.put("total", responseList.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("Failed to fetch PGs: " + e.getMessage()));
        }
    }
    
    // GET single hostel by ID
    @GetMapping("/{hostelId}")
    public ResponseEntity<?> getHostelById(@PathVariable Long hostelId) {
        try {
            Optional<Hostel> hostelOpt = hostelService.getHostelById(hostelId);
            
            if (hostelOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Hostel fetched successfully");
                response.put("data", convertToResponseMap(hostelOpt.get()));
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(404).body(errorResponse("Hostel not found"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("Failed to fetch hostel"));
        }
    }
    
    // POST - Add new PG/Hostel
    @PostMapping("/addhostel")
    public ResponseEntity<?> addHostel(
        @RequestParam("pgName") String pgName,
        @RequestParam("pgInfo") String pgInfo,
        @RequestParam("pgType") String pgType,
        @RequestParam("address") String address,
        @RequestParam("area") String area,
        @RequestParam("city") String city,
        @RequestParam("state") String state,
        @RequestParam("pincode") String pincode,
        @RequestParam("sharing") String sharing,
        @RequestParam("rules") String rules,
        @RequestParam("furnish") String furnish,
        @RequestParam("foodMenu") String foodMenu,
        @RequestParam(value = "images", required = false) MultipartFile[] images,
        @RequestHeader("Authorization") String authHeader
    ) {
        try {
            Long ownerId = extractOwnerIdFromToken(authHeader);
            
            // Extract minimum price from sharing
            Integer minPrice = 5000;
            try {
                Map<String, Integer> sharingMap = objectMapper.readValue(sharing, 
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Integer>>() {});
                minPrice = sharingMap.values().stream().min(Integer::compare).orElse(5000);
            } catch (Exception e) {
                // Use default price
            }
            
            // Create hostel object
            Hostel hostel = new Hostel();
            hostel.setHostelName(pgName);
            hostel.setDescription(pgInfo);
            hostel.setPgType(pgType);
            hostel.setAddress(address);
            hostel.setArea(area);
            hostel.setCity(city);
            hostel.setState(state);
            hostel.setPincode(pincode);
            hostel.setPrice(minPrice);
            hostel.setRent(minPrice);
            hostel.setSharingData(sharing);
            hostel.setRules(rules);
            hostel.setFacilities(furnish);
            hostel.setFoodMenu(foodMenu);
            hostel.setOwnerId(ownerId);
            hostel.setOwnerName("Owner " + ownerId);
            
            // Save hostel
            Hostel savedHostel = hostelService.saveHostel(hostel, images);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "PG added successfully!");
            response.put("hostel_id", savedHostel.getHostelId());
            response.put("hostel", convertToResponseMap(savedHostel));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(errorResponse("Failed to add PG: " + e.getMessage()));
        }
    }
    
    // PUT - Update hostel with images support
@PutMapping("/update/{hostelId}")
public ResponseEntity<?> updateHostel(
    @PathVariable Long hostelId,
    @RequestParam(value = "pgName", required = false) String pgName,
    @RequestParam(value = "pgInfo", required = false) String pgInfo,
    @RequestParam(value = "pgType", required = false) String pgType,
    @RequestParam(value = "address", required = false) String address,
    @RequestParam(value = "area", required = false) String area,
    @RequestParam(value = "city", required = false) String city,
    @RequestParam(value = "state", required = false) String state,
    @RequestParam(value = "pincode", required = false) String pincode,
    @RequestParam(value = "sharing", required = false) String sharing,
    @RequestParam(value = "rules", required = false) String rules,
    @RequestParam(value = "furnish", required = false) String furnish,
    @RequestParam(value = "foodMenu", required = false) String foodMenu,
    @RequestParam(value = "numFloors", required = false) String numFloors,
    @RequestParam(value = "roomsPerFloor", required = false) String roomsPerFloor,
    @RequestParam(value = "startingRoomNumber", required = false) String startingRoomNumber,
    @RequestParam(value = "advanceAmount", required = false) String advanceAmount,
    @RequestParam(value = "images", required = false) MultipartFile[] images,
    @RequestHeader("Authorization") String authHeader
) {
    try {
        Long ownerId = extractOwnerIdFromToken(authHeader);
        
        System.out.println("🔄 Updating hostel ID: " + hostelId);
        System.out.println("📋 Received " + (images != null ? images.length : 0) + " images");
        
        // Get existing hostel
        Optional<Hostel> existingHostelOpt = hostelService.getHostelById(hostelId);
        if (!existingHostelOpt.isPresent()) {
            System.out.println("❌ Hostel not found with ID: " + hostelId);
            return ResponseEntity.status(404).body(errorResponse("Hostel not found"));
        }
        
        Hostel existingHostel = existingHostelOpt.get();
        
        // Verify ownership
        if (!existingHostel.getOwnerId().equals(ownerId)) {
            System.out.println("❌ Unauthorized: Owner ID mismatch. Expected: " + existingHostel.getOwnerId() + ", Got: " + ownerId);
            return ResponseEntity.status(403).body(errorResponse("You are not authorized to update this hostel"));
        }
        
        // Prepare form data map
        Map<String, String> formData = new HashMap<>();
        if (pgName != null) formData.put("pgName", pgName);
        if (pgInfo != null) formData.put("pgInfo", pgInfo);
        if (pgType != null) formData.put("pgType", pgType);
        if (address != null) formData.put("address", address);
        if (area != null) formData.put("area", area);
        if (city != null) formData.put("city", city);
        if (state != null) formData.put("state", state);
        if (pincode != null) formData.put("pincode", pincode);
        if (sharing != null) formData.put("sharing", sharing);
        if (rules != null) formData.put("rules", rules);
        if (furnish != null) formData.put("furnish", furnish);
        if (foodMenu != null) formData.put("foodMenu", foodMenu);
        if (numFloors != null) formData.put("numFloors", numFloors);
        if (roomsPerFloor != null) formData.put("roomsPerFloor", roomsPerFloor);
        if (startingRoomNumber != null) formData.put("startingRoomNumber", startingRoomNumber);
        if (advanceAmount != null) formData.put("advanceAmount", advanceAmount);
        
        existingHostel.setIsEdited(true);
        // Update hostel with form data and images
        Hostel updatedHostel = hostelService.updateHostelWithFormData(hostelId, formData, images);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Hostel updated successfully");
        response.put("data", convertToResponseMap(updatedHostel));
        
        System.out.println("✅ Hostel updated successfully with ID: " + hostelId);
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("❌ Error updating hostel: " + e.getMessage());
        return ResponseEntity.status(500).body(errorResponse("Failed to update hostel: " + e.getMessage()));
    }
}
    
    // DELETE - Remove hostel
    @DeleteMapping("/{hostelId}")
    public ResponseEntity<?> deleteHostel(
        @PathVariable Long hostelId,
        @RequestHeader("Authorization") String authHeader
    ) {
        try {
            Long ownerId = extractOwnerIdFromToken(authHeader);
            boolean deleted = hostelService.deleteHostel(hostelId);
            
            if (deleted) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Hostel deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(404).body(errorResponse("Hostel not found"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("Failed to delete hostel"));
        }
    }
    
    // GET owner's PG count
    @GetMapping("/owner/count")
    public ResponseEntity<?> getOwnerPGsCount(@RequestHeader("Authorization") String authHeader) {
        try {
            Long ownerId = extractOwnerIdFromToken(authHeader);
            Long count = hostelService.getOwnerPGsCount(ownerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("Failed to get count"));
        }
    }
    
    // Helper method to convert Hostel to response map
    private Map<String, Object> convertToResponseMap(Hostel hostel) {
        Map<String, Object> map = new HashMap<>();
        
        // Basic info
        map.put("hostel_id", hostel.getHostelId());
        map.put("id", hostel.getHostelId());
        map.put("hostel_name", hostel.getHostelName());
        map.put("name", hostel.getHostelName());
        map.put("description", hostel.getDescription());
        map.put("pg_type", hostel.getPgType());
        map.put("address", hostel.getAddress());
        map.put("area", hostel.getArea());
        map.put("city", hostel.getCity());
        map.put("state", hostel.getState());
        map.put("pincode", hostel.getPincode());
        map.put("price", hostel.getPrice());
        map.put("rent", hostel.getRent());
        map.put("status", hostel.getStatus());
        map.put("rating", hostel.getRating());
        map.put("reviews", hostel.getReviews());
        map.put("owner_id", hostel.getOwnerId());
        map.put("owner_name", hostel.getOwnerName());
        map.put("created_at", hostel.getCreatedAt());
        map.put("total_rooms", hostel.getTotalRooms());
        map.put("occupied_rooms", hostel.getOccupiedRooms());
        map.put("vacant_rooms", hostel.getVacantRooms());

        map.put("numFloors", hostel.getNumFloors());
map.put("roomsPerFloor", hostel.getRoomsPerFloor());
map.put("startingRoom", hostel.getStartingRoomNumber());
map.put("advance", hostel.getAdvanceAmount());
map.put("isEdited", hostel.getIsEdited());

        
        // Parse JSON fields
        try {
            // Images
            List<String> images = objectMapper.readValue(hostel.getImages(), List.class);
            map.put("images", images);
            map.put("img", images.isEmpty() ? "/uploads/default_pg.jpg" : images.get(0));
            
            // Sharing data
            if (hostel.getSharingData() != null) {
                map.put("sharing_data", objectMapper.readValue(hostel.getSharingData(), Map.class));
            }
            
            // Facilities
            if (hostel.getFacilities() != null) {
                map.put("facilities", objectMapper.readValue(hostel.getFacilities(), Map.class));
            }
            
            // Rules
            if (hostel.getRules() != null) {
                map.put("rules", objectMapper.readValue(hostel.getRules(), List.class));
            }
            
            // Food menu
            if (hostel.getFoodMenu() != null) {
                map.put("food_menu", objectMapper.readValue(hostel.getFoodMenu(), Map.class));
            }
            
        } catch (Exception e) {
            // Default values if JSON parsing fails
            map.put("images", Arrays.asList("/uploads/default_pg.jpg"));
            map.put("img", "/uploads/default_pg.jpg");
            map.put("facilities", new HashMap<>());
            map.put("rules", new ArrayList<>());
            map.put("sharing_data", new HashMap<>());
            map.put("food_menu", new HashMap<>());
        }
        
        return map;
    }
    
    // Extract owner ID from token
private Long extractOwnerIdFromToken(String authHeader) {
    try {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("⚠️ No valid auth header");
            throw new RuntimeException("Authorization header is missing");
        }
        
        String token = authHeader.substring(7);
        System.out.println("🔑 Token received: " + token);
        
        // Your token format: hlopg_owner_123_timestamp
        String[] parts = token.split("_");
        
        if (parts.length >= 3 && parts[0].equals("hlopg")) {
            if (parts[1].equals("owner")) {
                // Owner token format: hlopg_owner_123_timestamp
                Long ownerId = Long.parseLong(parts[2]);
                System.out.println("✅ Extracted owner ID: " + ownerId);
                return ownerId;
            } else {
                // User token format: hlopg_123_timestamp
                Long userId = Long.parseLong(parts[1]);
                System.out.println("ℹ️ Using user ID as owner ID: " + userId);
                return userId;
            }
        }
        
        System.out.println("⚠️ Invalid token format, using default ID 1");
        return 1L; // Default owner ID
        
    } catch (Exception e) {
        System.err.println("❌ Error extracting owner ID: " + e.getMessage());
        System.out.println("⚠️ Using default owner ID 1 due to error");
        return 1L; // Fallback to default
    }
}
    
    // Error response helper
    private Map<String, Object> errorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        return error;
    }

  

// Helper method to extract user ID from token (add this method)
private Long extractUserIdFromToken(String authHeader) {
    try {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("⚠️ No valid auth header in like-hostel");
            return 1L; // Default user ID for testing
        }
        
        String token = authHeader.substring(7);
        System.out.println("🔑 Like token: " + token);
        
        // Your token format: hlopg_123_timestamp
        String[] parts = token.split("_");
        
        if (parts.length >= 2 && parts[0].equals("hlopg")) {
            Long userId = Long.parseLong(parts[1]);
            System.out.println("✅ Extracted user ID: " + userId);
            return userId;
        }
        
        System.out.println("⚠️ Invalid token format, using default ID 1");
        return 1L;
        
    } catch (Exception e) {
        System.err.println("❌ Error extracting user ID: " + e.getMessage());
        return 1L;
    }
}

@GetMapping("/{hostelId}/rooms")
public ResponseEntity<?> getRoomsByHostelId(
    @PathVariable Long hostelId,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size,
    @RequestHeader("Authorization") String authHeader
) {
    try {
        System.out.println("📋 Fetching rooms for hostel ID: " + hostelId);
        
        // Verify hostel exists
        Optional<Hostel> hostelOpt = hostelService.getHostelById(hostelId);
        if (!hostelOpt.isPresent()) {
            return ResponseEntity.status(404).body(errorResponse("Hostel not found"));
        }
        
        Hostel hostel = hostelOpt.get();
        
        // Create sample rooms based on hostel configuration
        List<Map<String, Object>> sampleRooms = createSampleRooms(hostel);
        
        // Apply pagination
        int start = page * size;
        int end = Math.min(start + size, sampleRooms.size());
        List<Map<String, Object>> paginatedRooms = new ArrayList<>();
        
        if (start < sampleRooms.size()) {
            paginatedRooms = sampleRooms.subList(start, end);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Rooms fetched successfully");
        response.put("rooms", paginatedRooms);
        response.put("total", sampleRooms.size());
        response.put("page", page);
        response.put("size", size);
        response.put("totalPages", (int) Math.ceil((double) sampleRooms.size() / size));
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(errorResponse("Failed to fetch rooms: " + e.getMessage()));
    }
}

// NEW: Alternative endpoint for MyRooms.jsx compatibility
@GetMapping("/rooms/pg/{hostelId}")
public ResponseEntity<?> getRoomsForPG(@PathVariable Long hostelId) {
    try {
        System.out.println("📋 Fetching rooms for PG ID: " + hostelId);
        
        // Verify hostel exists
        Optional<Hostel> hostelOpt = hostelService.getHostelById(hostelId);
        if (!hostelOpt.isPresent()) {
            return ResponseEntity.status(404).body(errorResponse("Hostel not found"));
        }
        
        Hostel hostel = hostelOpt.get();
        
        // Create sample rooms based on hostel configuration
        List<Map<String, Object>> sampleRooms = createSampleRooms(hostel);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Rooms fetched successfully");
        response.put("data", sampleRooms);
        response.put("total", sampleRooms.size());
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(errorResponse("Failed to fetch rooms: " + e.getMessage()));
    }
}

// NEW: Update room status (Frontend needs this)
@PutMapping("/room/{roomId}/status")
public ResponseEntity<?> updateRoomStatus(
    @PathVariable String roomId,
    @RequestBody Map<String, String> request,
    @RequestHeader("Authorization") String authHeader
) {
    try {
        System.out.println("🔄 Updating room status for room ID: " + roomId);
        
        if (!request.containsKey("status")) {
            return ResponseEntity.badRequest().body(errorResponse("Status field is required"));
        }
        
        String newStatus = request.get("status");
        
        // Validate status
        List<String> validStatuses = Arrays.asList("vacant", "occupied", "maintenance");
        if (!validStatuses.contains(newStatus)) {
            return ResponseEntity.badRequest().body(errorResponse("Invalid status. Must be: vacant, occupied, or maintenance"));
        }
        
        // In a real implementation, you would update the room in database
        // For now, simulate success
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Room status updated successfully");
        response.put("room_id", roomId);
        response.put("status", newStatus);
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(errorResponse("Failed to update room status: " + e.getMessage()));
    }
}

// NEW: Owner rooms endpoint (alternative for MyRooms.jsx)
@GetMapping("/owner/rooms/{hostelId}")
public ResponseEntity<?> getOwnerRooms(
    @PathVariable Long hostelId,
    @RequestHeader("Authorization") String authHeader
) {
    try {
        Long ownerId = extractOwnerIdFromToken(authHeader);
        System.out.println("📋 Fetching owner rooms for hostel ID: " + hostelId);
        
        // Verify hostel exists and belongs to owner
        Optional<Hostel> hostelOpt = hostelService.getHostelById(hostelId);
        if (!hostelOpt.isPresent()) {
            return ResponseEntity.status(404).body(errorResponse("Hostel not found"));
        }
        
        Hostel hostel = hostelOpt.get();
        if (!hostel.getOwnerId().equals(ownerId)) {
            return ResponseEntity.status(403).body(errorResponse("You are not authorized to view these rooms"));
        }
        
        // Create sample rooms based on hostel configuration
        List<Map<String, Object>> sampleRooms = createSampleRooms(hostel);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Rooms fetched successfully");
        response.put("data", sampleRooms);
        response.put("total", sampleRooms.size());
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(errorResponse("Failed to fetch rooms: " + e.getMessage()));
    }
}

// NEW: Helper method to create sample rooms
private List<Map<String, Object>> createSampleRooms(Hostel hostel) {
    List<Map<String, Object>> rooms = new ArrayList<>();
    
    // Get sharing data
    Map<String, Object> sharingData = new HashMap<>();
    try {
        if (hostel.getSharingData() != null) {
            sharingData = objectMapper.readValue(hostel.getSharingData(), Map.class);
        }
    } catch (Exception e) {
        // Default sharing data
        sharingData.put("2-Sharing", 5000);
        sharingData.put("3-Sharing", 4000);
        sharingData.put("4-Sharing", 3500);
    }
    
    // Get configuration
    int numFloors = 3;
    int roomsPerFloor = 5;
    String startingRoomNumber = "101";
    int advanceAmount = 5000;
    
    // Try to extract from facilities or other fields
    if (hostel.getFacilities() != null) {
        try {
            Map<String, Object> facilities = objectMapper.readValue(hostel.getFacilities(), Map.class);
            if (facilities.containsKey("numFloors")) {
                numFloors = Integer.parseInt(facilities.get("numFloors").toString());
            }
            if (facilities.containsKey("roomsPerFloor")) {
                roomsPerFloor = Integer.parseInt(facilities.get("roomsPerFloor").toString());
            }
            if (facilities.containsKey("startingRoomNumber")) {
                startingRoomNumber = facilities.get("startingRoomNumber").toString();
            }
            if (facilities.containsKey("advanceAmount")) {
                advanceAmount = Integer.parseInt(facilities.get("advanceAmount").toString());
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not parse facilities for room config");
        }
    }
    
    // Get sharing types
    List<String> sharingTypes = new ArrayList<>(sharingData.keySet());
    if (sharingTypes.isEmpty()) {
        sharingTypes = Arrays.asList("2-Sharing", "3-Sharing", "4-Sharing");
    }
    
    // Create rooms
    int startNum = 1;
    try {
        startNum = Integer.parseInt(startingRoomNumber.replaceAll("[^0-9]", ""));
    } catch (Exception e) {
        startNum = 101;
    }
    
    for (int floor = 1; floor <= numFloors; floor++) {
        for (int roomNum = 1; roomNum <= roomsPerFloor; roomNum++) {
            Map<String, Object> room = new HashMap<>();
            
            // Room ID
            room.put("id", hostel.getHostelId() * 1000 + floor * 100 + roomNum);
            room.put("_id", hostel.getHostelId() * 1000 + floor * 100 + roomNum);
            
            // Room number (format: floor + 2-digit room number)
            String roomNumber = floor + String.format("%02d", roomNum);
            room.put("room_number", roomNumber);
            
            // Floor
            room.put("floor", floor);
            
            // Sharing type (cycle through available types)
            String sharingType = sharingTypes.get((roomNum - 1) % sharingTypes.size());
            room.put("sharing_type", sharingType);
            room.put("sharing", sharingType);
            
            // Rent from sharing data or default
            int rent = 5000;
            if (sharingData.containsKey(sharingType)) {
                rent = Integer.parseInt(sharingData.get(sharingType).toString());
            }
            // Add floor premium
            rent += (floor - 1) * 500;
            room.put("rent", rent);
            room.put("price", rent);
            
            // Status (first 2 rooms occupied, others vacant)
            if (roomNum <= 2) {
                room.put("status", "occupied");
                room.put("tenant_name", roomNum == 1 ? "John Doe" : "Jane Smith");
                room.put("tenant_phone", "987654321" + roomNum);
            } else {
                room.put("status", "vacant");
                room.put("tenant_name", null);
                room.put("tenant_phone", null);
            }
            
            // Amenities
            List<String> amenities = Arrays.asList("WiFi", "Attached Bathroom", "Bed", "Wardrobe");
            room.put("amenities", amenities);
            
            // PG/Hostel ID
            room.put("pg_id", hostel.getHostelId());
            room.put("hostel_id", hostel.getHostelId());
            
            // Timestamps
            room.put("created_at", new Date());
            room.put("updated_at", new Date());
            
            rooms.add(room);
        }
    }
    
    return rooms;
}



@GetMapping("/food_menu/{hostelId}")
    public ResponseEntity<Map<String, Object>> getFoodMenu(@PathVariable Long hostelId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Hostel> hostelOpt = hostelRepository.findById(hostelId);
            if (!hostelOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Hostel not found");
                return ResponseEntity.status(404).body(response);
            }
            
            Hostel hostel = hostelOpt.get();
            Object foodMenuData = null;
            
            if (hostel.getFoodMenu() != null && !hostel.getFoodMenu().isEmpty()) {
                try {
                    foodMenuData = objectMapper.readValue(hostel.getFoodMenu(), Object.class);
                } catch (Exception e) {
                    foodMenuData = createSampleMenu();
                }
            } else {
                foodMenuData = createSampleMenu();
            }
            
            response.put("success", true);
            response.put("data", foodMenuData);
            response.put("message", "Food menu fetched successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch food menu: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/menu/{hostelId}")
    public ResponseEntity<Map<String, Object>> getMenu(@PathVariable Long hostelId) {
        return getFoodMenu(hostelId);
    }
    
    private Map<String, Object> createSampleMenu() {
        Map<String, Object> menu = new HashMap<>();
        Map<String, Object> weeklyMenu = new HashMap<>();
        
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        
        for (String day : days) {
            Map<String, String> dayMenu = new HashMap<>();
            dayMenu.put("breakfast", "Idli/Dosa/Poha + Tea");
            dayMenu.put("lunch", "Rice, Dal, Vegetable, Salad");
            dayMenu.put("dinner", "Chapati, Dal, Vegetable, Rice");
            weeklyMenu.put(day.toLowerCase(), dayMenu);
        }
        
        menu.put("weekly", weeklyMenu);
        return menu;
    }

    @PostMapping("/like-hostel")
public ResponseEntity<?> likeHostel(
    @RequestBody Map<String, Long> request,
    @RequestHeader("Authorization") String authHeader
) {
    try {
        Long userId = extractUserIdFromToken(authHeader);
        Long hostelId = request.get("hostel_id");
        
        System.out.println("🔍 Like request - User ID: " + userId + ", Hostel ID: " + hostelId);
        
        if (hostelId == null) {
            return ResponseEntity.badRequest().body(errorResponse("Hostel ID is required"));
        }
        
        // Check if hostel exists
        Optional<Hostel> hostelOpt = hostelService.getHostelById(hostelId);
        if (!hostelOpt.isPresent()) {
            return ResponseEntity.status(404).body(errorResponse("Hostel not found with ID: " + hostelId));
        }
        
        // Check if already liked
        boolean alreadyLiked = likeRepository.existsByUserIdAndHostelId(userId, hostelId);
        
        if (alreadyLiked) {
            // UNLIKE: Remove the like from database
            likeRepository.deleteByUserIdAndHostelId(userId, hostelId);
            
            // Update hostel like count if you have that column
            // hostelService.decrementLikeCount(hostelId);
            
            System.out.println("✅ User " + userId + " UNLIKED hostel " + hostelId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Hostel unliked successfully");
            response.put("liked", false); // IMPORTANT: false means unliked
            
            return ResponseEntity.ok(response);
            
        } else {
            // LIKE: Add new like to database
            Like like = new Like(userId, hostelId);
            likeRepository.save(like);
            
            // Update hostel like count if you have that column
            // hostelService.incrementLikeCount(hostelId);
            
            System.out.println("✅ User " + userId + " LIKED hostel " + hostelId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Hostel liked successfully");
            response.put("liked", true); // IMPORTANT: true means liked
            
            return ResponseEntity.ok(response);
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(errorResponse("Failed to like hostel: " + e.getMessage()));
    }
}

// GET - Get liked hostels for user
@GetMapping("/liked-hostels")
public ResponseEntity<?> getLikedHostels(@RequestHeader("Authorization") String authHeader) {
    try {
        Long userId = extractUserIdFromToken(authHeader);
        System.out.println("🔍 Fetching liked hostels for user ID: " + userId);
        
        // Get all hostel IDs that this user has liked
        List<Long> likedHostelIds = likeRepository.findHostelIdsByUserId(userId);
        System.out.println("✅ Found " + likedHostelIds.size() + " liked hostel IDs: " + likedHostelIds);
        
        if (likedHostelIds.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "No liked hostels found");
            response.put("data", new ArrayList<>());
            response.put("total", 0);
            return ResponseEntity.ok(response);
        }
        
        // Fetch the actual hostel details for these IDs
        List<Hostel> likedHostels = new ArrayList<>();
        for (Long hostelId : likedHostelIds) {
            Optional<Hostel> hostelOpt = hostelService.getHostelById(hostelId);
            hostelOpt.ifPresent(likedHostels::add);
        }
        
        // Convert to response format
        List<Map<String, Object>> responseList = new ArrayList<>();
        for (Hostel hostel : likedHostels) {
            responseList.add(convertToResponseMap(hostel));
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Liked hostels fetched successfully");
        response.put("data", responseList);
        response.put("total", responseList.size());
        
        System.out.println("✅ Returning " + responseList.size() + " liked hostels");
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(400).body(errorResponse("Failed to fetch liked hostels: " + e.getMessage()));
    }
}

// GET - Check if a hostel is liked by user (optional, useful for frontend)
@GetMapping("/check-like/{hostelId}")
public ResponseEntity<?> checkIfLiked(
    @PathVariable Long hostelId,
    @RequestHeader("Authorization") String authHeader
) {
    try {
        Long userId = extractUserIdFromToken(authHeader);
        
        boolean isLiked = likeRepository.existsByUserIdAndHostelId(userId, hostelId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("liked", isLiked);
        response.put("message", isLiked ? "Hostel is liked" : "Hostel is not liked");
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        return ResponseEntity.status(500).body(errorResponse("Failed to check like status"));
    }
}


@PostMapping("/addhostel/ios")
public ResponseEntity<?> addHostelIOS(
    @RequestParam String pgName,
    @RequestParam String pgInfo,
    @RequestParam String pgType,

    @RequestParam String city,
    @RequestParam String area,
    @RequestParam String pincode,
    @RequestParam String address,

    @RequestParam String amenities,
    @RequestParam String foodMenu,
    @RequestParam String sharing,

    @RequestParam Integer numFloors,
    @RequestParam Integer roomsPerFloor,
    @RequestParam Integer startingRoomNumber,
    @RequestParam Integer advanceAmount,

    @RequestParam("images") MultipartFile[] images,
    @RequestHeader("Authorization") String authHeader
) {
    try {
        Long ownerId = extractOwnerIdFromToken(authHeader);

        // ✅ STRICT VALIDATION
        if (pgName.isBlank() || pgInfo.isBlank() || pgType.isBlank()
                || city.isBlank() || area.isBlank()
                || pincode.isBlank() || address.isBlank()
                || images.length == 0) {

            return ResponseEntity.badRequest().body("All fields are required");
        }

        Hostel hostel = new Hostel();
        hostel.setHostelName(pgName);
        hostel.setDescription(pgInfo);
        hostel.setPgType(pgType);

        hostel.setCity(city);
        hostel.setArea(area);
        hostel.setPincode(pincode);
        hostel.setAddress(address);

        hostel.setFacilities(amenities);
        hostel.setFoodMenu(foodMenu);
        hostel.setSharingData(sharing);

        hostel.setNumFloors(numFloors);
        hostel.setRoomsPerFloor(roomsPerFloor);
        hostel.setStartingRoomNumber(startingRoomNumber);
        hostel.setAdvanceAmount(advanceAmount);




        hostel.setOwnerId(ownerId);
        hostel.setOwnerName("Owner " + ownerId);

        hostel.setPrice(advanceAmount);
        hostel.setRent(advanceAmount);

        int totalRooms = numFloors * roomsPerFloor;
        hostel.setTotalRooms(totalRooms);
        hostel.setVacantRooms(totalRooms);
        hostel.setOccupiedRooms(0);

        Hostel savedHostel = hostelService.saveHostel(hostel, images);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "PG uploaded successfully",
            "hostelId", savedHostel.getHostelId()
        ));

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(
            Map.of("success", false, "message", e.getMessage())
        );
    }
}




}
