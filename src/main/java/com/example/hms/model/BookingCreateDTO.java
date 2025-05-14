package com.example.hms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingCreateDTO {
    // email of the customer
    private String email;
    private String roomNumber;
    private String checkInDate;
    private String checkOutDate;
    private Double totalAmount;
}
