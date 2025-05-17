package com.example.hms.service;

import com.example.hms.config.PayOSConfig;
import com.example.hms.entity.Bookings;
import com.example.hms.entity.Payments;
import com.example.hms.entity.Rooms;
import com.example.hms.entity.Users;
import com.example.hms.model.PayOSLinkResDTO;
import com.example.hms.repository.BookingRepo;
import com.example.hms.repository.PaymentRepo;
import com.example.hms.repository.RoomRepo;
import com.example.hms.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
    private final RoomRepo roomRepo;
    private final UserRepo userRepo;

    // Tạo liên kết thanh toán cho admin
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
                .name("Đơn đặt phòng")
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
                payment.setOrderCode(orderCode);
            } else {
                payment = new Payments();
                payment.setBooking(booking);
                payment.setAmount((double) paymentData.getAmount());
                payment.setPaymentMethod("Chuyển khoản");
                payment.setPaymentStatus("Chờ");
                payment.setOrderCode(orderCode);
                payment.setCreatedAt(LocalDateTime.now());
            }

            paymentRepo.save(payment);

            return new PayOSLinkResDTO(data.getCheckoutUrl(), String.valueOf(orderCode));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Tạo liên kết thanh toán thất bại: " + e.getMessage(), e);
        }
    }

    public PayOSLinkResDTO createCustomerPaymentLink(String email, Integer roomTypeId,
                                                     LocalDate checkInDate, LocalDate checkOutDate) {

        // 1. Tìm phòng
        List<String> availableRoomNumbers = roomRepo.findAvailableRoomNumbers(checkInDate, checkOutDate);
        if (availableRoomNumbers.isEmpty()) {
            throw new RuntimeException("Không còn phòng trống trong khoảng thời gian này.");
        }

        List<Rooms> availableRooms = roomRepo.findAvailableRoomByRoomType(roomTypeId, availableRoomNumbers);
        if (availableRooms.isEmpty()) {
            throw new RuntimeException("Không còn phòng trống phù hợp với loại phòng này.");
        }

        Rooms selectedRoom = availableRooms.getFirst();
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        double totalAmount = nights * selectedRoom.getRoomType().getPricePerNight();
        Users customer = userRepo.findByEmail(email);

        // 2. Tạo booking và payment
        Bookings booking = new Bookings();
        booking.setCustomer(customer);
        booking.setRoom(selectedRoom);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setTotalAmount(totalAmount);
        booking.setStatus("Chờ");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepo.save(booking);

        Payments payment = new Payments();
        payment.setBooking(booking);
        payment.setAmount(totalAmount);
        payment.setPaymentStatus("Chờ");
        payment.setPaymentMethod("Chuyển khoản");
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepo.save(payment);

        System.out.println("✅ Booking ID just created: " + booking.getId());

        // 3. Gọi tạo link — nếu lỗi thì vẫn giữ lại booking và payment
        return createPaymentLink(booking.getId());
    }
}