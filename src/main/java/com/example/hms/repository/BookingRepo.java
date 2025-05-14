package com.example.hms.repository;

import com.example.hms.entity.Bookings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Bookings, Integer> {
    @Query("SELECT DISTINCT b.status FROM Bookings b")
    List<String> getAllBookingStatus();

    @Query(value = """
    SELECT 
        b.id,
        CONCAT(u.first_name, ' ', u.last_name) AS full_name,
        r.room_number,
        b.check_in_date,
        b.check_out_date,
        b.status
    FROM bookings b
    JOIN users u ON b.customer_id = u.id
    JOIN rooms r ON b.room_id = r.id
    ORDER BY b.check_in_date DESC
    """, nativeQuery = true)
    List<Object[]> fetchBookingManagementData();

    @Query(value = """
    SELECT 
        b.id,
        CONCAT(u.first_name, ' ', u.last_name) AS full_name,
        r.room_number,
        b.check_in_date,
        b.check_out_date,
        b.status,
        b.total_amount
    FROM bookings b
    JOIN users u ON b.customer_id = u.id
    JOIN rooms r ON b.room_id = r.id
    JOIN room_types rt ON r.room_type_id = rt.id
    WHERE b.id = :bookingId
    """, nativeQuery = true)
    Object fetchBookingDetailById(@Param("bookingId") int bookingId);

    @Query("SELECT b FROM Bookings b WHERE b.customer.email = :email " +
            "AND ((b.checkInDate BETWEEN :checkInDate AND :checkOutDate) " +
            "OR (b.checkOutDate BETWEEN :checkInDate AND :checkOutDate)) " +
            "AND b.status IN ('Chờ', 'Xác nhận', 'Nhận phòng')")
    List<Bookings> findBookingsByEmailAndDateRange(@Param("email") String email,
                                                   @Param("checkInDate") LocalDate checkInDate,
                                                   @Param("checkOutDate") LocalDate checkOutDate);
}
