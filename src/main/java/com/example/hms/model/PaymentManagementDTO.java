package com.example.hms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentManagementDTO {
    // full name of the customer
    private String fullName;
    private String roomNumber;
    private String paymentDate;
    private String paymentMethod;
    private String paymentStatus;
    private Double amount;
}
