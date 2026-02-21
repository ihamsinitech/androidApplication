// File: src/main/java/com/hlopg_backend/dto/BookingRequestDTO.java
package com.hlopg_backend.dto;

import java.util.Date;

public class BookingRequestDTO {
    private Integer hostelId;
    private String hostelName;
    private String hostelAddress;
    private Integer userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String sharingType;
    private Date bookingDate;
    private String status;

    // Constructors
    public BookingRequestDTO() {}

    public BookingRequestDTO(Integer hostelId, String hostelName, String hostelAddress, 
                           Integer userId, String userName, String userEmail, 
                           String userPhone, String sharingType, Date bookingDate, String status) {
        this.hostelId = hostelId;
        this.hostelName = hostelName;
        this.hostelAddress = hostelAddress;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.sharingType = sharingType;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    // Getters and Setters
    public Integer getHostelId() { return hostelId; }
    public void setHostelId(Integer hostelId) { this.hostelId = hostelId; }

    public String getHostelName() { return hostelName; }
    public void setHostelName(String hostelName) { this.hostelName = hostelName; }

    public String getHostelAddress() { return hostelAddress; }
    public void setHostelAddress(String hostelAddress) { this.hostelAddress = hostelAddress; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }

    public String getSharingType() { return sharingType; }
    public void setSharingType(String sharingType) { this.sharingType = sharingType; }

    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}