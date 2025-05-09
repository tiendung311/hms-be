package com.example.hms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResDTO {
    // This dto used to return the payment details
    private Integer transactionId;
    // fullName of the customer
    private String fullName;
    private String roomNumber;
    private String paymentDate;
    private String paymentMethod;
    private String paymentStatus;
    private double amount;
}
