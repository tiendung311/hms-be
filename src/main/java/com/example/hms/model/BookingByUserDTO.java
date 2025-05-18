package com.example.hms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingByUserDTO {
    private Integer bookingId;
    private String roomNumber;
    // roomType = type + star sao
    private String roomType;
    private List<String> services;
    private String checkInDate;
    private String checkOutDate;
    private String bookingStatus;
    private Double price;
}
