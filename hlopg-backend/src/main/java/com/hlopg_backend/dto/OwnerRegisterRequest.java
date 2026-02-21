// package com.hlopg_backend.dto;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// public class OwnerRegisterRequest {
//     private String name;
//     private String email;
//     private String phone;
//     private String password;
// }

// OwnerRegistrationRequest.java
package com.hlopg_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OwnerRegisterRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
    private String phone;
    
    @NotBlank(message = "Password is required")
    private String password;
}