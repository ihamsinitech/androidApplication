


package com.hlopg_backend.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hlopg_backend.model.User;
import com.hlopg_backend.repository.UserRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.http.MediaType;


import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api/auth")
// @CrossOrigin(origins = "*")
public class AuthController {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // private final GoogleAuthService googleAuthService;

    
    // Store OTPs temporarily
    private final Map<String, OTPData> otpStore = new ConcurrentHashMap<>();
    
    // Twilio credentials
    @Value("${twilio.account.sid:TEST}")
    private String twilioAccountSid;
    
    @Value("${twilio.auth.token:TEST}")
    private String twilioAuthToken;
    
    @Value("${twilio.phone.number:+1234567890}")
    private String twilioPhoneNumber;
    
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        System.out.println("✅ AuthController initialized!");
    }
    
    @PostConstruct
    public void initTwilio() {
        try {
            if (twilioAccountSid != null && !twilioAccountSid.equals("TEST") && 
                twilioAuthToken != null && !twilioAuthToken.equals("TEST")) {
                
                Twilio.init(twilioAccountSid, twilioAuthToken);
                System.out.println("✅ Twilio initialized successfully!");
                System.out.println("📱 Using Twilio phone: " + twilioPhoneNumber);
            } else {
                System.out.println("⚠️ Twilio not initialized - using TEST mode");
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize Twilio: " + e.getMessage());
        }
    }
    
    // ========== USER REGISTRATION ==========
    @PostMapping("/register/user")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> request) {
        System.out.println("📝 POST /api/auth/register/user called!");
        
        try {
            // Extract fields
            String name = (String) request.get("name");
            String email = (String) request.get("email");
            String phone = (String) request.get("phone");
            String password = (String) request.get("password");
            String gender = (String) request.get("gender");
            
            System.out.println("✅ Parsed - Name: " + name + ", Email: " + email + ", Phone: " + phone);
            
            // Validate required fields
            if (name == null || name.trim().isEmpty()) {
                return badRequest("Name is required");
            }
            if (email == null || email.trim().isEmpty()) {
                return badRequest("Email is required");
            }
            if (phone == null || phone.trim().isEmpty()) {
                return badRequest("Phone is required");
            }
            if (password == null || password.trim().isEmpty()) {
                return badRequest("Password is required");
            }
            if (gender == null || gender.trim().isEmpty()) {
                return badRequest("Gender is required");
            }
            
            // Clean data
            name = name.trim();
            email = email.trim().toLowerCase();
            phone = phone.trim();
            gender = gender.toUpperCase();
            
            // Check if email already exists
            if (userRepository.findByEmail(email).isPresent()) {
                return badRequest("Email already registered");
            }
            
            // Check if phone already exists
            if (userRepository.findByPhone(phone).isPresent()) {
                return badRequest("Phone number already exists");
            }
            
            // Create new user
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(passwordEncoder.encode(password));
            user.setGender(gender);
            user.setUserType("USER");
            
            // Save user to database
            User savedUser = userRepository.save(user);
            
            System.out.println("✅ User registered successfully! ID: " + savedUser.getId());
            
            // Send OTP for verification
            boolean otpSent = sendOTP(phone, "REGISTRATION");
            
            // Create response
            Map<String, Object> userData = new HashMap<>();
            userData.put("userId", savedUser.getId());
            userData.put("name", savedUser.getName());
            userData.put("email", savedUser.getEmail());
            userData.put("phone", savedUser.getPhone());
            userData.put("gender", savedUser.getGender());
            userData.put("userType", savedUser.getUserType());
            userData.put("otpSent", otpSent);
            userData.put("otpMessage", otpSent ? "OTP sent to your phone" : "OTP sending failed");
            
            return ResponseEntity.ok(
                createResponse(true, "Registration successful! " + 
                    (otpSent ? "OTP sent to your mobile." : "Please verify OTP later."), 
                    userData)
            );
            
        } catch (Exception e) {
            System.err.println("❌ Registration error: " + e.getMessage());
            e.printStackTrace();
            return badRequest("Registration failed: " + e.getMessage());
        }
    }
    
    // ========== USER LOGIN ==========
@PostMapping("/login/user")
public ResponseEntity<?> loginUser(@RequestBody Map<String, String> request) {
    System.out.println("📝 POST /api/auth/login/user called!");
    
    try {
        String identifier = request.get("identifier");
        String password = request.get("password");
        
        if (identifier == null || identifier.trim().isEmpty()) {
            System.out.println("❌ Identifier is empty");
            return badRequest("Email/Phone is required");
        }
        if (password == null || password.trim().isEmpty()) {
            System.out.println("❌ Password is empty");
            return badRequest("Password is required");
        }
        
        identifier = identifier.trim();
        password = password.trim();
        
        System.out.println("🔐 Login attempt - Identifier: " + identifier);
        
        // Find user by email
        User user = userRepository.findByEmail(identifier)
            .orElse(null);
        
        // If not found by email, try by phone
        if (user == null) {
            System.out.println("📱 Trying phone lookup...");
            user = userRepository.findByPhone(identifier)
                .orElse(null);
        }
        
        if (user == null) {
            System.out.println("❌ User not found with identifier: " + identifier);
            return badRequest("Invalid credentials");
        }
        
        System.out.println("✅ User found: " + user.getEmail());
        System.out.println("🔐 Stored password hash: " + user.getPassword());
        
        // Check password
        boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
        System.out.println("🔐 Password match: " + passwordMatches);
        
        if (!passwordMatches) {
            return badRequest("Invalid credentials");
        }
        
        // Create response data
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("phone", user.getPhone());
        userData.put("gender", user.getGender());
        userData.put("userType", user.getUserType());
        userData.put("token", "hlopg_" + user.getId() + "_" + System.currentTimeMillis());
        
        System.out.println("✅ Login successful for user: " + user.getEmail());
        
        return ResponseEntity.ok(
            createResponse(true, "Login successful!", userData)
        );
        
    } catch (Exception e) {
        System.err.println("❌ Login error: " + e.getMessage());
        e.printStackTrace();
        return badRequest("Login failed: " + e.getMessage());
    }
}
    
    // ========== SEND OTP (Twilio) ==========
    private boolean sendOTP(String phoneNumber, String purpose) {
        try {
            // Generate 6-digit OTP
            Random random = new Random();
            String otp = String.format("%04d", random.nextInt(9999));
            
            System.out.println("📱 Generating OTP for " + phoneNumber + ": " + otp);
            
            // Format phone number for India
            String formattedPhone = formatPhoneNumber(phoneNumber);
            System.out.println("📱 Formatted phone: " + formattedPhone);
            
            // Create message body
            String messageBody = "";
            if ("REGISTRATION".equals(purpose)) {
                messageBody = "Your OTP for HLOPG registration is: " + otp + ". Valid for 10 minutes.";
            } else if ("PASSWORD_RESET".equals(purpose)) {
                messageBody = "Your OTP for HLOPG password reset is: " + otp + ". Valid for 10 minutes.";
            } else {
                messageBody = "Your HLOPG verification code is: " + otp + ". Valid for 10 minutes.";
            }
            
            // Check if Twilio is properly initialized
            if (twilioAccountSid.equals("TEST") || twilioAuthToken.equals("TEST")) {
                System.out.println("⚠️ Twilio in TEST mode - OTP would be: " + otp);
                System.out.println("📱 Would send SMS: " + messageBody);
                System.out.println("📱 To: " + formattedPhone);
                System.out.println("📱 From: " + twilioPhoneNumber);
                
                // Store OTP for testing
                String key = phoneNumber + "_" + purpose;
                otpStore.put(key, new OTPData(otp, System.currentTimeMillis() + 600000));
                return true;
            }
            
            // REAL Twilio SMS sending
            System.out.println("📤 Sending real SMS via Twilio...");
            
            Message message = Message.creator(
                new PhoneNumber(formattedPhone),      // To
                new PhoneNumber(twilioPhoneNumber),   // From
                messageBody                           // Message
            ).create();
            
            System.out.println("✅ SMS sent successfully!");
            System.out.println("📱 Message SID: " + message.getSid());
            System.out.println("📱 Message Status: " + message.getStatus());
            
            // Store OTP for verification
            String key = phoneNumber + "_" + purpose;
            otpStore.put(key, new OTPData(otp, System.currentTimeMillis() + 600000));
            
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ ERROR sending OTP: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback for testing
            String otp = "1234";
            String key = phoneNumber + "_" + purpose;
            otpStore.put(key, new OTPData(otp, System.currentTimeMillis() + 600000));
            System.out.println("📱 Using fallback OTP: " + otp);
            return false;
        }
    }
    
    // ========== FORMAT PHONE NUMBER ==========
    private String formatPhoneNumber(String phone) {
        try {
            // Remove all non-digits
            phone = phone.replaceAll("[^0-9]", "");
            
            // Check if it's an Indian number
            if (phone.length() == 10) {
                // It's a 10-digit Indian number
                return "+91" + phone;
            } else if (phone.length() == 12 && phone.startsWith("91")) {
                // It's already +91 format without +
                return "+" + phone;
            } else if (phone.length() == 13 && phone.startsWith("+91")) {
                // It's already in correct format
                return phone;
            } else if (phone.length() == 11 && phone.startsWith("0")) {
                // It's 0XXXXXXXXXX format
                return "+91" + phone.substring(1);
            }
            
            // If unsure, just prepend +
            if (!phone.startsWith("+")) {
                return "+" + phone;
            }
            
            return phone;
            
        } catch (Exception e) {
            System.err.println("❌ Error formatting phone: " + phone + " - " + e.getMessage());
            return phone;
        }
    }

    // ========== HELPER METHODS ==========
private String cleanPhoneNumber(String phone) {
    if (phone == null || phone.trim().isEmpty()) {
        return "";
    }
    
    // Remove all non-digit characters
    phone = phone.replaceAll("[^0-9]", "");
    
    // Remove country code if present
    if (phone.startsWith("91") && phone.length() == 12) {
        phone = phone.substring(2); // Remove 91
    } else if (phone.startsWith("0") && phone.length() == 11) {
        phone = phone.substring(1); // Remove leading 0
    }
    
    return phone;
}

private String generate4DigitOTP() {
    Random random = new Random();
    return String.format("%04d", 1000 + random.nextInt(9000));
}
    
    // ========== VERIFY OTP ==========
@PostMapping("/verify-otp")
public ResponseEntity<?> verifyOTP(@RequestBody Map<String, String> request) {
    System.out.println("📝 POST /api/auth/verify-otp called!");
    
    try {
        String identifier = request.get("identifier");
        String otpCode = request.get("otpCode");
        String purpose = request.get("purpose");
        
        System.out.println("📱 Identifier: " + identifier);
        System.out.println("🔢 OTP Code: " + otpCode);
        System.out.println("🎯 Purpose: " + purpose);
        
        if (identifier == null || identifier.trim().isEmpty()) {
            return badRequest("Phone number is required");
        }
        if (otpCode == null || otpCode.trim().isEmpty()) {
            return badRequest("OTP is required");
        }
        
        identifier = identifier.trim();
        otpCode = otpCode.trim();
        purpose = purpose != null ? purpose : "REGISTRATION";
        
        // Clean phone number
        String cleanPhone = cleanPhoneNumber(identifier);
        String key = cleanPhone + "_" + purpose;
        
        System.out.println("🔑 Looking for OTP key: " + key);
        
        OTPData otpData = otpStore.get(key);
        
        if (otpData == null) {
            System.out.println("❌ OTP not found with key: " + key);
            System.out.println("🔍 OTP Store contents:");
            for (Map.Entry<String, OTPData> entry : otpStore.entrySet()) {
                System.out.println("   - " + entry.getKey() + " -> " + entry.getValue().otp);
            }
            return badRequest("No OTP found. Please request a new OTP.");
        }
        
        System.out.println("✅ OTP found: " + otpData.otp);
        
        // Check expiry
        if (System.currentTimeMillis() > otpData.expiryTime) {
            otpStore.remove(key); // Remove expired OTP
            return badRequest("OTP expired. Please request a new OTP.");
        }
        
        // Verify OTP
        if (otpData.otp.equals(otpCode)) {
            System.out.println("✅ OTP verified successfully!");
            
            // DON'T REMOVE THE OTP HERE!
            // Just mark it as verified or keep it for reset-password
            System.out.println("ℹ️ OTP kept in store for reset-password step");
            
            return ResponseEntity.ok(
                createResponse(true, "OTP verified successfully!", null)
            );
        } else {
            System.out.println("❌ OTP mismatch. Expected: " + otpData.otp + ", Got: " + otpCode);
            return badRequest("Invalid OTP. Please try again.");
        }
        
    } catch (Exception e) {
        System.err.println("❌ OTP verification error: " + e.getMessage());
        e.printStackTrace();
        return badRequest("OTP verification failed: " + e.getMessage());
    }
}
    
    // ========== RESEND OTP ==========
    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOTP(@RequestBody Map<String, String> request) {
        try {
            String identifier = request.get("identifier");
            String purpose = request.get("purpose");
            
            if (identifier == null || identifier.trim().isEmpty()) {
                return badRequest("Phone number is required");
            }
            
            identifier = identifier.trim();
            purpose = purpose != null ? purpose : "REGISTRATION";
            
            System.out.println("📝 Resending OTP to: " + identifier + " - " + purpose);
            
            // Remove old OTP
            String key = identifier + "_" + purpose;
            otpStore.remove(key);
            
            // Send new OTP
            boolean otpSent = sendOTP(identifier, purpose);
            
            if (otpSent) {
                return ResponseEntity.ok(
                    createResponse(true, "OTP resent successfully!", null)
                );
            } else {
                return badRequest("Failed to resend OTP");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Resend OTP error: " + e.getMessage());
            return badRequest("Failed to resend OTP: " + e.getMessage());
        }
    }
    
    // ========== FORGOT PASSWORD ==========
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            
            if (email == null || email.trim().isEmpty()) {
                return badRequest("Email is required");
            }
            
            email = email.trim().toLowerCase();
            
            // Check if user exists
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email"));
            
            // Send OTP for password reset
            boolean otpSent = sendOTP(user.getPhone(), "PASSWORD_RESET");
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("email", email);
            responseData.put("phone", user.getPhone());
             responseData.put("userType", user.getUserType());
            responseData.put("otpSent", otpSent);
            
            return ResponseEntity.ok(
                createResponse(true, 
                    otpSent ? "OTP sent to your registered phone number" : "Failed to send OTP", 
                    responseData)
            );
            
        } catch (Exception e) {
            System.err.println("❌ Forgot password error: " + e.getMessage());
            return badRequest("Password reset failed: " + e.getMessage());
        }
    }
    
    // ========== RESET PASSWORD ==========
@PostMapping("/reset-password")
public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
    System.out.println("🔐 ========== RESET PASSWORD CALLED ==========");
    
    try {
        String identifier = request.get("identifier");
        String otpCode = request.get("otpCode");
        String newPassword = request.get("newPassword");
        
        System.out.println("📱 Received identifier: " + identifier);
        System.out.println("🔢 Received OTP code: " + otpCode);
        System.out.println("🔑 New password (length): " + (newPassword != null ? newPassword.length() : 0));
        
        // Validate inputs
        if (identifier == null || identifier.trim().isEmpty()) {
            System.out.println("❌ ERROR: Identifier is empty");
            return badRequest("Phone number is required");
        }
        if (otpCode == null || otpCode.trim().isEmpty()) {
            System.out.println("❌ ERROR: OTP code is empty");
            return badRequest("OTP is required");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            System.out.println("❌ ERROR: New password is empty");
            return badRequest("New password is required");
        }
        
        identifier = identifier.trim();
        otpCode = otpCode.trim();
        newPassword = newPassword.trim();
        
        // Clean phone number - remove +91, 91, etc.
        String cleanPhone = identifier;
        if (cleanPhone.startsWith("+91")) {
            cleanPhone = cleanPhone.substring(3);
        } else if (cleanPhone.startsWith("91") && cleanPhone.length() >= 12) {
            cleanPhone = cleanPhone.substring(2);
        } else if (cleanPhone.startsWith("0") && cleanPhone.length() == 11) {
            cleanPhone = cleanPhone.substring(1);
        }
        
        System.out.println("📱 Cleaned phone: " + cleanPhone);
        
        // Check OTP store
        System.out.println("🔍 Checking OTP store...");
        System.out.println("🔍 Total OTPs in store: " + otpStore.size());
        
        // Try different key patterns
        String[] possibleKeys = {
            cleanPhone + "_PASSWORD_RESET",
            "91" + cleanPhone + "_PASSWORD_RESET",
            "+91" + cleanPhone + "_PASSWORD_RESET",
            identifier + "_PASSWORD_RESET"  // original identifier
        };
        
        System.out.println("🔍 Looking for OTP with keys:");
        for (String key : possibleKeys) {
            System.out.println("   - " + key);
        }
        
        OTPData otpData = null;
        String foundKey = null;
        
        for (String key : possibleKeys) {
            otpData = otpStore.get(key);
            if (otpData != null) {
                foundKey = key;
                System.out.println("✅ Found OTP with key: " + key);
                System.out.println("✅ OTP value: " + otpData.otp);
                break;
            }
        }
        
        if (otpData == null) {
            System.out.println("❌ ERROR: No OTP found in store!");
            System.out.println("🔍 Available keys in OTP store:");
            for (String key : otpStore.keySet()) {
                System.out.println("   - " + key + " -> " + otpStore.get(key).otp);
            }
            return badRequest("OTP not found or expired. Please request a new OTP.");
        }
        
        // Check if OTP expired
        long currentTime = System.currentTimeMillis();
        long timeLeft = otpData.expiryTime - currentTime;
        
        System.out.println("⏰ Current time: " + currentTime);
        System.out.println("⏰ OTP expiry time: " + otpData.expiryTime);
        System.out.println("⏰ Time left (ms): " + timeLeft);
        System.out.println("⏰ Time left (minutes): " + (timeLeft / 60000.0));
        
        if (currentTime > otpData.expiryTime) {
            System.out.println("❌ ERROR: OTP expired!");
            otpStore.remove(foundKey);
            return badRequest("OTP expired. Please request a new OTP.");
        }
        
        // Verify OTP
        System.out.println("🔢 Comparing OTPs:");
        System.out.println("🔢 Expected OTP: " + otpData.otp);
        System.out.println("🔢 Provided OTP: " + otpCode);
        
        if (!otpData.otp.equals(otpCode)) {
            System.out.println("❌ ERROR: OTP mismatch!");
            return badRequest("Invalid OTP. Please try again.");
        }
        
        // Find user by phone (try different formats)
        System.out.println("👤 Looking for user with phone: " + cleanPhone);
        
        User user = null;
        String[] phoneFormats = {
            cleanPhone,
            "+91" + cleanPhone,
            "91" + cleanPhone,
            "0" + cleanPhone,
            identifier  // original
        };
        
        for (String phone : phoneFormats) {
            System.out.println("   Trying phone format: " + phone);
            user = userRepository.findByPhone(phone).orElse(null);
            if (user != null) {
                System.out.println("✅ Found user with phone: " + phone);
                System.out.println("✅ User email: " + user.getEmail());
                break;
            }
        }
        
        if (user == null) {
            System.out.println("❌ ERROR: User not found!");
            System.out.println("🔍 Checking database for users...");
            List<User> allUsers = userRepository.findAll();
            System.out.println("🔍 Total users in DB: " + allUsers.size());
            for (User u : allUsers) {
                System.out.println("   - " + u.getEmail() + " | " + u.getPhone());
            }
            return badRequest("User not found");
        }
        
        // Update password
        System.out.println("🔑 Updating password for user: " + user.getEmail());
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        
        // Remove OTP after successful reset
        otpStore.remove(foundKey);
        
        System.out.println("✅ SUCCESS: Password reset completed for " + user.getEmail());
        System.out.println("==========================================");
        
        return ResponseEntity.ok(
            createResponse(true, "Password reset successfully!", null)
        );
        
    } catch (Exception e) {
        System.err.println("❌ EXCEPTION in reset password: " + e.getMessage());
        e.printStackTrace();
        return badRequest("Password reset failed: " + e.getMessage());
    }
}
    
    // ========== HELPER METHODS ==========
    private ResponseEntity<?> badRequest(String message) {
        return ResponseEntity.badRequest().body(
            createResponse(false, message, null)
        );
    }
    
    private Map<String, Object> createResponse(boolean success, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        if (data != null) {
            response.put("data", data);
        }
        return response;
    }
    
    // ========== TEST ENDPOINTS ==========
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        System.out.println("✅ GET /api/auth/test called!");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Auth controller is working!");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/test-post")
    public ResponseEntity<?> testPost(@RequestBody Map<String, Object> request) {
        System.out.println("✅ POST /api/auth/test-post called!");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "POST endpoint is working!");
        response.put("receivedData", request);
        
        return ResponseEntity.ok(response);
    }
    
    // ========== OTP DATA CLASS ==========
    private static class OTPData {
        String otp;
        long expiryTime;
        
        OTPData(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }

// ========== OWNER REGISTRATION ==========
@PostMapping("/register/owner")
public ResponseEntity<?> registerOwner(@RequestBody Map<String, Object> request) {
    System.out.println("📝 POST /api/auth/register/owner called!");
    
    try {
        // Extract fields - SAME AS USER
        String name = (String) request.get("name");
        String email = (String) request.get("email");
        String phone = (String) request.get("phone");
        String password = (String) request.get("password");
        
        System.out.println("✅ Parsed - Name: " + name + ", Email: " + email + ", Phone: " + phone);
        
        // Validate required fields - SAME AS USER
        if (name == null || name.trim().isEmpty()) {
            return badRequest("Name is required");
        }
        if (email == null || email.trim().isEmpty()) {
            return badRequest("Email is required");
        }
        if (phone == null || phone.trim().isEmpty()) {
            return badRequest("Phone is required");
        }
        if (password == null || password.trim().isEmpty()) {
            return badRequest("Password is required");
        }
        
        // Clean data - SAME AS USER
        name = name.trim();
        email = email.trim().toLowerCase();
        phone = phone.trim();
        
        // Check if email already exists
        if (userRepository.findByEmail(email).isPresent()) {
            return badRequest("Email already registered");
        }
        
        // Check if phone already exists
        if (userRepository.findByPhone(phone).isPresent()) {
            return badRequest("Phone number already exists");
        }
        
        // Create new user with OWNER type
        User ownerUser = new User();
        ownerUser.setName(name);
        ownerUser.setEmail(email);
        ownerUser.setPhone(phone);
        ownerUser.setPassword(passwordEncoder.encode(password));
        ownerUser.setGender("NOT_SPECIFIED"); // Default for owners
        ownerUser.setUserType("OWNER"); // ✅ SET AS OWNER
        
        // Save to database
        User savedOwner = userRepository.save(ownerUser);
        
        System.out.println("✅ Owner registered successfully! ID: " + savedOwner.getId());
        System.out.println("✅ User Type: " + savedOwner.getUserType());
        
        // Send OTP for verification - USING YOUR WORKING sendOTP METHOD
        boolean otpSent = sendOTP(phone, "REGISTRATION");
        
        // Create response
        Map<String, Object> ownerData = new HashMap<>();
        ownerData.put("id", savedOwner.getId());
        ownerData.put("name", savedOwner.getName());
        ownerData.put("email", savedOwner.getEmail());
        ownerData.put("phone", savedOwner.getPhone());
        ownerData.put("userType", savedOwner.getUserType()); // Will be "OWNER"
        ownerData.put("otpSent", otpSent);
        ownerData.put("otpMessage", otpSent ? "OTP sent to your phone" : "OTP sending failed");
        
        return ResponseEntity.ok(
            createResponse(true, "Owner registration successful! " + 
                (otpSent ? "OTP sent to your mobile." : "Please verify OTP later."), 
                ownerData)
        );
        
    } catch (Exception e) {
        System.err.println("❌ Owner registration error: " + e.getMessage());
        e.printStackTrace();
        return badRequest("Registration failed: " + e.getMessage());
    }
}

// ========== OWNER LOGIN ==========
@PostMapping("/login/owner")
public ResponseEntity<?> loginOwner(@RequestBody Map<String, String> request) {
    System.out.println("📝 POST /api/auth/login/owner called!");
    
    try {
        String identifier = request.get("identifier");
        String password = request.get("password");
        
        if (identifier == null || identifier.trim().isEmpty()) {
            System.out.println("❌ Identifier is empty");
            return badRequest("Email/Phone is required");
        }
        if (password == null || password.trim().isEmpty()) {
            System.out.println("❌ Password is empty");
            return badRequest("Password is required");
        }
        
        identifier = identifier.trim();
        password = password.trim();
        
        System.out.println("🔐 Owner login attempt - Identifier: " + identifier);
        
        // Find user by email
        User user = userRepository.findByEmail(identifier).orElse(null);
        
        // If not found by email, try by phone
        if (user == null) {
            System.out.println("📱 Trying phone lookup...");
            user = userRepository.findByPhone(identifier).orElse(null);
        }
        
        if (user == null) {
            System.out.println("❌ User not found with identifier: " + identifier);
            return badRequest("Invalid credentials");
        }
        
        System.out.println("✅ User found: " + user.getEmail());
        System.out.println("✅ User Type: " + user.getUserType());
        
        // ✅ CHECK IF USER IS AN OWNER
        if (!"OWNER".equals(user.getUserType())) {
            System.out.println("❌ User is not an owner. UserType: " + user.getUserType());
            return badRequest("This account is not an owner account");
        }
        
        // Check password
        boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
        System.out.println("🔐 Password match: " + passwordMatches);
        
        if (!passwordMatches) {
            return badRequest("Invalid credentials");
        }
        
        // Create response data
        Map<String, Object> ownerData = new HashMap<>();
        ownerData.put("id", user.getId());
        ownerData.put("name", user.getName());
        ownerData.put("email", user.getEmail());
        ownerData.put("phone", user.getPhone());
        ownerData.put("userType", user.getUserType()); // Will be "OWNER"
        ownerData.put("token", "hlopg_owner_" + user.getId() + "_" + System.currentTimeMillis());
        
        System.out.println("✅ Owner login successful for: " + user.getEmail());
        
        return ResponseEntity.ok(
            createResponse(true, "Login successful!", ownerData)
        );
        
    } catch (Exception e) {
        System.err.println("❌ Owner login error: " + e.getMessage());
        e.printStackTrace();
        return badRequest("Login failed: " + e.getMessage());
    }
}
    
// ========== GET USER BY TOKEN ==========
@GetMapping("/userid")
public ResponseEntity<?> getUserById(@RequestHeader("Authorization") String authHeader) {
    System.out.println("👤 GET /auth/userid - Fetch user data");
    
    try {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Invalid token");
        }
        
        String token = authHeader.substring(7);
        System.out.println("🔑 Token received: " + token);
        
        // Extract user ID from token (format: hlopg_123_timestamp)
        String[] parts = token.split("_");
        if (parts.length < 2) {
            return ResponseEntity.status(401).body("Invalid token format");
        }
        
        Long userId;
        try {
            userId = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(401).body("Invalid user ID in token");
        }
        
        // Find user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Create response
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("phone", user.getPhone());
        userData.put("gender", user.getGender());
        userData.put("userType", user.getUserType());
        userData.put("profileImage", user.getProfileImage());
        
        return ResponseEntity.ok(userData);
        
    } catch (Exception e) {
        System.err.println("❌ Error fetching user: " + e.getMessage());
        return ResponseEntity.status(500).body("Failed to fetch user data");
    }
}

// ========== GET OWNER BY TOKEN ==========
@GetMapping("/ownerid")
public ResponseEntity<?> getOwnerById(@RequestHeader("Authorization") String authHeader) {
    System.out.println("👑 GET /auth/ownerid - Fetch owner data");
    
    try {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Invalid token");
        }
        
        String token = authHeader.substring(7);
        System.out.println("🔑 Token received: " + token);
        
        // Extract owner ID from token (format: hlopg_owner_123_timestamp)
        String[] parts = token.split("_");
        if (parts.length < 3) {
            return ResponseEntity.status(401).body("Invalid token format");
        }
        
        Long ownerId;
        try {
            ownerId = Long.parseLong(parts[2]); // owner is at index 2
        } catch (NumberFormatException e) {
            return ResponseEntity.status(401).body("Invalid owner ID in token");
        }
        
        // Find user
        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        // Verify it's an owner
        if (!"OWNER".equals(owner.getUserType())) {
            return ResponseEntity.status(403).body("User is not an owner");
        }
        
        // Create response
        Map<String, Object> ownerData = new HashMap<>();
        ownerData.put("id", owner.getId());
        ownerData.put("name", owner.getName());
        ownerData.put("email", owner.getEmail());
        ownerData.put("phone", owner.getPhone());
        ownerData.put("userType", owner.getUserType());
        
        return ResponseEntity.ok(ownerData);
        
    } catch (Exception e) {
        System.err.println("❌ Error fetching owner: " + e.getMessage());
        return ResponseEntity.status(500).body("Failed to fetch owner data");
    }
}

// ========== UPDATE BASIC USER INFO ==========
@PutMapping("/update-basic-info")
public ResponseEntity<?> updateBasicInfo(
        @RequestBody Map<String, String> request,
        @RequestHeader("Authorization") String authHeader) {
    
    System.out.println("🔄 PUT /api/auth/update-basic-info called!");
    
    try {
        // Extract and validate token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Invalid token");
        }
        
        String token = authHeader.substring(7);
        System.out.println("🔑 Token received: " + token);
        
        // Extract user ID from token
        String[] parts = token.split("_");
        if (parts.length < 2) {
            return ResponseEntity.status(401).body("Invalid token format");
        }
        
        Long userId;
        try {
            userId = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(401).body("Invalid user ID in token");
        }
        
        // Find user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        System.out.println("✅ User found: " + user.getEmail());
        
        // Extract update fields
        String name = request.get("name");
        String gender = request.get("gender");
        
        // Update fields if provided
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name.trim());
            System.out.println("📝 Updating name to: " + name);
        }
        
        if (gender != null && !gender.trim().isEmpty()) {
            user.setGender(gender.trim().toUpperCase());
            System.out.println("📝 Updating gender to: " + gender);
        }
        
        // Save updated user
        User updatedUser = userRepository.save(user);
        
        // Prepare response
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", updatedUser.getId());
        userData.put("name", updatedUser.getName());
        userData.put("email", updatedUser.getEmail());
        userData.put("phone", updatedUser.getPhone());
        userData.put("gender", updatedUser.getGender());
        userData.put("userType", updatedUser.getUserType());
        
        return ResponseEntity.ok(
            createResponse(true, "Profile updated successfully!", userData)
        );
        
    } catch (Exception e) {
        System.err.println("❌ Error updating user info: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(500)
            .body(createResponse(false, "Failed to update profile: " + e.getMessage(), null));
    }
}

// ========== CHANGE PASSWORD ==========
@PutMapping("/change-password")
public ResponseEntity<?> changePassword(
        @RequestBody Map<String, String> request,
        @RequestHeader("Authorization") String authHeader) {
    
    System.out.println("🔐 PUT /api/auth/change-password called!");
    
    try {
        // Extract and validate token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Invalid token");
        }
        
        String token = authHeader.substring(7);
        System.out.println("🔑 Token received: " + token);
        
        // Extract user ID from token
        String[] parts = token.split("_");
        if (parts.length < 2) {
            return ResponseEntity.status(401).body("Invalid token format");
        }
        
        Long userId;
        try {
            userId = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(401).body("Invalid user ID in token");
        }
        
        // Find user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Extract password fields
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");
        
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            return badRequest("Current password is required");
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return badRequest("New password is required");
        }
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return badRequest("Current password is incorrect");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword.trim()));
        userRepository.save(user);
        
        System.out.println("✅ Password updated for user: " + user.getEmail());
        
        return ResponseEntity.ok(
            createResponse(true, "Password changed successfully!", null)
        );
        
    } catch (Exception e) {
        System.err.println("❌ Error changing password: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(500)
            .body(createResponse(false, "Failed to change password: " + e.getMessage(), null));
    }
}

@PostMapping("/update-profile-image")
public ResponseEntity<?> updateProfileImage(
        @RequestParam("profileImage") MultipartFile file,
        @RequestHeader("Authorization") String authHeader) {
    
    System.out.println("🖼️ POST /api/auth/update-profile-image called!");
    
    try {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(createResponse(false, "Invalid token", null));
        }
        
        String token = authHeader.substring(7);
        String[] parts = token.split("_");
        Long userId = Long.parseLong(parts[1]);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        System.out.println("✅ User found: " + user.getEmail());
        
        // Create uploads/profiles directory if it doesn't exist
        Path uploadPath = Paths.get("uploads/profiles");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("📁 Created directory: uploads/profiles");
        }
        
        // Generate unique filename
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        String fileName = "profile_" + userId + "_" + System.currentTimeMillis() + fileExtension;
        Path filePath = uploadPath.resolve(fileName);
        
        // Save the file
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("💾 File saved: " + filePath.toString());
        
        // Save image URL to user
        String imageUrl = "/uploads/profiles/" + fileName;
        user.setProfileImage(imageUrl);
        userRepository.save(user);
        
        System.out.println("✅ Profile image saved to database for user: " + user.getEmail());
        
        // Return updated user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("phone", user.getPhone());
        userData.put("gender", user.getGender());
        userData.put("userType", user.getUserType());
        userData.put("profileImage", imageUrl);
        
        return ResponseEntity.ok(
            createResponse(true, "Profile image updated successfully!", userData)
        );
        
    } catch (Exception e) {
        System.err.println("❌ Error uploading profile image: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(500)
            .body(createResponse(false, "Failed to upload profile image: " + e.getMessage(), null));
    }
}

// ========== CHANGE PASSWORD FOR OWNER ==========
@PutMapping("/change-password/owner")
public ResponseEntity<?> changePasswordOwner(
        @RequestBody Map<String, String> request,
        @RequestHeader("Authorization") String authHeader) {
    
    System.out.println("🔐 PUT /api/auth/change-password/owner called!");
    
    try {
        // Extract and validate token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(createResponse(false, "Invalid token", null));
        }
        
        String token = authHeader.substring(7);
        System.out.println("🔑 Token received: " + token);
        
        // Extract owner ID from token (format: hlopg_owner_123_timestamp)
        String[] parts = token.split("_");
        if (parts.length < 3) {
            return ResponseEntity.status(401).body(createResponse(false, "Invalid token format", null));
        }
        
        Long ownerId;
        try {
            ownerId = Long.parseLong(parts[2]); // owner ID is at index 2
        } catch (NumberFormatException e) {
            return ResponseEntity.status(401).body(createResponse(false, "Invalid owner ID in token", null));
        }
        
        // Find owner
        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        System.out.println("✅ Owner found: " + owner.getEmail());
        System.out.println("✅ Owner Type: " + owner.getUserType());
        
        // Verify it's an owner
        if (!"OWNER".equals(owner.getUserType())) {
            return ResponseEntity.status(403).body(createResponse(false, "User is not an owner", null));
        }
        
        // Extract password fields
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");
        
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            return badRequest("Current password is required");
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return badRequest("New password is required");
        }
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, owner.getPassword())) {
            return badRequest("Current password is incorrect");
        }
        
        // Update password
        owner.setPassword(passwordEncoder.encode(newPassword.trim()));
        userRepository.save(owner);
        
        System.out.println("✅ Password updated for owner: " + owner.getEmail());
        
        return ResponseEntity.ok(
            createResponse(true, "Password changed successfully!", null)
        );
        
    } catch (Exception e) {
        System.err.println("❌ Error changing owner password: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(500)
            .body(createResponse(false, "Failed to change password: " + e.getMessage(), null));
    }
}


//some endpoints added for ios purpose

@PutMapping("/update-profile")
public ResponseEntity<?> updateProfile(
        @RequestBody Map<String, String> request,
        @RequestHeader("Authorization") String authHeader) {

    System.out.println("🔄 PUT /api/auth/update-profile called!");

    try {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401)
                    .body(createResponse(false, "Invalid token", null));
        }

        String token = authHeader.substring(7);
        String[] parts = token.split("_");

        if (parts.length < 2) {
            return ResponseEntity.status(401)
                    .body(createResponse(false, "Invalid token format", null));
        }

        Long userId = Long.parseLong(parts[1]);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔹 Extract fields
        String name = request.get("name");
        String email = request.get("email");
        String gender = request.get("gender");
        String password = request.get("password");

        // 🔹 Name
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name.trim());
        }

        // 🔹 Email (check duplicate)
        if (email != null && !email.trim().isEmpty()) {
            email = email.trim().toLowerCase();

            userRepository.findByEmail(email).ifPresent(existing -> {
                if (!existing.getId().equals(userId)) {
                    throw new RuntimeException("Email already exists");
                }
            });

            user.setEmail(email);
        }

        // 🔹 Gender
        if (gender != null && !gender.trim().isEmpty()) {
            user.setGender(gender.trim().toUpperCase());
        }

        // 🔹 Password (OPTIONAL)
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(password.trim()));
            System.out.println("🔐 Password updated");
        } else {
            System.out.println("ℹ️ Password NOT changed");
        }

        userRepository.save(user);

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("phone", user.getPhone());
        userData.put("gender", user.getGender());
        userData.put("userType", user.getUserType());
        userData.put("profileImage", user.getProfileImage());

        return ResponseEntity.ok(
                createResponse(true, "Profile updated successfully!", userData)
        );

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(400)
                .body(createResponse(false, e.getMessage(), null));
    }
}

// ========== FORGOT PASSWORD (MOBILE ONLY) ==========
@PostMapping("/forgot-password/mobile")
public ResponseEntity<?> forgotPasswordMobile(
        @RequestBody Map<String, String> request) {

    System.out.println("📱 POST /api/auth/forgot-password/mobile called!");

    try {
        String phone = request.get("phone");

        if (phone == null || phone.trim().isEmpty()) {
            return badRequest("Mobile number is required");
        }

        phone = phone.trim();

        // 🔍 Find user by PHONE (not email)
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() ->
                        new RuntimeException("User not found with this mobile number"));

        // 🔐 Send OTP for password reset
        boolean otpSent = sendOTP(user.getPhone(), "PASSWORD_RESET");

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("phone", user.getPhone());
        responseData.put("otpSent", otpSent);

        return ResponseEntity.ok(
                createResponse(
                        true,
                        otpSent
                                ? "OTP sent to your mobile number"
                                : "Failed to send OTP",
                        responseData
                )
        );

    } catch (Exception e) {
        System.err.println("❌ Mobile forgot password error: " + e.getMessage());
        return badRequest(e.getMessage());
    }
}

@GetMapping("/owner/profile")
public ResponseEntity<?> getOwnerProfile(
        @RequestHeader("Authorization") String authHeader) {

    try {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Invalid token");
        }

        String token = authHeader.substring(7);
        String[] parts = token.split("_");

        Long ownerId;

        // ✅ hlopg_owner_<id>_<time>
        if (parts.length >= 4 && "owner".equals(parts[1])) {
            ownerId = Long.parseLong(parts[2]);
        }
        // ✅ fallback: hlopg_<id>_<time>
        else if (parts.length >= 3) {
            ownerId = Long.parseLong(parts[1]);
        }
        else {
            return ResponseEntity.status(401).body("Invalid token format");
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        if (!"OWNER".equals(owner.getUserType())) {
            return ResponseEntity.status(403).body("Not an owner");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", owner.getId());
        data.put("name", owner.getName());
        data.put("email", owner.getEmail());
        data.put("phone", owner.getPhone());
        data.put("userType", owner.getUserType());
        data.put("profileImage", owner.getProfileImage());

        return ResponseEntity.ok(data);

    } catch (NumberFormatException e) {
        return ResponseEntity.status(401).body("Invalid token data");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Failed to fetch owner data");
    }
}


@PutMapping("/owner/update-profile")
public ResponseEntity<?> updateOwnerProfile(
        @RequestBody Map<String, String> request,
        @RequestHeader("Authorization") String authHeader) {

    try {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Invalid token");
        }

        String token = authHeader.substring(7);
        String[] parts = token.split("_");

        Long ownerId;

        if (parts.length >= 4 && "owner".equals(parts[1])) {
            ownerId = Long.parseLong(parts[2]);
        } else if (parts.length >= 3) {
            ownerId = Long.parseLong(parts[1]);
        } else {
            return ResponseEntity.status(401).body("Invalid token format");
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        if (!"OWNER".equals(owner.getUserType())) {
            return ResponseEntity.status(403).body("Not an owner");
        }

        // Name
        if (request.containsKey("name") && !request.get("name").isBlank()) {
            owner.setName(request.get("name").trim());
        }

        // Email
        if (request.containsKey("email") && !request.get("email").isBlank()) {
            String email = request.get("email").trim().toLowerCase();

            userRepository.findByEmail(email).ifPresent(existing -> {
                if (!existing.getId().equals(ownerId)) {
                    throw new RuntimeException("Email already exists");
                }
            });

            owner.setEmail(email);
        }

        // Password (optional)
        if (request.containsKey("password") && !request.get("password").isBlank()) {
            owner.setPassword(passwordEncoder.encode(request.get("password")));
        }

        userRepository.save(owner);

        return ResponseEntity.ok("Owner profile updated successfully");

    } catch (NumberFormatException e) {
        return ResponseEntity.status(401).body("Invalid token data");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(e.getMessage());
    }
}



@PostMapping(
    value = "/owner/update-profile-image",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
)
public ResponseEntity<?> updateOwnerProfileImage(
        @RequestParam("profileImage") MultipartFile file,
        @RequestHeader("Authorization") String authHeader) {

    try {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401)
                    .body(createResponse(false, "Invalid token", null));
        }

        String token = authHeader.substring(7);
        String[] parts = token.split("_");

        // ✅ SUPPORT BOTH TOKEN TYPES
        Long userId = Long.parseLong(parts[1]);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ ROLE CHECK FROM DB (NOT TOKEN STRING)
        if (!"OWNER".equals(owner.getUserType())) {
            return ResponseEntity.status(403)
                    .body(createResponse(false, "Not an owner", null));
        }

        // 📁 Directory
        Path uploadPath = Paths.get("uploads/profiles");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 🖼️ Filename
        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }

        String fileName =
                "owner_profile_" + owner.getId() + "_" + System.currentTimeMillis() + ext;

        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String imageUrl = "/uploads/profiles/" + fileName;

        owner.setProfileImage(imageUrl);
        userRepository.save(owner);

        Map<String, Object> data = new HashMap<>();
        data.put("profileImage", imageUrl);

        return ResponseEntity.ok(
                createResponse(true, "Profile image updated", data)
        );

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500)
                .body(createResponse(false, "Image upload failed", null));
    }
}


// @PostMapping("/login/google")
// public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {

//     System.out.println("📝 POST /api/auth/login/google called!");

//     try {
//         String idToken = request.get("token");

//         if (idToken == null || idToken.isBlank()) {
//             return badRequest("Google token is required");
//         }

//         var payload = googleAuthService.verify(idToken);

//         String email = payload.getEmail();
//         String name = (String) payload.get("name");
//         String picture = (String) payload.get("picture");

//         System.out.println("✅ Google user verified: " + email);

//         // 🔍 Find or create user
//         User user = userRepository.findByEmail(email)
//                 .orElseGet(() -> {
//                     User newUser = new User();
//                     newUser.setName(name != null ? name : "Google User");
//                     newUser.setEmail(email);
//                     newUser.setPhone(""); // optional
//                     newUser.setGender("NOT_SPECIFIED");
//                     newUser.setUserType("USER");
//                     newUser.setPassword(""); // no password for Google users
//                     newUser.setProfileImage(picture);
//                     return userRepository.save(newUser);
//                 });

//         // 🔑 SAME token pattern you already use
//         String token = "hlopg_" + user.getId() + "_" + System.currentTimeMillis();

//         Map<String, Object> data = new HashMap<>();
//         data.put("id", user.getId());
//         data.put("name", user.getName());
//         data.put("email", user.getEmail());
//         data.put("userType", user.getUserType());
//         data.put("profileImage", user.getProfileImage());
//         data.put("token", token);

//         return ResponseEntity.ok(
//                 createResponse(true, "Google login successful!", data)
//         );

//     } catch (Exception e) {
//         e.printStackTrace();
//         return ResponseEntity.status(401)
//                 .body(createResponse(false, "Google authentication failed", null));
//     }
// }


}