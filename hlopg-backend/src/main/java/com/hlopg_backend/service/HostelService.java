package com.hlopg_backend.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hlopg_backend.model.Hostel;
import com.hlopg_backend.model.Like;
import com.hlopg_backend.repository.HostelRepository;
import com.hlopg_backend.repository.LikeRepository;

@Service
public class HostelService {
    
    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private LikeRepository likeRepository;
    
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public HostelService() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }
    
    // Get all hostels with optional filters
    public List<Hostel> getAllHostels(String city, String area, Integer minPrice, Integer maxPrice) {
        if (city != null || area != null || minPrice != null || maxPrice != null) {
            return hostelRepository.searchHostels(city, area, minPrice, maxPrice);
        }
        return hostelRepository.findByStatus("ACTIVE");
    }
    
    // Get hostels by owner ID
    public List<Hostel> getHostelsByOwnerId(Long ownerId) {
        return hostelRepository.findByOwnerId(ownerId);
    }
    
    // Get hostel by ID
    public Optional<Hostel> getHostelById(Long hostelId) {
        return hostelRepository.findById(hostelId);
    }
    
    // Save new hostel
    public Hostel saveHostel(Hostel hostel, MultipartFile[] images) throws Exception {
        // Save images
        List<String> imagePaths = saveImages(images);
        hostel.setImages(objectMapper.writeValueAsString(imagePaths));
        
        // Set default values
        if (hostel.getStatus() == null) hostel.setStatus("ACTIVE");
        if (hostel.getRating() == null) hostel.setRating(4.5);
        if (hostel.getReviews() == null) hostel.setReviews(0);
        if (hostel.getTotalRooms() == null) hostel.setTotalRooms(20);
        if (hostel.getOccupiedRooms() == null) hostel.setOccupiedRooms(0);
        if (hostel.getVacantRooms() == null) hostel.setVacantRooms(20);
        if (hostel.getCreatedAt() == null) hostel.setCreatedAt(new Date());
        
        return hostelRepository.save(hostel);
    }
    
    // Update hostel
    // Add these methods to your existing HostelService class

// Update hostel with images
public Hostel updateHostel(Hostel hostel, MultipartFile[] images) throws Exception {
    // Handle image updates
    if (images != null && images.length > 0) {
        List<String> newImagePaths = saveImages(images);
        
        // Get existing images
        String existingImagesJson = hostel.getImages();
        List<String> allImages = new ArrayList<>();
        
        // Parse existing images if available
        if (existingImagesJson != null && !existingImagesJson.isEmpty()) {
            try {
                List<String> existingImages = objectMapper.readValue(existingImagesJson, List.class);
                allImages.addAll(existingImages);
            } catch (Exception e) {
                // If parsing fails, just use new images
                System.out.println("⚠️ Could not parse existing images JSON: " + e.getMessage());
            }
        }
        
        // Add new images
        allImages.addAll(newImagePaths);
        
        // Update hostel images
        hostel.setImages(objectMapper.writeValueAsString(allImages));
    }
    
    // Update timestamps or other fields if needed
    hostel.setUpdatedAt(new Date());
    
    return hostelRepository.save(hostel);
}

// Save hostel (without images)
public Hostel save(Hostel hostel) {
    return hostelRepository.save(hostel);
}

// Simple update method that accepts Hostel object
public Hostel updateHostel(Hostel hostel) {
    return hostelRepository.save(hostel);
}
    
    // Delete hostel
    public boolean deleteHostel(Long hostelId) {
        if (hostelRepository.existsById(hostelId)) {
            hostelRepository.deleteById(hostelId);
            return true;
        }
        return false;
    }
    
    // Helper method to save images
    private List<String> saveImages(MultipartFile[] images) throws Exception {
        List<String> imagePaths = new ArrayList<>();
        
        if (images != null && images.length > 0) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                    Path targetLocation = this.fileStorageLocation.resolve(fileName);
                    Files.copy(image.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                    imagePaths.add("/uploads/" + fileName);
                }
            }
        }
        
        if (imagePaths.isEmpty()) {
            imagePaths.add("/uploads/default_pg.jpg");
        }
        
        return imagePaths;
    }
    
    // Get total PGs count for owner
    public Long getOwnerPGsCount(Long ownerId) {
        return hostelRepository.countByOwnerId(ownerId);
    }

     public boolean toggleLike(Long userId, Long hostelId) {
        boolean exists = likeRepository.existsByUserIdAndHostelId(userId, hostelId);
        
        if (exists) {
            // Unlike
            likeRepository.deleteByUserIdAndHostelId(userId, hostelId);
            return false;
        } else {
            // Like
            Like like = new Like(userId, hostelId);
            likeRepository.save(like);
            return true;
        }
    }
    
    public List<Hostel> getLikedHostels(Long userId) {
        List<Long> likedHostelIds = likeRepository.findHostelIdsByUserId(userId);
        return hostelRepository.findAllById(likedHostelIds);
    }
    
    public boolean isHostelLikedByUser(Long userId, Long hostelId) {
        return likeRepository.existsByUserIdAndHostelId(userId, hostelId);
    }
    
    public Long getLikeCountForHostel(Long hostelId) {
        return likeRepository.countByHostelId(hostelId);
    }
    
    public Long getUserLikeCount(Long userId) {
        return likeRepository.countByUserId(userId);
    }

    // In HostelService.java, add these methods:

// Update hostel with form data and images
public Hostel updateHostelWithFormData(Long hostelId, Map<String, String> formData, MultipartFile[] images) throws Exception {
    return hostelRepository.findById(hostelId).map(existingHostel -> {
        try {
            // Update basic fields
            updateBasicFields(existingHostel, formData);
            
            // Update JSON fields
            updateJsonFields(existingHostel, formData);
            
            // Update images if provided
            if (images != null && images.length > 0) {
                updateImages(existingHostel, images);
            }
            
            // Set update timestamp
            existingHostel.setUpdatedAt(new Date());
            
            return hostelRepository.save(existingHostel);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to update hostel: " + e.getMessage(), e);
        }
    }).orElseThrow(() -> new RuntimeException("Hostel not found with ID: " + hostelId));
}

// Helper method to update basic fields
private void updateBasicFields(Hostel hostel, Map<String, String> formData) {
    if (formData.containsKey("pgName") && formData.get("pgName") != null) {
        hostel.setHostelName(formData.get("pgName"));
    }
    if (formData.containsKey("pgInfo") && formData.get("pgInfo") != null) {
        hostel.setDescription(formData.get("pgInfo"));
    }
    if (formData.containsKey("pgType") && formData.get("pgType") != null) {
        hostel.setPgType(formData.get("pgType"));
    }
    if (formData.containsKey("address") && formData.get("address") != null) {
        hostel.setAddress(formData.get("address"));
    }
    if (formData.containsKey("area") && formData.get("area") != null) {
        hostel.setArea(formData.get("area"));
    }
    if (formData.containsKey("city") && formData.get("city") != null) {
        hostel.setCity(formData.get("city"));
    }
    if (formData.containsKey("state") && formData.get("state") != null) {
        hostel.setState(formData.get("state"));
    }
    if (formData.containsKey("pincode") && formData.get("pincode") != null) {
        hostel.setPincode(formData.get("pincode"));
    }
}

// Helper method to update JSON fields
private void updateJsonFields(Hostel hostel, Map<String, String> formData) throws Exception {
    if (formData.containsKey("sharing") && formData.get("sharing") != null) {
        String sharingJson = formData.get("sharing");
        hostel.setSharingData(sharingJson);
        
        // Update price from sharing data
        try {
            Map<String, Integer> sharingMap = objectMapper.readValue(
                sharingJson, 
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, Integer>>() {}
            );
            Integer minPrice = sharingMap.values().stream()
                .min(Integer::compare)
                .orElse(hostel.getPrice());
            hostel.setPrice(minPrice);
            hostel.setRent(minPrice);
        } catch (Exception e) {
            System.out.println("⚠️ Failed to parse sharing JSON, keeping existing price");
        }
    }
    
    if (formData.containsKey("rules") && formData.get("rules") != null) {
        hostel.setRules(formData.get("rules"));
    }
    
    if (formData.containsKey("furnish") && formData.get("furnish") != null) {
        hostel.setFacilities(formData.get("furnish"));
    }
    
    if (formData.containsKey("foodMenu") && formData.get("foodMenu") != null) {
        hostel.setFoodMenu(formData.get("foodMenu"));
    }
    
    // Optional fields (add these to your Hostel model if needed)
    if (formData.containsKey("numFloors") && formData.get("numFloors") != null) {
        // hostel.setNumFloors(Integer.parseInt(formData.get("numFloors")));
    }
    if (formData.containsKey("roomsPerFloor") && formData.get("roomsPerFloor") != null) {
        // hostel.setRoomsPerFloor(Integer.parseInt(formData.get("roomsPerFloor")));
    }
    if (formData.containsKey("startingRoomNumber") && formData.get("startingRoomNumber") != null) {
        // hostel.setStartingRoomNumber(formData.get("startingRoomNumber"));
    }
    if (formData.containsKey("advanceAmount") && formData.get("advanceAmount") != null) {
        // hostel.setAdvanceAmount(Integer.parseInt(formData.get("advanceAmount")));
    }
}

// Helper method to update images
private void updateImages(Hostel hostel, MultipartFile[] images) throws Exception {
    // Save new images
    List<String> newImagePaths = saveImages(images);
    
    // Get existing images
    String existingImagesJson = hostel.getImages();
    List<String> allImages = new ArrayList<>();
    
    // Parse existing images if available
    if (existingImagesJson != null && !existingImagesJson.isEmpty()) {
        try {
            List<String> existingImages = objectMapper.readValue(existingImagesJson, List.class);
            allImages.addAll(existingImages);
        } catch (Exception e) {
            System.out.println("⚠️ Could not parse existing images JSON: " + e.getMessage());
        }
    }
    
    // Add new images
    allImages.addAll(newImagePaths);
    
    // Update hostel images (keep only last 20 images to avoid too many)
    if (allImages.size() > 20) {
        allImages = allImages.subList(allImages.size() - 20, allImages.size());
    }
    
    hostel.setImages(objectMapper.writeValueAsString(allImages));
}


}
