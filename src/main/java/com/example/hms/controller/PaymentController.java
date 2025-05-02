package com.example.hms.controller;

import com.example.hms.model.PaymentManagementDTO;
import com.example.hms.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
