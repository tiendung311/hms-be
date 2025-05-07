package com.example.hms.service.impl;

import com.example.hms.model.BookingManagementDTO;
import com.example.hms.model.BookingResDTO;
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
            Integer bookingId = (Integer) row[0];
            String fullName = (String) row[1];
            String roomNumber = (String) row[2];
            LocalDate checkInDate = ((Timestamp) row[3]).toLocalDateTime().toLocalDate();
            LocalDate checkOutDate = ((Timestamp) row[4]).toLocalDateTime().toLocalDate();
            String status = (String) row[5];

            String formattedCheckInDate = checkInDate.format(formatter);
            String formattedCheckOutDate = checkOutDate.format(formatter);

            result.add(new BookingManagementDTO(bookingId, fullName, roomNumber,
                    formattedCheckInDate, formattedCheckOutDate, status));
        }
        return result;
    }

    @Override
    public BookingResDTO getBookingDetailById(int id) {
        Object res = bookingRepo.fetchBookingDetailById(id);
        if (res == null) throw new RuntimeException("Booking not found with id: " + id);

        Object[] row = (Object[]) res;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Integer bookingId = (Integer) row[0];
        String fullName = (String) row[1];
        String roomNumber = (String) row[2];
        String roomType = (String) row[3];
        LocalDate checkIn = ((Timestamp) row[4]).toLocalDateTime().toLocalDate();
        LocalDate checkOut = ((Timestamp) row[5]).toLocalDateTime().toLocalDate();
        String status = (String) row[6];
        double totalPrice = ((Number) row[7]).doubleValue();

        return new BookingResDTO(
                bookingId,
                fullName,
                roomNumber,
                roomType,
                checkIn.format(formatter),
                checkOut.format(formatter),
                status,
                totalPrice
        );
    }
}
