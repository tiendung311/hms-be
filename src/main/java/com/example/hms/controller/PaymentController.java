package com.example.hms.controller;

import com.example.hms.model.MonthlyNetRevenueDTO;
import com.example.hms.model.PaymentManagementDTO;
import com.example.hms.model.PaymentReqDTO;
import com.example.hms.model.PaymentResDTO;
import com.example.hms.repository.PaymentRepo;
import com.example.hms.service.ActivityLogService;
import com.example.hms.service.PayOSService;
import com.example.hms.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepo paymentRepo;

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

    @GetMapping("/payments/date-range")
    public ResponseEntity<Map<String, String>> getPaymentDateRange() {
        LocalDate minDate = paymentRepo.findMinPaymentDate();
        LocalDate maxDate = paymentRepo.findMaxPaymentDate();

        Map<String, String> dateRange = new HashMap<>();
        dateRange.put("fromDate", minDate.toString()); // yyyy-MM-dd
        dateRange.put("toDate", maxDate.toString());

        return ResponseEntity.ok(dateRange);
    }

    @GetMapping("/payments/total-income")
    public ResponseEntity<BigDecimal> getTotalIncome(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        LocalDateTime fromDateTime = from.atStartOfDay(); // 00:00
        LocalDateTime toDateTime = to.atTime(23, 59, 59); // cuối ngày
        return ResponseEntity.ok(paymentService.getTotalIncome(fromDateTime, toDateTime));
    }

    @GetMapping("/payments/total-refund")
    public ResponseEntity<BigDecimal> getTotalRefund(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        LocalDateTime fromDateTime = from.atStartOfDay(); // 00:00
        LocalDateTime toDateTime = to.atTime(23, 59, 59); // cuối ngày
        return ResponseEntity.ok(paymentService.getTotalRefund(fromDateTime, toDateTime));
    }

    @GetMapping("/payments/net-revenue")
    public ResponseEntity<BigDecimal> getNetRevenue(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        LocalDateTime fromDateTime = from.atStartOfDay(); // 00:00
        LocalDateTime toDateTime = to.atTime(23, 59, 59); // cuối ngày
        return ResponseEntity.ok(paymentService.getNetRevenue(fromDateTime, toDateTime));
    }

    @GetMapping("/payments/monthly-net-revenue")
    public ResponseEntity<?> getMonthlyNetRevenue(
            @RequestParam String from,
            @RequestParam String to
    ) {
        try {
            LocalDate fromDate = LocalDate.parse(from + "-01"); // parse yyyy-MM-dd, giả định ngày đầu tháng
            LocalDate toDate = LocalDate.parse(to + "-01");

            List<MonthlyNetRevenueDTO> data = paymentService.getMonthlyNetRevenue(fromDate, toDate);

            return ResponseEntity.ok(data);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Use yyyy-MM");
        }
    }
}
