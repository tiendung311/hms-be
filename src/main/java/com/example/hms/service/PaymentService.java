package com.example.hms.service;

import com.example.hms.model.PaymentManagementDTO;

import java.util.List;

public interface PaymentService {
    List<String> getAllPaymentStatuses();

    List<String> getAllPaymentMethods();

    List<PaymentManagementDTO> getPaymentManagementList();
}
