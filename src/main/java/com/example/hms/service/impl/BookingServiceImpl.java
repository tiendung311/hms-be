package com.example.hms.service.impl;

import com.example.hms.entity.Bookings;
import com.example.hms.entity.Rooms;
import com.example.hms.model.BookingManagementDTO;
import com.example.hms.model.BookingReqDTO;
import com.example.hms.model.BookingResDTO;
import com.example.hms.repository.BookingRepo;
import com.example.hms.repository.RoomRepo;
import com.example.hms.service.BookingService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {
    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private RoomRepo roomRepo;

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
        LocalDate checkIn = ((Timestamp) row[3]).toLocalDateTime().toLocalDate();
        LocalDate checkOut = ((Timestamp) row[4]).toLocalDateTime().toLocalDate();
        String status = (String) row[5];
        double totalPrice = ((Number) row[6]).doubleValue();

        return new BookingResDTO(
                bookingId,
                fullName,
                roomNumber,
                checkIn.format(formatter),
                checkOut.format(formatter),
                status,
                totalPrice
        );
    }

    @Override
    @Transactional
    public void updateBookingDetail(int bookingId, BookingReqDTO reqDTO) {
        Optional<Bookings> optionalBooking = bookingRepo.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new RuntimeException("Booking not found with ID: " + bookingId);
        }

        Bookings booking = optionalBooking.get();

        // Format date from String to LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate checkInDate = LocalDate.parse(reqDTO.getCheckInDate(), formatter);
        LocalDate checkOutDate = LocalDate.parse(reqDTO.getCheckOutDate(), formatter);

        // Set status for old room
        Rooms oldRoom = booking.getRoom();
        if (!oldRoom.getRoomNumber().equals(reqDTO.getRoomNumber())) {
            oldRoom.setRoomStatus("Trống");
            roomRepo.save(oldRoom);
        }

        // Find room by room number
        Rooms room = roomRepo.findByRoomNumber(reqDTO.getRoomNumber())
                .orElseThrow(() -> new RuntimeException("Room not found with number: " + reqDTO.getRoomNumber()));

        String bookingStatus = reqDTO.getBookingStatus();
        if ("Hủy".equalsIgnoreCase(bookingStatus) || "Trả phòng".equalsIgnoreCase(bookingStatus)) {
            room.setRoomStatus("Trống");
        } else {
            room.setRoomStatus("Đã đặt");
        }
        roomRepo.save(room);

        booking.setRoom(room);
        booking.setCheckInDate(checkInDate.atStartOfDay());
        booking.setCheckOutDate(checkOutDate.atStartOfDay());
        booking.setStatus(reqDTO.getBookingStatus());
        booking.setTotalAmount(reqDTO.getTotalPrice());

        bookingRepo.save(booking);
    }
}
