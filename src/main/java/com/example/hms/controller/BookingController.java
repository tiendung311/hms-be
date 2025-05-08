package com.example.hms.controller;

import com.example.hms.model.BookingManagementDTO;
import com.example.hms.model.BookingReqDTO;
import com.example.hms.model.BookingResDTO;
import com.example.hms.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BookingController {
    @Autowired
    private BookingService bookingService;

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
        return ResponseEntity.ok("Booking updated successfully");
    }

    @GetMapping("/admin/bookings/calculate-price")
    public ResponseEntity<?> calculateTotalAmount(@RequestParam String roomNumber,
                                                  @RequestParam String checkInDate,
                                                  @RequestParam String checkOutDate) {
        double total = bookingService.calculateTotalAmount(roomNumber, checkInDate, checkOutDate);
        return ResponseEntity.ok(Collections.singletonMap("totalAmount", total));
    }
}
