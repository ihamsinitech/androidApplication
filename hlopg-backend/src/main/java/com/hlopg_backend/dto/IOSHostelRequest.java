package com.hlopg_backend.dto;

import lombok.Data;

@Data
public class IOSHostelRequest {

    // BASIC INFO
    private String pgName;
    private String pgInfo;
    private String pgType;

    // LOCATION (iOS ORDER)
    private String city;
    private String area;
    private String pincode;
    private String address;

    // AMENITIES
    private String amenities;      // JSON STRING

    // FOOD MENU
    private String foodMenu;       // JSON STRING

    // SHARING
    private String sharing;        // JSON STRING

    // FLOORS & ROOMS
    private Integer numFloors;
    private Integer roomsPerFloor;
    private Integer startingRoomNumber;

    // PAYMENT
    private Integer advanceAmount;
}
