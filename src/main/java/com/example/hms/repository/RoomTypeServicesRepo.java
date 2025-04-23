package com.example.hms.repository;

import com.example.hms.entity.RoomTypeServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomTypeServicesRepo extends JpaRepository<RoomTypeServices, Integer> {
    @Query(value = """
        SELECT 
            CONCAT(rt.type, ' ', rt.star, ' sao') AS room_type,
            GROUP_CONCAT(DISTINCT s.service_name ORDER BY s.service_name SEPARATOR ', ') AS service_names,
            GROUP_CONCAT(DISTINCT r.room_number ORDER BY r.room_number SEPARATOR ', ') AS room_numbers
        FROM room_types rt
        LEFT JOIN room_type_services rts ON rt.id = rts.room_type_id
        LEFT JOIN services s ON rts.service_id = s.id
        LEFT JOIN rooms r ON rt.id = r.room_type_id
        GROUP BY rt.id, room_type
        ORDER BY rt.id
    """, nativeQuery = true)
    List<Object[]> fetchServiceManagementData();
}
