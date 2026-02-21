// package com.hlopg_backend.dto;


// import com.hlopg_backend.model.User;

// import jakarta.validation.constraints.*;
// import lombok.Data;

// @Data
// public class RegisterRequest {
    
//     @NotBlank(message = "Name is required")
//     @Size(min = 3, max = 32, message = "Name must be between 3 and 32 characters")
//     private String name;
    
//     @NotBlank(message = "Email is required")
//     @Email(message = "Email should be valid")
//     @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@(gmail|yahoo|outlook)\\.com$", 
//              message = "Email must be gmail, yahoo, or outlook only")
//     private String email;
    
//     @NotBlank(message = "Phone is required")
//     @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be 10 digits starting with 6-9")
//     private String phone;
    
//     @NotBlank(message = "Password is required")
//     @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{5,12}$", 
//              message = "Password must be 5-12 chars, include uppercase, lowercase, and number")
//     private String password;
    
//     private User.Gender gender;
    
//     private User.UserType userType = User.UserType.USER;
// }

package com.hlopg_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String name;
    private String email;
    private String phone;
    private String password;
    private String gender;
}