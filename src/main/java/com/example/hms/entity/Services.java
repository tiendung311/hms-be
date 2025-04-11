package com.example.hms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Services {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String serviceName;

    @OneToMany(mappedBy = "service")
    private List<RoomTypeServices> roomTypeServices;
}
