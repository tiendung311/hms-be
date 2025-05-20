package com.example.hms.controller;

import com.example.hms.model.RoomAvailableDTO;
import com.example.hms.model.RoomSearchReqDTO;
import com.example.hms.model.RoomTypeCountDTO;
import com.example.hms.service.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RoomTypeController {
    @Autowired
    private RoomTypeService roomTypeService;

    @GetMapping("/room-types")
    public List<String> getAllRoomTypes() {
        return roomTypeService.getAllRoomTypes();
    }

    @PostMapping("/available-rooms")
    public List<RoomAvailableDTO> findAvailableRooms(@RequestBody RoomSearchReqDTO request) {
        return roomTypeService.findAvailableRooms(
                request.getType(),
                request.getStar(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getCheckInDate(),
                request.getCheckOutDate()
        );
    }

    @GetMapping("/room/min-price")
    public Double findMinPrice() {
        return roomTypeService.findMinPrice();
    }

    @GetMapping("/room/max-price")
    public Double findMaxPrice() {
        return roomTypeService.findMaxPrice();
    }

    @GetMapping("/room-types/count")
    public List<RoomTypeCountDTO> countRoomsByRoomType() {
        return roomTypeService.countRoomsByRoomType();
    }
}
