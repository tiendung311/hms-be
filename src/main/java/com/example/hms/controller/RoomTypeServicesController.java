package com.example.hms.controller;

import com.example.hms.model.ServiceManagementDTO;
import com.example.hms.service.RoomTypeServicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
