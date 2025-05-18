package com.example.hms.controller;

import com.example.hms.model.*;
import com.example.hms.service.ActivityLogService;
import com.example.hms.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping("/bookings/status")
    public List<String> getAllBookingStatuses() {
        return bookingService.getAllBookingStatuses();
    }

    @GetMapping("/admin/bookings")
    public ResponseEntity<List<BookingManagementDTO>> getBookingManagementList() {
        return ResponseEntity.ok(bookingService.getBookingManagementList());
    }

    @GetMapping("/admin/bookings/{bookingId}")
    public ResponseEntity<?> getBookingDetail(@PathVariable int bookingId) {
        return ResponseEntity.ok(bookingService.getBookingDetailById(bookingId));
    }

    @PutMapping("/admin/bookings/{bookingId}")
    public ResponseEntity<?> updateBooking(@PathVariable int bookingId,
                                           @RequestBody BookingReqDTO bookingReqDTO) {
        bookingService.updateBookingDetail(bookingId, bookingReqDTO);
        activityLogService.log("Cập nhật booking với ID: " + bookingId);
        return ResponseEntity.ok("Booking updated successfully");
    }

    @GetMapping("/admin/bookings/calculate-price")
    public ResponseEntity<?> calculateTotalAmount(@RequestParam String roomNumber,
                                                  @RequestParam String checkInDate,
                                                  @RequestParam String checkOutDate) {
        double total = bookingService.calculateTotalAmount(roomNumber, checkInDate, checkOutDate);
        return ResponseEntity.ok(Collections.singletonMap("totalAmount", total));
    }

    @PostMapping("/admin/bookings")
    public ResponseEntity<?> createBooking(@RequestBody BookingCreateDTO bookingCreateDTO) {
        try {
            bookingService.createBooking(bookingCreateDTO);
            activityLogService.log("Tạo booking mới với phòng: " + bookingCreateDTO.getRoomNumber());
            return ResponseEntity.status(HttpStatus.CREATED).body("Tạo booking thành công!");
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tạo booking");
        }
    }

    @GetMapping("/bookings/customer")
    public ResponseEntity<List<BookingByUserDTO>> getBookingsByUserEmail(@RequestParam String email) {
        return ResponseEntity.ok(bookingService.getBookingsByUserEmail(email));
    }
}
