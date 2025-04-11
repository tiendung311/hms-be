package com.example.hms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "room_types")
public class RoomTypes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String type;
    private String star;
    private Double pricePerNight;

    @OneToMany(mappedBy = "roomType")
    private List<Rooms> rooms;

    @OneToMany(mappedBy = "roomType")
    private List<RoomTypeServices> roomTypeServices;
}
