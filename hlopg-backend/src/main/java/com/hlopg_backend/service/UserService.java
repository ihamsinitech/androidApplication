// // // // package com.hlopg_backend.service;

// // // // import java.util.Optional;

// // // // import org.springframework.security.crypto.password.PasswordEncoder;
// // // // import org.springframework.stereotype.Service;

// // // // import com.hlopg_backend.dto.RegisterRequest;
// // // // import com.hlopg_backend.model.OTP;
// // // // import com.hlopg_backend.model.User;
// // // // import com.hlopg_backend.repository.UserRepository;

// // // // import lombok.RequiredArgsConstructor;

// // // // @Service
// // // // @RequiredArgsConstructor
// // // // public class UserService {
    
// // // //     private final UserRepository userRepository;
// // // //     private final PasswordEncoder passwordEncoder; // Fix: was "passwordEncoder" (lowercase)
// // // //     private final OTPService otpService;
    
// // // //     public User registerUser(RegisterRequest request) {
// // // //         if (userRepository.existsByEmail(request.getEmail())) {
// // // //             throw new RuntimeException("Email already registered");
// // // //         }
// // // //         if (userRepository.existsByPhone(request.getPhone())) {
// // // //             throw new RuntimeException("Phone already registered");
// // // //         }
        
// // // //         User user = User.builder()
// // // //                 .name(request.getName())
// // // //                 .email(request.getEmail().toLowerCase())
// // // //                 .phone(request.getPhone())
// // // //                 .password(passwordEncoder.encode(request.getPassword()))
// // // //                 .gender(request.getGender())
// // // //                 .userType(request.getUserType())
// // // //                 .isVerified(false)
// // // //                 .build();
        
// // // //         User savedUser = userRepository.save(user);
        
// // // //         // Generate OTP
// // // //         otpService.generateOTP(request.getPhone(), OTP.Purpose.REGISTRATION);
        
// // // //         return savedUser;
// // // //     }
    
// // // //     public Optional<User> findByIdentifier(String identifier) {
// // // //         if (identifier.matches("^[6-9]\\d{9}$")) {
// // // //             return userRepository.findByPhone(identifier);
// // // //         } else {
// // // //             return userRepository.findByEmail(identifier.toLowerCase());
// // // //         }
// // // //     }
    
// // // //     public User updateUserProfile(Long userId, String name, User.Gender gender) {
// // // //         User user = userRepository.findById(userId)
// // // //                 .orElseThrow(() -> new RuntimeException("User not found"));
        
// // // //         user.setName(name);
// // // //         user.setGender(gender);
        
// // // //         return userRepository.save(user);
// // // //     }
    
// // // //     public boolean changePassword(Long userId, String currentPassword, String newPassword) {
// // // //         User user = userRepository.findById(userId)
// // // //                 .orElseThrow(() -> new RuntimeException("User not found"));
        
// // // //         if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
// // // //             throw new RuntimeException("Current password is incorrect");
// // // //         }
        
// // // //         user.setPassword(passwordEncoder.encode(newPassword));
// // // //         userRepository.save(user);
// // // //         return true;
// // // //     }
// // // // }
// // // package com.hlopg_backend.service;

// // // import java.util.Optional;

// // // import org.springframework.security.crypto.password.PasswordEncoder;
// // // import org.springframework.stereotype.Service;

// // // import com.hlopg_backend.dto.RegisterRequest;
// // // import com.hlopg_backend.model.OTP;
// // // import com.hlopg_backend.model.User;
// // // import com.hlopg_backend.repository.UserRepository;

// // // import lombok.RequiredArgsConstructor;

// // // @Service
// // // @RequiredArgsConstructor
// // // public class UserService {
    
// // //     private final UserRepository userRepository;
// // //     private final PasswordEncoder passwordEncoder;
// // //     private final OTPService otpService;
    
// // //     public User registerUser(RegisterRequest request) {
// // //         // Check if email exists
// // //         if (userRepository.findByEmail(request.getEmail()).isPresent()) {
// // //             throw new RuntimeException("Email already registered");
// // //         }
        
// // //         // Check if phone exists
// // //         if (userRepository.findByPhone(request.getPhone()).isPresent()) {
// // //             throw new RuntimeException("Phone already registered");
// // //         }
        
// // //         // Create user
// // //         User user = User.builder()
// // //                 .name(request.getName())
// // //                 .email(request.getEmail().toLowerCase())
// // //                 .phone(request.getPhone())
// // //                 .password(passwordEncoder.encode(request.getPassword()))
// // //                 .gender(User.Gender.valueOf(request.getGender().toUpperCase()))
// // //                 .userType(User.UserType.USER)
// // //                 .isVerified(false)
// // //                 .build();
        
// // //         User savedUser = userRepository.save(user);
        
// // //         // Generate OTP
// // //         otpService.generateOTP(request.getPhone(), OTP.Purpose.REGISTRATION);
        
// // //         return savedUser;
// // //     }
    
// // //     public Optional<User> findByIdentifier(String identifier) {
// // //         // Try email first
// // //         Optional<User> byEmail = userRepository.findByEmail(identifier.toLowerCase());
// // //         if (byEmail.isPresent()) {
// // //             return byEmail;
// // //         }
        
// // //         // Try phone
// // //         return userRepository.findByPhone(identifier);
// // //     }
// // // }

// // package com.hlopg_backend.service;

// // import java.util.Optional;

// // import org.springframework.security.crypto.password.PasswordEncoder;
// // import org.springframework.stereotype.Service;

// // import com.hlopg_backend.dto.RegisterRequest;
// // import com.hlopg_backend.model.User;
// // import com.hlopg_backend.repository.UserRepository;

// // import lombok.RequiredArgsConstructor;

// // @Service
// // @RequiredArgsConstructor
// // public class UserService {
    
// //     private final UserRepository userRepository;
// //     private final PasswordEncoder passwordEncoder;
    
// //     public User registerUser(RegisterRequest request) {
// //         // Check if email exists
// //         if (userRepository.findByEmail(request.getEmail()).isPresent()) {
// //             throw new RuntimeException("Email already registered");
// //         }
        
// //         // Check if phone exists
// //         if (userRepository.findByPhone(request.getPhone()).isPresent()) {
// //             throw new RuntimeException("Phone already registered");
// //         }
        
// //         // Validate gender
// //         try {
// //             User.Gender.valueOf(request.getGender().toUpperCase());
// //         } catch (IllegalArgumentException e) {
// //             throw new RuntimeException("Invalid gender. Must be MALE, FEMALE, or OTHER");
// //         }
        
// //         // Create user
// //         User user = User.builder()
// //                 .name(request.getName())
// //                 .email(request.getEmail().toLowerCase())
// //                 .phone(request.getPhone())
// //                 .password(passwordEncoder.encode(request.getPassword()))
// //                 .gender(User.Gender.valueOf(request.getGender().toUpperCase()))
// //                 .userType(User.UserType.USER)
// //                 .isVerified(false)
// //                 .build();
        
// //         System.out.println("✅ User created: " + user.getEmail());
        
// //         return userRepository.save(user);
// //     }
    
// //     public Optional<User> findByIdentifier(String identifier) {
// //         Optional<User> byEmail = userRepository.findByEmail(identifier.toLowerCase());
// //         if (byEmail.isPresent()) {
// //             return byEmail;
// //         }
// //         return userRepository.findByPhone(identifier);
// //     }
// // }

// package com.hlopg_backend.service;

// import com.hlopg_backend.model.User;
// import com.hlopg_backend.repository.UserRepository;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;

// import java.time.LocalDateTime;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Optional;

// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class UserService {
    
//     private final UserRepository userRepository;
//     private final PasswordEncoder passwordEncoder;
    
//     public Map<String, Object> registerUser(Map<String, Object> requestData) {
//         String email = (String) requestData.get("email");
//         String phone = (String) requestData.get("phone");
//         String name = (String) requestData.get("name");
//         String password = (String) requestData.get("password");
//         String gender = (String) requestData.get("gender");
//         String userType = (String) requestData.get("userType");
        
//         log.info("🔍 Checking if user exists - Email: {}, Phone: {}", email, phone);
        
//         // Check if email already exists
//         if (userRepository.findByEmail(email).isPresent()) {
//             log.warn("❌ Email already exists: {}", email);
//             throw new RuntimeException("Email already registered");
//         }
        
//         // Check if phone already exists
//         if (userRepository.findByPhone(phone).isPresent()) {
//             log.warn("❌ Phone already exists: {}", phone);
//             throw new RuntimeException("Phone number already exists");
//         }
        
//         // Create new user
//         User user = User.builder()
//                 .name(name)
//                 .email(email)
//                 .phone(phone)
//                 .password(passwordEncoder.encode(password))
//                 .gender(gender)
//                 .userType(userType != null ? userType : "USER")
//                 .createdAt(LocalDateTime.now())
//                 .updatedAt(LocalDateTime.now())
//                 .build();
        
//         // Save user
//         User savedUser = userRepository.save(user);
//         log.info("✅ User registered successfully - ID: {}, Email: {}", 
//                 savedUser.getUserId(), savedUser.getEmail());
        
//         // Prepare response data (without password)
//         Map<String, Object> responseData = new HashMap<>();
//         responseData.put("userId", savedUser.getUserId());
//         responseData.put("email", savedUser.getEmail());
//         responseData.put("phone", savedUser.getPhone());
//         responseData.put("name", savedUser.getName());
//         responseData.put("gender", savedUser.getGender());
//         responseData.put("userType", savedUser.getUserType());
        
//         return responseData;
//     }
// }



