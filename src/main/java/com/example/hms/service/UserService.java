package com.example.hms.service;

import com.example.hms.entity.Users;
import com.example.hms.model.UserManagementDTO;

import java.util.List;
import java.util.Map;

public interface UserService {
    void save(Users user);

    void updateUserInfoByEmail(String currentEmail, String newFirstName, String newLastName,
                               String newEmail, String newRole);

    void handleClerkWebhook(Map<String, Object> payload);

    List<UserManagementDTO> getCustomerManagementList();

    void deleteUserByEmail(String email);
}
