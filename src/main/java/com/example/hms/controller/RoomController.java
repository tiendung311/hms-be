package com.example.hms.controller;

import com.example.hms.model.RoomManagementDTO;
import com.example.hms.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @GetMapping("/rooms/status")
    public List<String> getAllRoomStatuses() {
        return roomService.getAllRoomStatuses();
    }

    @GetMapping("/admin/rooms")
    public ResponseEntity<List<RoomManagementDTO>> getRoomManagementList() {
        return ResponseEntity.ok(roomService.getRoomManagementList());
    }
}
