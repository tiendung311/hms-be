package com.example.hms.controller;

import com.example.hms.model.PaymentManagementDTO;
import com.example.hms.model.PaymentReqDTO;
import com.example.hms.model.PaymentResDTO;
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
}
