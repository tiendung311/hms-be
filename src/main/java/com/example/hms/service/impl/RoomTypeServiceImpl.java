package com.example.hms.service.impl;

import com.example.hms.repository.RoomTypeRepo;
import com.example.hms.service.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomTypeServiceImpl implements RoomTypeService {
    @Autowired
    private RoomTypeRepo roomTypeRepo;

    @Override
    public List<String> getAllRoomTypes() {
        return roomTypeRepo.getAllRoomTypeDisplayNames();
    }
}
