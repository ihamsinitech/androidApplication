package com.hlopg_backend.dto;

import lombok.Data;

@Data
public class MonthlySummaryDTO {
    private String month;
    private Integer year;
    private int paymentCount;
    private double totalAmount;
    private double paidAmount;
    private double pendingAmount;
}