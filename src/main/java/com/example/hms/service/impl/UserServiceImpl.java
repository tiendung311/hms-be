package com.example.hms.service.impl;

import com.example.hms.config.ClerkConfig;
import com.example.hms.entity.Users;
import com.example.hms.model.UserManagementDTO;
import com.example.hms.repository.UserRepo;
import com.example.hms.service.UserService;
import jakarta.transaction.Transactional;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ClerkConfig clerkConfig;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.clerk.com/v1")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

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
        System.out.println("🚀 Webhook received: " + payload);
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
            user.setClerkUserId((String) data.get("id"));

            try {
                save(user);
                System.out.println("✅ User created: " + email);
            } catch (RuntimeException e) {
                System.out.println("⚠️ User already exists: " + e.getMessage());
            }
        }

        if ("user.updated".equals(eventType)) {
            String updatedRole = null;
            if (data.get("public_metadata") instanceof Map<?, ?> publicMetadata) {
                updatedRole = (String) publicMetadata.get("role");
            }

            try {
                updateUserInfoByEmail(email, firstName, lastName, email, updatedRole);
                System.out.println("✅ User updated: " + email);
            } catch (RuntimeException e) {
                System.out.println("⚠️ User update failed: " + e.getMessage());
            }
        }

        if ("user.deleted".equals(eventType)) {
            String clerkUserId = (String) data.get("id");
            if (clerkUserId != null) {
                try {
                    Users user = userRepo.findByClerkUserId(clerkUserId);
                    if (user != null) {
                        user.setDeleted(true);
                        user.setUpdatedAt(LocalDateTime.now());
                        userRepo.save(user);
                        System.out.println("✅ User marked deleted in DB: " + user.getEmail());
                    } else {
                        System.out.println("⚠️ User not found in DB with Clerk ID: " + clerkUserId);
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ Error marking user as deleted: " + e.getMessage());
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

            deleteClerkUserByEmail(email);
        }
    }

    @Override
    public Users getCustomerByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    private void deleteClerkUserByEmail(String email) {
        // 1. Gọi API Clerk để tìm userId theo email
        String clerkUserId = getClerkUserIdByEmail(email);
        if (clerkUserId != null) {
            // 2. Gọi DELETE /users/{userId}
            try {
                webClient.delete()
                        .uri("/users/{userId}", clerkUserId)
                        .header("Authorization", "Bearer " + clerkConfig.getSecretKey())
                        .retrieve()
                        .bodyToMono(Void.class)
                        .block();
                System.out.println("✅ Clerk user deleted: " + clerkUserId);
            } catch (Exception e) {
                System.out.println("⚠️ Failed to delete user in Clerk: " + e.getMessage());
            }
        }
    }

    private String getClerkUserIdByEmail(String email) {
        try {
            // GET /users?email_address=email@example.com
            List<Map<String, Object>> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/users")
                            .queryParam("email_address", email)
                            .build())
                    .header("Authorization", "Bearer " + clerkConfig.getSecretKey())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                    .block();

            if (response != null && !response.isEmpty()) {
                return (String) response.get(0).get("id");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error fetching Clerk userId: " + e.getMessage());
        }
        return null;
    }
}
