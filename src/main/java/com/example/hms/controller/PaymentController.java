package com.example.hms.controller;

import com.example.hms.model.PaymentManagementDTO;
import com.example.hms.model.PaymentReqDTO;
import com.example.hms.model.PaymentResDTO;
import com.example.hms.service.ActivityLogService;
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

    @Autowired
    private ActivityLogService activityLogService;

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
        activityLogService.log("Cập nhật thông tin thanh toán với transactionId: " + transactionId);
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

    @PostMapping("/admin/payments/{paymentId}/refund")
    public ResponseEntity<String> refundPayment(@PathVariable("paymentId") Integer paymentId) {
        try {
            paymentService.refundPayment(paymentId);
            activityLogService.log("Hoàn tiền cho giao dịch: " + paymentId);
            return ResponseEntity.ok("Hoàn tiền thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body("Hoàn tiền thất bại: " + e.getMessage());
        }
    }
}
