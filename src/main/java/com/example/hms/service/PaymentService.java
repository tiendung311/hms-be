package com.example.hms.service;

import com.example.hms.model.PaymentManagementDTO;
import com.example.hms.model.PaymentReqDTO;
import com.example.hms.model.PaymentResDTO;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {
    List<String> getAllPaymentStatuses();

    List<String> getAllPaymentMethods();

    List<PaymentManagementDTO> getPaymentManagementList();

    PaymentResDTO getPaymentDetailById(int id);

    void updatePaymentDetail(int transactionId, PaymentReqDTO dto);

    Integer getBookingIdByTransactionId(Integer transactionId);

    BigDecimal getTotalAmountByStatus(String status);

    BigDecimal getTotalAmountByMonth(String status, int month, int year);
}
