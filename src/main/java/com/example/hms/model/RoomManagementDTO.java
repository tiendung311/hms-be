package com.example.hms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomManagementDTO {
    private String roomNumber;
    private String roomType;
    private List<String> roomServices;
    private String roomStatus;
}
