package com.example.hms.repository;

import com.example.hms.entity.Bookings;
import com.example.hms.entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<Payments, Integer> {
    @Query("SELECT DISTINCT p.paymentStatus FROM Payments p")
    List<String> getAllPaymentStatuses();

    @Query("SELECT DISTINCT p.paymentMethod FROM Payments p")
    List<String> getAllPaymentMethods();

    @Query(value = """
    SELECT 
        p.id AS transaction_id,
        CONCAT(u.first_name, ' ', u.last_name) AS full_name,
        r.room_number,
        p.payment_date,
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

    @Query(value = """
    SELECT 
        p.id AS transaction_id,
        CONCAT(u.first_name, ' ', u.last_name) AS full_name,
        r.room_number,
        p.payment_date,
        p.payment_method,
        p.payment_status,
        p.amount
    FROM payments p
    JOIN bookings b ON p.booking_id = b.id
    JOIN users u ON b.customer_id = u.id
    JOIN rooms r ON b.room_id = r.id
    WHERE p.id = :transactionId
    """, nativeQuery = true)
    Object fetchPaymentDetailById(@Param("transactionId") Integer transactionId);

    Optional<Payments> findById(Integer id);

    @Query(value = "SELECT booking_id FROM payments WHERE id = :transactionId", nativeQuery = true)
    Integer findBookingIdByTransactionId(@Param("transactionId") Integer transactionId);

    Optional<Payments> findFirstByBooking_IdAndPaymentStatus(Integer bookingId, String status);

    Optional<Payments> findByOrderCode(Long orderCode);

    @Query(value = "SELECT SUM(amount) FROM payments WHERE payment_status = :status", nativeQuery = true)
    BigDecimal findTotalAmountByStatus(String status);

    @Query(value = """
    SELECT SUM(p.amount)
    FROM payments p
    WHERE p.payment_status = :status
      AND MONTH(p.payment_date) = :month
      AND YEAR(p.payment_date) = :year
    """, nativeQuery = true)
    BigDecimal findTotalAmountByMonth(
            @Param("status") String status,
            @Param("month") int month,
            @Param("year") int year
    );
}
