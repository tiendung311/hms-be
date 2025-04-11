package com.example.hms.repository;

import com.example.hms.entity.RoomTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeRepo extends JpaRepository<RoomTypes, Integer> {
}
