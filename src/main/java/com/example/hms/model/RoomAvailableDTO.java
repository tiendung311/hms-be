package com.example.hms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomAvailableDTO {
    // roomName = type + star + " sao"
    private String roomName;
    private List<String> services;
    private Double price;
}
