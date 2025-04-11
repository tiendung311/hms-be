package com.example.hms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Notifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Users customer;

    private String message;
    private String errorMessage;

    private LocalDateTime scheduledAt;

    private Boolean isSent = false;

    private LocalDateTime sentAt;
}
