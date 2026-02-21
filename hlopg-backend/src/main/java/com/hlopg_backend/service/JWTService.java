

// package com.hlopg_backend.service;

// import java.security.Key;
// import java.util.Date;

// import org.springframework.stereotype.Service;

// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.security.Keys;

// @Service
// public class JWTService {
    
//     private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//     private final long EXPIRATION_TIME = 86400000; // 24 hours
    
//     public String generateToken(String email) {
//         return Jwts.builder()
//                 .setSubject(email)
//                 .setIssuedAt(new Date())
//                 .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                 .signWith(SECRET_KEY)
//                 .compact();
//     }
    
//     public String extractEmail(String token) {
//         return Jwts.parserBuilder()
//                 .setSigningKey(SECRET_KEY)
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody()
//                 .getSubject();
//     }
    
//     public boolean validateToken(String token, String email) {
//         try {
//             String extractedEmail = extractEmail(token);
//             return extractedEmail.equals(email) && !isTokenExpired(token);
//         } catch (Exception e) {
//             return false;
//         }
//     }
    
//     private boolean isTokenExpired(String token) {
//         Date expiration = Jwts.parserBuilder()
//                 .setSigningKey(SECRET_KEY)
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody()
//                 .getExpiration();
//         return expiration.before(new Date());
//     }
// }

package com.hlopg_backend.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hlopg_backend.model.User;
import com.hlopg_backend.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {
    
    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);
    
    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRATION_TIME = 86400000; // 24 hours
    
    @Autowired
    private UserRepository userRepository;
    
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("userType", user.getUserType());
        claims.put("name", user.getName());
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }
    
    public Long extractUserId(String token) {
        try {
            logger.info("🔑 Extracting user ID from token: {}", token);
            Claims claims = extractAllClaims(token);
            Long userId = claims.get("userId", Long.class);
            logger.info("✅ Extracted user ID: {}", userId);
            return userId;
        } catch (Exception e) {
            logger.error("❌ Error extracting user ID from token: {}", e.getMessage());
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }
    
    public String extractEmail(String token) {
        try {
            logger.info("📧 Extracting email from token");
            return extractAllClaims(token).getSubject();
        } catch (Exception e) {
            logger.error("❌ Error extracting email from token: {}", e.getMessage());
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }
    
    public String extractUserType(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("userType", String.class);
        } catch (Exception e) {
            logger.error("❌ Error extracting user type from token: {}", e.getMessage());
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }
    
    private Claims extractAllClaims(String token) {
        try {
            logger.info("🔍 Parsing JWT token...");
            // Remove "Bearer " prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            logger.info("📝 Token after cleanup: {}", token.substring(0, Math.min(20, token.length())) + "...");
            
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            logger.info("✅ Token parsed successfully. Claims: {}", claims);
            return claims;
        } catch (Exception e) {
            logger.error("❌ JWT Parsing Error: {}", e.getMessage());
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }
    
    public boolean validateToken(String token) {
        try {
            logger.info("🔒 Validating token...");
            extractAllClaims(token);
            boolean isValid = !isTokenExpired(token);
            logger.info("✅ Token validation result: {}", isValid);
            return isValid;
        } catch (Exception e) {
            logger.error("❌ Token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    private boolean isTokenExpired(String token) {
        try {
            Date expiration = extractAllClaims(token).getExpiration();
            boolean expired = expiration.before(new Date());
            if (expired) {
                logger.warn("⚠️ Token has expired");
            }
            return expired;
        } catch (Exception e) {
            logger.error("❌ Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }
    
    public User getUserFromToken(String token) {
        try {
            logger.info("👤 Getting user from token...");
            
            // Remove "Bearer " prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            String email = extractEmail(token);
            logger.info("📧 Looking up user with email: {}", email);
            
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.error("❌ User not found for email: {}", email);
                        return new RuntimeException("User not found");
                    });
            
            logger.info("✅ Found user: {} (ID: {})", user.getName(), user.getId());
            return user;
        } catch (Exception e) {
            logger.error("❌ Error getting user from token: {}", e.getMessage());
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }
    
    // Add a method to validate and extract token from authorization header
    public String validateAndExtractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.error("❌ Invalid authorization header: {}", authHeader);
            throw new RuntimeException("Authorization header is missing or invalid");
        }
        
        String token = authHeader.substring(7);
        logger.info("✅ Extracted token from header (first 20 chars): {}...", 
                    token.substring(0, Math.min(20, token.length())));
        
        if (!validateToken(token)) {
            logger.error("❌ Token validation failed");
            throw new RuntimeException("Invalid or expired token");
        }
        
        return token;
    }
}