// package com.hlopg_backend.dto;

// import com.fasterxml.jackson.annotation.JsonInclude;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// @JsonInclude(JsonInclude.Include.NON_NULL)
// public class AuthResponse {
//     private String token;
//     private String message;
//     private boolean success;
//     private UserResponse user;
//     private OwnerResponse owner;
    
//     @Data
//     @Builder
//     public static class UserResponse {
//         private Long userId;
//         private String name;
//         private String email;
//         private String phone;
//         private String userType;
//     }
    
//     @Data
//     @Builder
//     public static class OwnerResponse {
//         private Long ownerId;
//         private String name;
//         private String email;
//         private String phone;
//         private String userType;
//     }
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
public class AuthResponse {
    private boolean success;
    private String message;
    private String token;
    private UserResponse user;
    private OwnerResponse owner;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private Long userId;
        private String name;
        private String email;
        private String phone;
        private String userType;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OwnerResponse {
        private Long ownerId;
        private String name;
        private String email;
        private String phone;
        private String userType;
    }
}