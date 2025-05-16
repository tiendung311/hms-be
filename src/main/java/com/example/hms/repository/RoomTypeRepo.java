package com.example.hms.repository;

import com.example.hms.entity.RoomTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomTypeRepo extends JpaRepository<RoomTypes, Integer> {
    @Query("SELECT CONCAT(rt.type, ' ', rt.star, ' sao') FROM RoomTypes rt")
    List<String> getAllRoomTypeDisplayNames();

    @Query(value = """
    SELECT DISTINCT rt.id AS room_type_id,
           CONCAT(rt.type, ' ', rt.star, ' sao') AS room_name,
           rt.price_per_night
    FROM room_types rt
    JOIN rooms r ON r.room_type_id = rt.id
    WHERE rt.price_per_night BETWEEN :minPrice AND :maxPrice
      AND (:type IS NULL OR rt.type = :type)
      AND (:star IS NULL OR rt.star = CAST(:star AS CHAR))
      AND r.id NOT IN (
          SELECT b.room_id
          FROM bookings b
          WHERE b.status IN ('Chờ', 'Xác nhận', 'Nhận phòng')
            AND b.check_in_date < :checkOutDate
            AND b.check_out_date > :checkInDate
      )
    """, nativeQuery = true)
    List<Object[]> findAvailableRoomTypesRaw(
            @Param("type") String type,
            @Param("star") Integer star,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    @Query("SELECT s.serviceName FROM RoomTypeServices rts JOIN rts.service s WHERE rts.roomType.id = :roomTypeId")
    List<String> findServicesByRoomTypeId(@Param("roomTypeId") Integer roomTypeId);

    @Query("SELECT MIN(rt.pricePerNight) FROM RoomTypes rt")
    Double findMinPrice();

    @Query("SELECT MAX(rt.pricePerNight) FROM RoomTypes rt")
    Double findMaxPrice();
}
