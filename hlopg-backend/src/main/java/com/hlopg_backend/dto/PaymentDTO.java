package com.hlopg_backend.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PaymentDTO {
    private String tenantName;
    private String tenantPhone;
    private String tenantEmail;
    private Long pgId;
    private String pgName;
    private String roomNumber;
    private Double amount;
    private LocalDate paymentDate;
    private LocalDate dueDate;
    private String status;
    private String paymentMethod;
    private String sharingType;
    private String notes;
}

