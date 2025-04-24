package com.example.hms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingManagementDTO {
    // full name of the customer
    private String fullName;
    private String roomNumber;
    private String checkInDate;
    private String checkOutDate;
    private String bookingStatus;
}
