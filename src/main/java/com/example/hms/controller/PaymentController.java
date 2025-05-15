package com.example.hms.controller;

import com.example.hms.model.PaymentManagementDTO;
import com.example.hms.model.PaymentReqDTO;
import com.example.hms.model.PaymentResDTO;
import com.example.hms.service.PayOSService;
import com.example.hms.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PayOSService payOSService;

    @GetMapping("/payments/status")
    public List<String> getAllPaymentStatuses() {
        return paymentService.getAllPaymentStatuses();
    }

    @GetMapping("/payments/method")
    public List<String> getAllPaymentMethods() {
        return paymentService.getAllPaymentMethods();
    }

    @GetMapping("/admin/payments")
    public ResponseEntity<List<PaymentManagementDTO>> getPaymentManagementList() {
        return ResponseEntity.ok(paymentService.getPaymentManagementList());
    }

    @GetMapping("/admin/payments/{id}")
    public ResponseEntity<PaymentResDTO> getPaymentDetail(@PathVariable("id") Integer id) {
        PaymentResDTO payment = paymentService.getPaymentDetailById(id);
        return ResponseEntity.ok(payment);
    }

    @PutMapping("/admin/payments/{transactionId}")
    public ResponseEntity<Void> updatePaymentDetail(@PathVariable("transactionId") Integer transactionId,
                                                    @RequestBody PaymentReqDTO dto) {
        paymentService.updatePaymentDetail(transactionId, dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/payments/create-link")
    public ResponseEntity<?> createPaymentLink(@RequestParam("transactionId") Integer transactionId) {
        try {
            Integer bookingId = paymentService.getBookingIdByTransactionId(transactionId);
            if (bookingId == null) {
                throw new RuntimeException("Không tìm thấy bookingId từ transactionId: " + transactionId);
            }
            var response = payOSService.createPaymentLink(bookingId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("Tạo liên kết thanh toán thất bại: " + e.getMessage());
        }
    }
}
