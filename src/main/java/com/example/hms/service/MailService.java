package com.example.hms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendBookingReminder(String toEmail, String customerName, String roomName, String checkInDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Nhắc lịch nhận phòng khách sạn");
        message.setText("Chào " + customerName + ",\n\n"
                + "Bạn có lịch nhận phòng cho phòng \"" + roomName + "\" vào ngày " + checkInDate + ".\n"
                + "Vui lòng đến đúng giờ để nhận phòng.\n\n"
                + "Trân trọng,\nKhách sạn HMS");
        mailSender.send(message);
    }
}
