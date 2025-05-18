package com.example.hms.service;

import com.example.hms.model.*;

import java.util.List;

public interface BookingService {
    List<String> getAllBookingStatuses();

    List<BookingManagementDTO> getBookingManagementList();

    BookingResDTO getBookingDetailById(int id);

    void updateBookingDetail(int bookingId, BookingReqDTO reqDTO);

    double calculateTotalAmount(String roomNumber, String checkInDateStr, String checkOutDateStr);

    void createBooking(BookingCreateDTO bookingCreateDTO);

    List<BookingByUserDTO> getBookingsByUserEmail(String email);
}
