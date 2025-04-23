package com.example.hms.repository;

import com.example.hms.entity.RoomTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomTypeRepo extends JpaRepository<RoomTypes, Integer> {
    @Query("SELECT CONCAT(rt.type, ' ', rt.star, ' sao') FROM RoomTypes rt")
    List<String> getAllRoomTypeDisplayNames();
}
