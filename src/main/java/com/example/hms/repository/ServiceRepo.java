package com.example.hms.repository;

import com.example.hms.entity.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepo extends JpaRepository<Services, Integer> {
    @Query("SELECT s.serviceName FROM Services s")
    List<String> findAllServiceNames();

    @Query("SELECT s FROM Services s WHERE s.serviceName = :serviceName")
    Services findByServiceName(String serviceName);
}
