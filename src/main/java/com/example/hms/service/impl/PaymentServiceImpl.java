package com.example.hms.service.impl;

import com.example.hms.entity.Payments;
import com.example.hms.model.PaymentManagementDTO;
import com.example.hms.model.PaymentReqDTO;
import com.example.hms.model.PaymentResDTO;
import com.example.hms.repository.PaymentRepo;
import com.example.hms.service.PaymentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentRepo paymentRepo;

    @Override
    public List<String> getAllPaymentStatuses() {
        return paymentRepo.getAllPaymentStatuses();
    }

    @Override
    public List<String> getAllPaymentMethods() {
        return paymentRepo.getAllPaymentMethods();
    }

    @Override
    public List<PaymentManagementDTO> getPaymentManagementList() {
        List<Object[]> rawData = paymentRepo.fetchPaymentManagementData();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        List<PaymentManagementDTO> result = new ArrayList<>();
        for (Object[] row : rawData) {
            Integer transactionId = (Integer) row[0];
            String fullName = (String) row[1];
            String roomNumber = (String) row[2];
            Timestamp paymentTimestamp = (Timestamp) row[3];
            String paymentMethod = (String) row[4];
            String paymentStatus = (String) row[5];
            Double amount = (Double) row[6];

            String formattedPaymentDate;
            if (paymentTimestamp != null) {
                LocalDateTime paymentDateTime = paymentTimestamp.toLocalDateTime();
                formattedPaymentDate = paymentDateTime.format(formatter);
            } else {
                formattedPaymentDate = "Chưa thanh toán";
            }

            PaymentManagementDTO dto = new PaymentManagementDTO();
            dto.setTransactionId(transactionId);
            dto.setFullName(fullName);
            dto.setRoomNumber(roomNumber);
            dto.setPaymentDate(formattedPaymentDate);
            dto.setPaymentMethod(paymentMethod);
            dto.setPaymentStatus(paymentStatus);
            dto.setAmount(amount);

            result.add(dto);
        }
        return result;
    }

    @Override
    public PaymentResDTO getPaymentDetailById(int transactionId) {
        Object res = paymentRepo.fetchPaymentDetailById(transactionId);
        if (res == null) {
            throw new RuntimeException("Payment with ID " + transactionId + " not found.");
        }

        Object[] row = (Object[]) res;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        String formattedDate = null;
        if (row[3] != null) {
            LocalDateTime paymentDateTime = ((Timestamp) row[3]).toLocalDateTime();
            formattedDate = paymentDateTime.format(formatter);
        } else {
            formattedDate = "Chưa thanh toán";
        }

        Double amount = row[6] != null ? (Double) row[6] : 0.0;

        return new PaymentResDTO(
                (Integer) row[0],
                (String) row[1],
                (String) row[2],
                formattedDate,
                (String) row[4],
                (String) row[5],
                amount
        );
    }

    @Override
    @Transactional
    public void updatePaymentDetail(int transactionId, PaymentReqDTO dto) {
        Payments payments = paymentRepo.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        payments.setPaymentStatus(dto.getPaymentStatus());
        payments.setPaymentMethod(dto.getPaymentMethod());

        paymentRepo.save(payments);
    }
}
