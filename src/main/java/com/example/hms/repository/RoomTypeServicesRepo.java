package com.example.hms.repository;

import com.example.hms.entity.RoomTypeServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeServicesRepo extends JpaRepository<RoomTypeServices, Integer> {
}
