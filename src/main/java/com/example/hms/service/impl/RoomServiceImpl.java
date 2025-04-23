package com.example.hms.service.impl;

import com.example.hms.model.RoomManagementDTO;
import com.example.hms.repository.RoomRepo;
import com.example.hms.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
