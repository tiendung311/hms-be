package com.example.hms.service.impl;

import com.example.hms.entity.Bookings;
import com.example.hms.entity.Rooms;
import com.example.hms.repository.BookingRepo;
import com.example.hms.repository.RoomRepo;
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

    // Chạy mỗi phút (test)
    @Scheduled(cron = "0 * * * * *")
    // Tùy chọn thay thế để chạy mỗi 30 phút:
    // @Scheduled(cron = "0 */30 * * * *")
    // Hoặc mỗi giờ:
    // @Scheduled(cron = "0 0 * * * *")
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
}
