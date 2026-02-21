package com.hlopg_backend.service;

// import lombok.RequiredArgsConstructor;

// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;

// import java.util.Optional;

// import com.hlopg_backend.dto.AuthRequest;
// import com.hlopg_backend.dto.AuthResponse;
// import com.hlopg_backend.model.User;
// import com.hlopg_backend.repository.UserRepository;

// @Service
// @RequiredArgsConstructor
public class AuthService {
    
//     private final UserRepository userRepository;
//     private final JWTService jwtService;
//     private final AuthenticationManager authenticationManager;
//     private final UserDetailsService userDetailsService;
//     private final PasswordEncoder passwordEncoder;
    
//     public AuthResponse authenticate(AuthRequest request) {
//         // Find user by identifier (email or phone)
//         Optional<User> userOptional = userRepository.findByEmailOrPhone(
//                 request.getIdentifier().toLowerCase(), 
//                 request.getIdentifier()
//         );
        
//         if (userOptional.isEmpty()) {
//             throw new RuntimeException("User not found");
//         }
        
//         User user = userOptional.get();
        
//         // Authenticate using Spring Security
//         authenticationManager.authenticate(
//                 new UsernamePasswordAuthenticationToken(
//                         user.getEmail(),
//                         request.getPassword()
//                 )
//         );
        
//         // Load user details for JWT
//         UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        
//         // Generate JWT token
//         String token = jwtService.generateToken(userDetails);
        
//         // Create response
//         return AuthResponse.builder()
//                 .token(token)
//                 .success(true)
//                 .user(AuthResponse.UserResponse.builder()
//                         .userId(user.getUserId())
//                         .name(user.getName())
//                         .email(user.getEmail())
//                         .phone(user.getPhone())
//                         .userType(user.getUserType().name())
//                         .build())
//                 .build();
//     }
    
//     public boolean validatePassword(String rawPassword, String encodedPassword) {
//         return passwordEncoder.matches(rawPassword, encodedPassword);
//     }
}

