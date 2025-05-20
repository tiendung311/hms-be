package com.example.hms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomTypeCountDTO {
    private Integer roomTypeId;
    private String roomTypeName;
    private Long totalRooms;
}
