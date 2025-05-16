package com.example.hms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomCardDTO {
    private String roomName;
    private String type;
    private String star;
    private List<String> services;
    private Double price;
}
