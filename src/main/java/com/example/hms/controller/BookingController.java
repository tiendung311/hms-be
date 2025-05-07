package com.example.hms.controller;

import com.example.hms.model.BookingManagementDTO;
import com.example.hms.model.BookingResDTO;
import com.example.hms.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
