package com.example.hms.controller;

import com.example.hms.service.PaymentService;
import com.example.hms.service.RoomService;
import com.example.hms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin")
public class AdminHomeController {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    @GetMapping("/total-amount/success")
    public BigDecimal getTotalAmountByStatus() {
        String status = "Thành công";
        BigDecimal totalAmount = paymentService.getTotalAmountByStatus(status);
        return totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }

    @GetMapping("/total-amount/month/now")
    public BigDecimal getTotalAmountByCurrentMonth() {
        String status = "Thành công";
        LocalDateTime now = LocalDateTime.now();
        int month = now.getMonthValue();
        int year = now.getYear();
        BigDecimal total = paymentService.getTotalAmountByMonth(status, month, year);
        return total != null ? total : BigDecimal.ZERO;
    }

    @GetMapping("/total-users")
    private Integer getTotalUsers() {
        return userService.getTotalActiveUsers();
    }

    @GetMapping("/total-rooms")
    private Integer getTotalRooms() {
        return roomService.countAllRoom();
    }
}
