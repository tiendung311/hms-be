package com.example.hms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingReqDTO {
    // This dto used to update the booking details
    private String fullName;
    private String roomNumber;
    private String checkInDate;
    private String checkOutDate;
    private String bookingStatus;
    private double totalPrice;
}
