package com.example.hms.service.impl;

import com.example.hms.model.RoomAvailableDTO;
import com.example.hms.repository.RoomTypeRepo;
import com.example.hms.service.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoomTypeServiceImpl implements RoomTypeService {
    @Autowired
    private RoomTypeRepo roomTypeRepo;

    @Override
    public List<String> getAllRoomTypes() {
        return roomTypeRepo.getAllRoomTypeDisplayNames();
    }

    @Override
    public List<RoomAvailableDTO> findAvailableRooms(String type, Integer star,
                                                     Double minPrice, Double maxPrice,
                                                     LocalDate checkInDate, LocalDate checkOutDate) {
        if (minPrice == null) minPrice = roomTypeRepo.findMinPrice();
        if (maxPrice == null) maxPrice = roomTypeRepo.findMaxPrice();
//        System.out.println("minPrice: " + minPrice);
//        System.out.println("maxPrice: " + maxPrice);

        if (type != null && type.trim().isEmpty()) type = null;

        if (star != null && star == 0) star = null;

        List<Object[]> rawResult = roomTypeRepo.findAvailableRoomTypesRaw(
                type, star, minPrice, maxPrice, checkInDate, checkOutDate
        );

//        System.out.println("Raw result size = " + rawResult.size());
//        for (Object[] row : rawResult) {
//            System.out.println("RoomTypeId = " + row[0] + ", Name = " + row[1] + ", Price = " + row[2]);
//        }

        List<RoomAvailableDTO> result = new ArrayList<>();
        for (Object[] row : rawResult) {
            Integer roomTypeId = (Integer) row[0];
            String roomName = (String) row[1];
            Double price = (Double) row[2];
            List<String> services = roomTypeRepo.findServicesByRoomTypeId(roomTypeId);

            result.add(new RoomAvailableDTO(roomName, services, price, roomTypeId));
        }

        return result;
    }

    @Override
    public Double findMinPrice() {
        return roomTypeRepo.findMinPrice();
    }

    @Override
    public Double findMaxPrice() {
        return roomTypeRepo.findMaxPrice();
    }
}
