package com.example.hms.service;

import com.example.hms.config.PayOSConfig;
import com.example.hms.entity.Payments;
import com.example.hms.model.PayOSLinkResDTO;
import com.example.hms.repository.BookingRepo;
import com.example.hms.repository.PaymentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;

import java.time.LocalDateTime;
import java.util.Optional;

import vn.payos.type.PaymentData;
import vn.payos.type.ItemData;
import vn.payos.type.CheckoutResponseData;

@Service
@RequiredArgsConstructor
public class PayOSService {

    private final PayOSConfig payOSConfig;
    private final BookingRepo bookingRepo;
    private final PaymentRepo paymentRepo;

    public PayOSLinkResDTO createPaymentLink(Integer bookingId) {
        var booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));

        // Kiểm tra xem đã tồn tại payment với trạng thái CHỜ hoặc PENDING chưa
        Optional<Payments> existingPaymentOpt = paymentRepo.findFirstByBooking_IdAndPaymentStatus(
                bookingId, "CHỜ"
        );

        String returnUrl = "http://localhost:3000/payos/success";
        String cancelUrl = "http://localhost:3000/payos/cancel";
        long orderCode = System.currentTimeMillis(); // Hoặc sử dụng UUID nếu cần

        PayOS payOS = new PayOS(
                payOSConfig.getClientId(),
                payOSConfig.getApiKey(),
                payOSConfig.getChecksumKey()
        );

        ItemData item = ItemData.builder()
                .name("Tên sản phẩm")
                .quantity(1)
                .price(booking.getTotalAmount().intValue())
                .build();

        String fullName = booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName();

        PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .amount(booking.getTotalAmount().intValue())
                .description(fullName + " Dat Phong")
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .item(item)
                .build();

        try {
            CheckoutResponseData data = payOS.createPaymentLink(paymentData);

            Payments payment;
            if (existingPaymentOpt.isPresent()) {
                payment = existingPaymentOpt.get();
                payment.setCreatedAt(LocalDateTime.now());
            } else {
                payment = new Payments();
                payment.setBooking(booking);
                payment.setAmount((double) paymentData.getAmount());
                payment.setPaymentMethod("Chuyển khoản");
                payment.setPaymentStatus("Chờ");
                payment.setCreatedAt(LocalDateTime.now());
            }

            paymentRepo.save(payment);

            return new PayOSLinkResDTO(data.getCheckoutUrl(), String.valueOf(orderCode));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Tạo liên kết thanh toán thất bại: " + e.getMessage(), e);
        }
    }
}