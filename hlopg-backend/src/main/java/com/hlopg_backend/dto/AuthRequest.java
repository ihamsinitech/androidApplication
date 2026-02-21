package com.hlopg_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    
    @NotBlank(message = "Identifier (email/phone) is required")
    private String identifier;
    
    @NotBlank(message = "Password is required")
    private String password;
}
