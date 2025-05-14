package com.example.hms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Bookings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Users customer;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Rooms room;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private Double totalAmount;

    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
