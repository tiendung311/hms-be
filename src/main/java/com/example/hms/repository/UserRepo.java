package com.example.hms.repository;

import com.example.hms.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<Users, Integer> {
    boolean existsByEmail(String email);

    Users findByEmail(String email);

    @Query(value = "SELECT u FROM Users u WHERE u.role = 'user' AND u.isDeleted = false")
    List<Users> fetchCustomerManagementData();
}
