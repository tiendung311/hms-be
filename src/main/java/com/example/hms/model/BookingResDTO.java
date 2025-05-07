package com.example.hms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResDTO {
    // This dto used to return the booking details
    private Integer bookingId;
    private String fullName;
    private String roomNumber;
    private String roomType;
    private String checkInDate;
    private String checkOutDate;
    private String bookingStatus;
    private double totalPrice;
}
