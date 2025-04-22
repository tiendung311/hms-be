package com.example.hms.controller;

import com.example.hms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/webhook")
    public void handleClerkWebhook(@RequestBody Map<String, Object> payload) {
        userService.handleClerkWebhook(payload);
    }
}
