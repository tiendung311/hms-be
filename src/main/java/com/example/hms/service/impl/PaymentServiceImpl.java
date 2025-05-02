package com.example.hms.service.impl;

import com.example.hms.model.PaymentManagementDTO;
import com.example.hms.repository.PaymentRepo;
import com.example.hms.service.PaymentService;
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
            String fullName = (String) row[0];
            String roomNumber = (String) row[1];
            LocalDateTime paymentDateTime = ((Timestamp) row[2]).toLocalDateTime();
            String paymentMethod = (String) row[3];
            String paymentStatus = (String) row[4];
            Double amount = (Double) row[5];

            String formattedPaymentDate = paymentDateTime.format(formatter);

            PaymentManagementDTO dto = new PaymentManagementDTO();
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
}
