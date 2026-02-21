package com.hlopg_backend.dto;

import java.util.Date;

import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long ownerId;
    private String type;
    private String title;
    private String message;
    private Long hostelId;
    private String hostelName;
    private String hostelAddress;
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String sharingType;
    private String bookingId;
    private Boolean read;
    private Date createdAt;
}