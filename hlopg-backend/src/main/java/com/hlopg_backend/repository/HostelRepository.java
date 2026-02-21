


// package com.hlopg_backend.repository;


// import java.util.List;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import com.hlopg_backend.model.Hostel;

// @Repository
// public interface HostelRepository extends JpaRepository<Hostel, Long> {
    
//     // Find all hostels by owner ID
//     List<Hostel> findByOwnerId(Long ownerId);
    
//     // Find hostels by city
//     List<Hostel> findByCity(String city);
    
//     // Find hostels by city and area
//     List<Hostel> findByCityAndArea(String city, String area);
    
//     // Find active hostels
//     List<Hostel> findByStatus(String status);
    
//     // Find hostels with price range
//     @Query("SELECT h FROM Hostel h WHERE h.price BETWEEN :minPrice AND :maxPrice")
//     List<Hostel> findByPriceRange(@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice);
    
//     // Search hostels by city, area, and price range
//     @Query("SELECT h FROM Hostel h WHERE " +
//            "(:city IS NULL OR h.city = :city) AND " +
//            "(:area IS NULL OR h.area = :area) AND " +
//            "(:minPrice IS NULL OR h.price >= :minPrice) AND " +
//            "(:maxPrice IS NULL OR h.price <= :maxPrice)")
//     List<Hostel> searchHostels(
//         @Param("city") String city,
//         @Param("area") String area,
//         @Param("minPrice") Integer minPrice,
//         @Param("maxPrice") Integer maxPrice
//     );
    
//     // Count hostels by owner
//     @Query("SELECT COUNT(h) FROM Hostel h WHERE h.ownerId = :ownerId")
//     Long countByOwnerId(@Param("ownerId") Long ownerId);
// }

package com.hlopg_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hlopg_backend.model.Hostel;

@Repository
public interface HostelRepository extends JpaRepository<Hostel, Long> {
    
    // Find all hostels by owner ID
    List<Hostel> findByOwnerId(Long ownerId);
    
    // Find hostels by city
    List<Hostel> findByCity(String city);
    
    // Find hostels by city and area
    List<Hostel> findByCityAndArea(String city, String area);
    
    // Find active hostels
    List<Hostel> findByStatus(String status);
    
    // Find hostels with price range
    @Query("SELECT h FROM Hostel h WHERE h.price BETWEEN :minPrice AND :maxPrice")
    List<Hostel> findByPriceRange(@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice);
    
    // Search hostels by city, area, and price range
    @Query("SELECT h FROM Hostel h WHERE " +
           "(:city IS NULL OR h.city = :city) AND " +
           "(:area IS NULL OR h.area = :area) AND " +
           "(:minPrice IS NULL OR h.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR h.price <= :maxPrice)")
    List<Hostel> searchHostels(
        @Param("city") String city,
        @Param("area") String area,
        @Param("minPrice") Integer minPrice,
        @Param("maxPrice") Integer maxPrice
    );
    
    // Count hostels by owner
    @Query("SELECT COUNT(h) FROM Hostel h WHERE h.ownerId = :ownerId")
    Long countByOwnerId(@Param("ownerId") Long ownerId);
    
    // ========== FIXED METHODS ==========
    
    // FIX: Use the correct method name for your entity
    // This method is already provided by JpaRepository, so we don't need to define it
    // List<Hostel> findAllById(List<Long> ids); // REMOVE THIS LINE
    
    // Instead, if you need to find by hostelIds, use a custom query:
    @Query("SELECT h FROM Hostel h WHERE h.hostelId IN :hostelIds")
    List<Hostel> findAllByHostelIds(@Param("hostelIds") List<Long> hostelIds);
    
    // Find by city and price range
    @Query("SELECT h FROM Hostel h WHERE h.city = :city AND h.price BETWEEN :minPrice AND :maxPrice")
    List<Hostel> findByCityAndPriceRange(
        @Param("city") String city,
        @Param("minPrice") Integer minPrice,
        @Param("maxPrice") Integer maxPrice
    );
    
    // Find by filters
    @Query("SELECT h FROM Hostel h WHERE " +
           "(:city IS NULL OR h.city LIKE %:city%) AND " +
           "(:area IS NULL OR h.area LIKE %:area%) AND " +
           "h.price >= :minPrice AND h.price <= :maxPrice " +
           "ORDER BY h.price ASC")
    List<Hostel> findByFilters(
        @Param("city") String city,
        @Param("area") String area,
        @Param("minPrice") Integer minPrice,
        @Param("maxPrice") Integer maxPrice
    );
    
    // Find by owner ID and status
    @Query("SELECT h FROM Hostel h WHERE h.ownerId = :ownerId AND h.status = :status")
    List<Hostel> findByOwnerIdAndStatus(
        @Param("ownerId") Long ownerId,
        @Param("status") String status
    );
    
    // Find by owner ID and city
    @Query("SELECT h FROM Hostel h WHERE h.ownerId = :ownerId AND h.city = :city")
    List<Hostel> findByOwnerIdAndCity(
        @Param("ownerId") Long ownerId,
        @Param("city") String city
    );
    
    // Check if hostel exists by owner ID and hostel ID
    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM Hostel h WHERE h.hostelId = :hostelId AND h.ownerId = :ownerId")
    boolean existsByHostelIdAndOwnerId(
        @Param("hostelId") Long hostelId,
        @Param("ownerId") Long ownerId
    );
    
    // Find all hostels sorted by creation date
    @Query("SELECT h FROM Hostel h ORDER BY h.createdAt DESC")
    List<Hostel> findAllByOrderByCreatedAtDesc();
    
    // Find all hostels sorted by price
    @Query("SELECT h FROM Hostel h ORDER BY h.price ASC")
    List<Hostel> findAllByOrderByPriceAsc();
    
    // Find all hostels sorted by rating
    @Query("SELECT h FROM Hostel h ORDER BY h.rating DESC")
    List<Hostel> findAllByOrderByRatingDesc();
    
    // Check if hostel exists by hostelId
    boolean existsByHostelId(Long hostelId);

   
}
