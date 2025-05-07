package com.example.hms.service;

import com.example.hms.model.BookingManagementDTO;
import com.example.hms.model.BookingResDTO;

import java.util.List;

public interface BookingService {
    List<String> getAllBookingStatuses();

    List<BookingManagementDTO> getBookingManagementList();

    BookingResDTO getBookingDetailById(int id);
}
