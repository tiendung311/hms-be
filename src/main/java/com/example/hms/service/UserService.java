package com.example.hms.service;

import com.example.hms.entity.Users;

import java.util.Map;

public interface UserService {
    void save(Users user);
    void updateUserInfoByEmail(String currentEmail, String newFirstName, String newLastName,
                               String newEmail, String newRole);
    void handleClerkWebhook(Map<String, Object> payload);
}
