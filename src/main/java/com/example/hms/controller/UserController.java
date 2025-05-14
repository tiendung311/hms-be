package com.example.hms.controller;

import com.example.hms.entity.Users;
import com.example.hms.model.UserManagementDTO;
import com.example.hms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/user/webhook")
    public void handleClerkWebhook(@RequestBody Map<String, Object> payload) {
        userService.handleClerkWebhook(payload);
    }

    @GetMapping("/admin/customers")
    public ResponseEntity<List<UserManagementDTO>> getCustomerManagementList() {
        return ResponseEntity.ok(userService.getCustomerManagementList());
    }

    @DeleteMapping("/admin/customers/{email}")
    public ResponseEntity<Void> deleteUserByEmail(@PathVariable String email) {
        userService.deleteUserByEmail(email);
        return ResponseEntity.noContent().build();
    }

    // api to get customer by email
    @GetMapping("/customers/email/{email}")
    public ResponseEntity<Map<String, String>> getCustomerByEmail(@PathVariable String email) {
        Users user = userService.getCustomerByEmail(email);
        Map<String, String> response = new HashMap<>();
        if (user == null || !user.getRole().equals("user") || user.isDeleted()) {
            response.put("fullName", "Không tìm thấy tên khách hàng");
            return ResponseEntity.ok(response);
        }
        response.put("fullName", user.getFirstName() + " " + user.getLastName());
        return ResponseEntity.ok(response);
    }
}
