package com.example.hms.controller;

import com.example.hms.model.RoomManagementDTO;
import com.example.hms.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // api to get room type by room number
    @GetMapping("/admin/rooms/{roomNumber}")
    public ResponseEntity<Map<String, String>> getRoomTypeByRoomNumber(@PathVariable String roomNumber) {
        String roomType = roomService.getRoomTypeByRoomNumber(roomNumber);
        if (roomType != null) {
            Map<String, String> response = new HashMap<>();
            response.put("roomType", roomType);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Không tìm thấy phòng"));
    }

    @GetMapping("/admin/rooms/available")
    public List<String> getAvailableRooms(@RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                              LocalDate checkInDate,
                                          @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                          LocalDate checkOutDate) {
        return roomService.getAvailableRooms(checkInDate, checkOutDate);
    }
}
