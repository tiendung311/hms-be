package com.example.hms.repository;

import com.example.hms.entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepo extends JpaRepository<Payments, Integer> {
    @Query("SELECT DISTINCT p.paymentStatus FROM Payments p")
    List<String> getAllPaymentStatuses();

    @Query("SELECT DISTINCT p.paymentMethod FROM Payments p")
    List<String> getAllPaymentMethods();

    @Query(value = """
    SELECT 
        CONCAT(u.first_name, ' ', u.last_name) AS full_name,
        r.room_number,
        p.created_at AS payment_date,
        p.payment_method,
        p.payment_status,
        p.amount
    FROM payments p
    JOIN bookings b ON p.booking_id = b.id
    JOIN users u ON b.customer_id = u.id
    JOIN rooms r ON b.room_id = r.id
    ORDER BY p.created_at DESC
    """, nativeQuery = true)
    List<Object[]> fetchPaymentManagementData();
}
