package com.example.hms.service.impl;

import com.example.hms.model.BookingManagementDTO;
import com.example.hms.repository.BookingRepo;
import com.example.hms.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    @Autowired
    private BookingRepo bookingRepo;

    @Override
    public List<String> getAllBookingStatuses() {
        return bookingRepo.getAllBookingStatus();
    }

    @Override
    public List<BookingManagementDTO> getBookingManagementList() {
        List<Object[]> rawData = bookingRepo.fetchBookingManagementData();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<BookingManagementDTO> result = new ArrayList<>();
        for (Object[] row : rawData) {
            String fullName = (String) row[0];
            String roomNumber = (String) row[1];
            LocalDate checkInDate = ((Timestamp) row[2]).toLocalDateTime().toLocalDate();
            LocalDate checkOutDate = ((Timestamp) row[3]).toLocalDateTime().toLocalDate();
            String status = (String) row[4];

            String formattedCheckInDate = checkInDate.format(formatter);
            String formattedCheckOutDate = checkOutDate.format(formatter);

            result.add(new BookingManagementDTO(fullName, roomNumber,
                    formattedCheckInDate, formattedCheckOutDate, status));
        }
        return result;
    }
}
