package com.example.hms.repository;

import com.example.hms.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<Users, Integer> {
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Users u WHERE u.email = :email " +
            "AND u.isDeleted = false")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT u FROM Users u WHERE u.email = :email " +
            "AND u.isDeleted = false")
    Users findByEmail(@Param("email") String email);

    @Query(value = "SELECT u FROM Users u WHERE u.role = 'user' AND u.isDeleted = false")
    List<Users> fetchCustomerManagementData();

    Users findByClerkUserId(String clerkUserId);
}
