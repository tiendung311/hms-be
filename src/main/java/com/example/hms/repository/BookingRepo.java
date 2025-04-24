package com.example.hms.repository;

import com.example.hms.entity.Bookings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Bookings, Integer> {
    @Query("SELECT DISTINCT b.status FROM Bookings b")
    List<String> getAllBookingStatus();

    @Query(value = """
    SELECT 
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
}
