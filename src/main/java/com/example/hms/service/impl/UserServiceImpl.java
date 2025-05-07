package com.example.hms.service.impl;

import com.example.hms.entity.Users;
import com.example.hms.model.UserManagementDTO;
import com.example.hms.repository.UserRepo;
import com.example.hms.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;

    @Override
    public void save(Users user) {
        if (!userRepo.existsByEmail(user.getEmail())) {
            user.setRole("user");
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userRepo.save(user);
        } else {
            throw new RuntimeException("User already exists with this email");
        }
    }

    @Override
    public void updateUserInfoByEmail(String currentEmail, String newFirstName, String newLastName, String newEmail, String newRole) {
        Users user = userRepo.findByEmail(currentEmail);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + currentEmail);
        }

        if (newFirstName != null) user.setFirstName(newFirstName);
        if (newLastName != null) user.setLastName(newLastName);
        if (newEmail != null && !newEmail.equals(currentEmail)) user.setEmail(newEmail);
        if (newRole != null) user.setRole(newRole);

        user.setUpdatedAt(LocalDateTime.now());
        userRepo.save(user);
    }


    @Override
    public void handleClerkWebhook(Map<String, Object> payload) {
        String eventType = (String) payload.get("type");
        Map<String, Object> data = (Map<String, Object>) payload.get("data");

        if (data == null || eventType == null) return;

        // Common data extraction
        String email = "";
        if (data.get("email_addresses") instanceof List<?> emailList && !emailList.isEmpty()) {
            Object emailObj = ((Map<String, Object>) emailList.get(0)).get("email_address");
            if (emailObj != null) email = emailObj.toString();
        }

        String firstName = (String) data.get("first_name");
        String lastName = (String) data.get("last_name");

        if ("user.created".equals(eventType)) {
            Users user = new Users();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPassword(null);

            try {
                save(user);
                System.out.println("✅ User created: " + email);
            } catch (RuntimeException e) {
                System.out.println("⚠️ User already exists: " + e.getMessage());
            }
        }

        if ("user.updated".equals(eventType)) {
            Map<String, Object> publicMetadata = (Map<String, Object>) data.get("public_metadata");
            String updatedRole = publicMetadata != null ? (String) publicMetadata.get("role") : null;

            if (updatedRole != null) {
                try {
                    updateUserInfoByEmail(email, firstName, lastName, email, updatedRole);
                    System.out.println("✅ User updated: " + email);
                } catch (RuntimeException e) {
                    System.out.println("⚠️ User update failed: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public List<UserManagementDTO> getCustomerManagementList() {
        List<Users> users = userRepo.fetchCustomerManagementData();

        List<UserManagementDTO> result = new ArrayList<>();
        for (Users user : users) {
            UserManagementDTO dto = new UserManagementDTO();
            dto.setFullName(user.getFirstName() + " " + user.getLastName());
            dto.setEmail(user.getEmail());
            result.add(dto);
        }
        return result;
    }

    @Override
    @Transactional
    public void deleteUserByEmail(String email) {
        Users user = userRepo.findByEmail(email);
        if (user != null) {
            user.setDeleted(true);
            user.setUpdatedAt(LocalDateTime.now());
            userRepo.save(user);
        }
    }
}
