package com.example.hms.controller;

import com.example.hms.service.ServicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServicesController {
    @Autowired
    private ServicesService servicesService;

    @GetMapping("/services/names")
    public List<String> getAllServiceNames() {
        return servicesService.getAllServiceNames();
    }
}
