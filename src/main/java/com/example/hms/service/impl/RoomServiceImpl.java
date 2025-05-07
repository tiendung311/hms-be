package com.example.hms.service.impl;

import com.example.hms.entity.Rooms;
import com.example.hms.model.RoomManagementDTO;
import com.example.hms.repository.RoomRepo;
import com.example.hms.service.RoomService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomRepo roomRepo;

    @Override
    public List<String> getAllRoomStatuses() {
        return roomRepo.getAllRoomStatus();
    }

    @Override
    public List<RoomManagementDTO> getRoomManagementList() {
        List<Object[]> rawData = roomRepo.fetchRoomManagementData();

        List<RoomManagementDTO> result = new ArrayList<>();
        for (Object[] row : rawData) {
            String roomNumber = (String) row[0];
            String roomType = (String) row[1];
            List<String> roomServices = row[2] != null ? List.of(((String) row[2]).split(", ")) : new ArrayList<>();
            String roomStatus = (String) row[3];

            result.add(new RoomManagementDTO(roomNumber, roomType, roomServices, roomStatus));
        }
        return result;
    }

    @Override
    @Transactional
    public void toggleRoomMaintenance(String roomNumber) {
        Rooms room = roomRepo.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        String currentStatus = room.getRoomStatus();

        if ("Trống".equalsIgnoreCase(currentStatus)) {
            room.setRoomStatus("Bảo trì");
            room.setUpdatedAt(LocalDateTime.now());
        } else if ("Bảo trì".equalsIgnoreCase(currentStatus)) {
            room.setRoomStatus("Trống");
            room.setUpdatedAt(LocalDateTime.now());
        } else {
            throw new IllegalStateException("Cannot toggle status while the current status: " + currentStatus);
        }
        roomRepo.save(room);
    }
}
