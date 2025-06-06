package com.example.hms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingManagementDTO {
    // full name of the customer
    private Integer bookingId;
    private String fullName;
    private String roomNumber;
    private String checkInDate;
    private String checkOutDate;
    private String bookingStatus;
}
