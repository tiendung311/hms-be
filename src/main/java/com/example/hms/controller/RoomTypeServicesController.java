package com.example.hms.controller;

import com.example.hms.model.RoomCardDTO;
import com.example.hms.model.RoomTypeServiceReqDTO;
import com.example.hms.model.RoomTypeServiceResDTO;
import com.example.hms.model.ServiceManagementDTO;
import com.example.hms.service.RoomTypeServicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoomTypeServicesController {
    @Autowired
    private RoomTypeServicesService roomTypeServicesService;

    @GetMapping("/admin/room-type-services")
    public ResponseEntity<List<ServiceManagementDTO>> getServiceManagementList() {
        return ResponseEntity.ok(roomTypeServicesService.getServiceManagementList());
    }

    @GetMapping("/admin/room-type-services/{id}")
    public ResponseEntity<?> getRoomTypeServicesById(@PathVariable("id") Integer id) {
        RoomTypeServiceResDTO dto = roomTypeServicesService.getRoomTypeServicesById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/admin/room-type-services/{id}")
    public ResponseEntity<?> updateRoomTypeServices(
            @PathVariable("id") Integer id,
            @RequestBody RoomTypeServiceReqDTO request
    ) {
        try {
            roomTypeServicesService.updateRoomTypeServices(id, request);
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi cập nhật: " + e.getMessage());
        }
    }

    @GetMapping("/room-cards")
    public ResponseEntity<List<RoomCardDTO>> getRoomCards() {
        return ResponseEntity.ok(roomTypeServicesService.getRoomCardList());
    }
}
