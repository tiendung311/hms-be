package com.example.hms.controller;

import com.example.hms.model.RoomManagementDTO;
import com.example.hms.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/admin/rooms/{roomNumber}/toggle-maintenance")
    public ResponseEntity<String> toggleRoomMaintenance(@PathVariable String roomNumber) {
        try {
            roomService.toggleRoomMaintenance(roomNumber);
            return ResponseEntity.ok("Toggle status successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/admin/rooms/empty")
    public List<String> getAllEmptyRoom() {
        return roomService.getAllEmptyRoom();
    }
}
