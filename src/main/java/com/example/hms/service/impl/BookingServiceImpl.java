package com.example.hms.service.impl;

import com.example.hms.entity.*;
import com.example.hms.model.*;
import com.example.hms.repository.BookingRepo;
import com.example.hms.repository.PaymentRepo;
import com.example.hms.repository.RoomRepo;
import com.example.hms.repository.UserRepo;
import com.example.hms.service.BookingService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {
    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PaymentRepo paymentRepo;

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
            LocalDate checkInDate = ((Date) row[3]).toLocalDate();
            LocalDate checkOutDate = ((Date) row[4]).toLocalDate();
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
        LocalDate checkIn = ((Date) row[3]).toLocalDate();
        LocalDate checkOut = ((Date) row[4]).toLocalDate();
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
        } else if ("Nhận phòng".equalsIgnoreCase(bookingStatus)) {
            room.setRoomStatus("Đang sử dụng");
        } else if ("Xác nhận".equalsIgnoreCase(bookingStatus)) {
            room.setRoomStatus("Đã xác nhận");
        } else {
            room.setRoomStatus("Chờ");
        }
        roomRepo.save(room);

        booking.setRoom(room);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setStatus(reqDTO.getBookingStatus());
        booking.setTotalAmount(reqDTO.getTotalPrice());

        bookingRepo.save(booking);
    }

    @Override
    public double calculateTotalAmount(String roomNumber, String checkInDateStr, String checkOutDateStr) {
        // Parse day
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate checkInDate = LocalDate.parse(checkInDateStr, formatter);
        LocalDate checkOutDate = LocalDate.parse(checkOutDateStr, formatter);

        // Calculate number of nights
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (nights <= 0) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }

        // Find room by room number
        Rooms room = roomRepo.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new RuntimeException("Room not found with number: " + roomNumber));

        // Get room type and price
        RoomTypes roomType = room.getRoomType();
        double pricePerNight = roomType.getPricePerNight();

        return pricePerNight * nights;
    }

    // Check if the room is booked
    private boolean isRoomBooked(String email, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Bookings> bookings = bookingRepo.findBookingsByEmailAndDateRange(email, checkInDate, checkOutDate);
        return !bookings.isEmpty();
    }

    @Override
    public void createBooking(BookingCreateDTO bookingCreateDTO) {
        String email = bookingCreateDTO.getEmail();
        String roomNumber = bookingCreateDTO.getRoomNumber();
        LocalDate checkInDate = LocalDate.parse(bookingCreateDTO.getCheckInDate());
        LocalDate checkOutDate = LocalDate.parse(bookingCreateDTO.getCheckOutDate());

        // Check if the room is already booked
        if (isRoomBooked(email, checkInDate, checkOutDate)) {
            throw new IllegalStateException("Khách hàng đang có booking trong thời gian này.");
        }

        Rooms room = roomRepo.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng này."));

        Users customer = userRepo.findByEmail(email);

        Bookings booking = new Bookings();
        booking.setCustomer(customer);
        booking.setRoom(room);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setTotalAmount(bookingCreateDTO.getTotalAmount());
        booking.setStatus("Chờ");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        bookingRepo.save(booking);

        Payments payment = new Payments();
        payment.setBooking(booking);
        payment.setPaymentStatus("Chờ");
        payment.setAmount(null);
        payment.setPaymentMethod(null);
        payment.setPaymentDate(null);
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepo.save(payment);
    }

    @Override
    public List<BookingByUserDTO> getBookingsByUserEmail(String email) {
        List<Bookings> bookings = bookingRepo.findBookingsByCustomerEmail(email);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<BookingByUserDTO> result = new ArrayList<>();

        for (Bookings b : bookings) {
            Rooms room = b.getRoom();
            RoomTypes roomType = room.getRoomType();

            // Lấy tên services từ roomTypeServices
            List<String> serviceNames = roomType.getRoomTypeServices()
                    .stream()
                    .map(rts -> rts.getService().getServiceName())
                    .toList();

            String roomTypeStr = "Phòng " + roomType.getType() + " - " + roomType.getStar() + " sao";

            BookingByUserDTO dto = new BookingByUserDTO();
            dto.setBookingId(b.getId());
            dto.setRoomNumber(room.getRoomNumber());
            dto.setRoomType(roomTypeStr);
            dto.setServices(serviceNames);
            dto.setCheckInDate(b.getCheckInDate().format(formatter));
            dto.setCheckOutDate(b.getCheckOutDate().format(formatter));
            dto.setBookingStatus(b.getStatus());
            dto.setPrice(b.getTotalAmount());

            result.add(dto);
        }
        return result;
    }
}
