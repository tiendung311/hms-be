package com.example.hms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentReqDTO {
    // This dto used to update the payment details
    private String paymentMethod;
    private String paymentStatus;
}
