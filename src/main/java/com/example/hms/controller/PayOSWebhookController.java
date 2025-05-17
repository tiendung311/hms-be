package com.example.hms.controller;

import com.example.hms.entity.Bookings;
import com.example.hms.entity.Payments;
import com.example.hms.model.PayOSLinkResDTO;
import com.example.hms.model.PayOSWebhookPayload;
import com.example.hms.repository.BookingRepo;
import com.example.hms.repository.PaymentRepo;
import com.example.hms.service.PayOSService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@RequestMapping("/api/payos")
@RequiredArgsConstructor
public class PayOSWebhookController {

    private final BookingRepo bookingRepo;

    private final PaymentRepo paymentRepo;

    private final PayOSService payOSService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhookRaw(@RequestBody String rawBody) {
        System.out.println("👉 Webhook nhận được từ PayOS:");
        System.out.println(rawBody);

        ObjectMapper mapper = new ObjectMapper();
        try {
            PayOSWebhookPayload payload = mapper.readValue(rawBody, PayOSWebhookPayload.class);

            // Xử lý nếu success = true
            if (payload.isSuccess() && payload.getData() != null) {
                // Gọi method xử lý logic update DB
                handleValidPayment(payload);
            } else {
                System.out.println("❗ Đây là webhook test hoặc không thành công. Bỏ qua xử lý.");
            }

            // Dù là test hay thật, vẫn trả 200 OK để PayOS không báo lỗi
            return ResponseEntity.ok("Webhook xử lý xong");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("❌ Parse JSON lỗi: " + e.getMessage());
        }
    }

    private void handleValidPayment(PayOSWebhookPayload payload) {
        var data = payload.getData();
        Long orderCode = data.getOrderCode();

        if (orderCode == null) {
            System.err.println("❌ orderCode null, bỏ qua xử lý");
            return;
        }

        Optional<Payments> paymentOpt = paymentRepo.findByOrderCode(orderCode);

        if (paymentOpt.isEmpty()) {
            System.err.println("❌ Không tìm thấy payment với orderCode: " + orderCode);
            return;
        }

        Payments payment = paymentOpt.get();
        Bookings booking = payment.getBooking(); // lấy booking từ payment

        if ("00".equals(data.getCode())) {
            payment.setPaymentStatus("Thành công");
            booking.setStatus("Xác nhận");
        } else {
            payment.setPaymentStatus("Thất bại");
            booking.setStatus("Chờ");
        }

        payment.setAmount(data.getAmount());
        payment.setPaymentMethod("Chuyển khoản");

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime paymentDate = LocalDateTime.parse(data.getTransactionDateTime(), formatter);
            payment.setPaymentDate(paymentDate);
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi parse thời gian thanh toán: " + e.getMessage());
        }

        paymentRepo.save(payment);
        bookingRepo.save(booking);
    }

    @PostMapping("/create-customer-link")
    public ResponseEntity<PayOSLinkResDTO> createCustomerPayment(
            @RequestParam String email,
            @RequestParam Integer roomTypeId,
            @RequestParam String checkIn,
            @RequestParam String checkOut
    ) {
        try {
            LocalDate checkInDate = LocalDate.parse(checkIn);
            LocalDate checkOutDate = LocalDate.parse(checkOut);

            System.out.println("data: " + email + ", " + roomTypeId + ", " + checkInDate + ", " + checkOutDate);

            PayOSLinkResDTO res = payOSService.createCustomerPaymentLink(email, roomTypeId, checkInDate, checkOutDate);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new PayOSLinkResDTO(null, "❌ " + e.getMessage()));
        }
    }
}
