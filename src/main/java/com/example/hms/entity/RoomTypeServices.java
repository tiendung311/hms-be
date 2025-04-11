package com.example.hms.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "room_type_services")
public class RoomTypeServices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomTypes roomType;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Services service;
}
