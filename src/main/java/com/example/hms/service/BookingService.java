package com.example.hms.service;

import com.example.hms.model.BookingManagementDTO;

import java.util.List;

public interface BookingService {
    List<String> getAllBookingStatuses();

    List<BookingManagementDTO> getBookingManagementList();
}
