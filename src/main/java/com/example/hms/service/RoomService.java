package com.example.hms.service;

import com.example.hms.model.RoomManagementDTO;

import java.util.List;

public interface RoomService {
    List<String> getAllRoomStatuses();

    List<RoomManagementDTO> getRoomManagementList();
}
