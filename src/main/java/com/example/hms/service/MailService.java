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

    public void sendCancellationEmail(String toEmail, String customerName, String roomNumber, String checkInDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Hủy đặt phòng thành công");
        message.setText("Chào " + customerName + ",\n\n"
                + "Yêu cầu hủy phòng \"" + roomNumber + "\" vào ngày " + checkInDate + " đã được xác nhận.\n"
                + "Khách sạn sẽ liên hệ với bạn để hoàn tiền trong thời gian sớm nhất.\n\n"
                + "Trân trọng,\nKhách sạn HMS");
        mailSender.send(message);
    }
}
