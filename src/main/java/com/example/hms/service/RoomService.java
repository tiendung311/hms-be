package com.example.hms.service;

import com.example.hms.model.RoomManagementDTO;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {
    List<String> getAllRoomStatuses();

    List<RoomManagementDTO> getRoomManagementList();

    void toggleRoomMaintenance(String roomNumber);

    List<String> getAllEmptyRoom();

    String getRoomTypeByRoomNumber(String roomNumber);

    List<String> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate);

    Integer countAllRoom();
}
