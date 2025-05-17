package com.example.hms.repository;

import com.example.hms.entity.Rooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepo extends JpaRepository<Rooms, Integer> {
    @Query("SELECT DISTINCT r.roomStatus FROM Rooms r")
    List<String> getAllRoomStatus();

    @Query(value = """
    SELECT 
        r.room_number,
        CONCAT(rt.type, ' ', rt.star, ' sao') AS room_type,
        GROUP_CONCAT(DISTINCT s.service_name ORDER BY s.service_name SEPARATOR ', ') AS service_names,
        r.room_status
    FROM rooms r
    JOIN room_types rt ON r.room_type_id = rt.id
    LEFT JOIN room_type_services rts ON rt.id = rts.room_type_id
    LEFT JOIN services s ON rts.service_id = s.id
    GROUP BY r.id, room_type, r.room_status
    ORDER BY r.id
    """, nativeQuery = true)
    List<Object[]> fetchRoomManagementData();

    Optional<Rooms> findByRoomNumber(String roomNumber);

    @Query("SELECT r.roomNumber FROM Rooms r WHERE r.roomStatus = 'Trống'")
    List<String> getAllEmptyRoom();

    @Query("""
    SELECT r.roomNumber FROM Rooms r
    WHERE r.id NOT IN (
        SELECT b.room.id FROM Bookings b
        WHERE b.status IN ('Chờ', 'Xác nhận', 'Nhận phòng')
        AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate
        )
    """)
    List<String> findAvailableRoomNumbers(@Param("checkInDate") LocalDate checkInDate,
                                          @Param("checkOutDate") LocalDate checkOutDate);

    @Query("SELECT r FROM Rooms r WHERE r.roomType.id = :roomTypeId AND r.roomNumber IN :availableRoomNumbers")
    List<Rooms> findAvailableRoomByRoomType(@Param("roomTypeId") Integer roomTypeId,
                                            @Param("availableRoomNumbers") List<String> availableRoomNumbers);
}
