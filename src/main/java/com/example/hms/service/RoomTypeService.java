package com.example.hms.service;

import com.example.hms.model.RoomAvailableDTO;

import java.time.LocalDate;
import java.util.List;

public interface RoomTypeService {
    List<String> getAllRoomTypes();

    List<RoomAvailableDTO> findAvailableRooms(String type, Integer star,
                                              Double minPrice, Double maxPrice,
                                              LocalDate checkInDate, LocalDate checkOutDate);

    Double findMinPrice();

    Double findMaxPrice();
}
