package com.example.hms.service.impl;

import com.example.hms.entity.Bookings;
import com.example.hms.entity.Payments;
import com.example.hms.entity.Rooms;
import com.example.hms.entity.Users;
import com.example.hms.repository.BookingRepo;
import com.example.hms.repository.PaymentRepo;
import com.example.hms.repository.RoomRepo;
import com.example.hms.service.MailService;
import com.example.hms.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {

    private final BookingRepo bookingRepo;

    private final RoomRepo roomRepo;

    private final MailService mailService;

    private final PaymentRepo paymentRepo;

    // Chạy mỗi phút (test)
    //@Scheduled(cron = "0 * * * * *")
    // Tùy chọn thay thế để chạy mỗi 30 phút:
    // @Scheduled(cron = "0 */30 * * * *")
    // Hoặc mỗi giờ:
     @Scheduled(cron = "0 0 * * * *")
    @Override
    public void updateRoomStatuses() {
        LocalDate today = LocalDate.now();

        List<Bookings> activeBookings = bookingRepo.findAllByCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
                today, today);

        Set<Integer> updatedRoomIds = new HashSet<>();

        System.out.println("Running scheduled job at " + LocalDateTime.now());
        System.out.println("Found bookings: " + activeBookings.size());

        for (Bookings booking : activeBookings) {
            Rooms room = booking.getRoom();

            if (room == null || updatedRoomIds.contains(room.getId())) continue;

            String bookingStatus = booking.getStatus();

            if ("Hủy".equalsIgnoreCase(bookingStatus) || "Trả phòng".equalsIgnoreCase(bookingStatus)) {
                room.setRoomStatus("Trống");
            } else if ("Nhận phòng".equalsIgnoreCase(bookingStatus)) {
                room.setRoomStatus("Đang sử dụng");
            } else if ("Xác nhận".equalsIgnoreCase(bookingStatus)) {
                room.setRoomStatus("Đã xác nhận");
            } else {
                room.setRoomStatus("Chờ");
            }

            roomRepo.save(room);
            updatedRoomIds.add(room.getId());
        }
    }

    @Scheduled(cron = "0 0 */12 * * *")
    public void sendCheckInReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<Bookings> bookings = bookingRepo.findAllByStatusAndCheckInDate("Xác nhận", tomorrow);

        System.out.println("Found " + bookings.size() + " bookings for tomorrow check-in");

        for (Bookings booking : bookings) {
            Users customer = booking.getCustomer();
            if (customer.getEmail() != null) {
                mailService.sendBookingReminder(
                        customer.getEmail(),
                        customer.getFirstName() + " " + customer.getLastName(),
                        booking.getRoom().getRoomNumber(),
                        booking.getCheckInDate().toString()
                );
            }
        }
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void cancelExpiredPendingPayments() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredTime = now.minusMinutes(20);

        // Lấy tất cả payment có trạng thái "Chờ" và createdAt trước thời điểm 20 phút trước
        List<Payments> pendingPayments = paymentRepo.findAllByPaymentStatusAndCreatedAtBefore("Chờ", expiredTime);

        for (Payments payment : pendingPayments) {
            payment.setPaymentStatus("Hết hạn");

            Bookings booking = payment.getBooking();
            booking.setStatus("Hủy");
            booking.setUpdatedAt(now);

            bookingRepo.save(booking);
            paymentRepo.save(payment);

            System.out.println("Hủy booking ID " + booking.getId() + " vì quá thời gian thanh toán");
        }
    }
}
