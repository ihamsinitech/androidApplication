package com.hlopg_backend.dto;

import lombok.Data;

@Data

public class PaymentSummaryDTO {
    private double totalAmount;
    private double pendingAmount;
    private double paidAmount;
    private double overdueAmount;
    private int totalPayments;
    private int pendingCount;
    private int paidCount;
    private int overdueCount;
}