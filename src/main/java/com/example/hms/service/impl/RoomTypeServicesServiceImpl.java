package com.example.hms.service.impl;

import com.example.hms.model.ServiceManagementDTO;
import com.example.hms.repository.RoomTypeServicesRepo;
import com.example.hms.service.RoomTypeServicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class RoomTypeServicesServiceImpl implements RoomTypeServicesService {
    @Autowired
    private RoomTypeServicesRepo roomTypeServicesRepo;

    @Override
    public List<ServiceManagementDTO> getServiceManagementList() {
        List<Object[]> rawData = roomTypeServicesRepo.fetchServiceManagementData();

        List<ServiceManagementDTO> result = new ArrayList<>();
        for (Object[] row : rawData) {
            String roomType = (String) row[0];
            List<String> services = row[1] != null ? Arrays.asList(((String) row[1]).split(", ")) : new ArrayList<>();
            List<String> rooms = row[2] != null ? Arrays.asList(((String) row[2]).split(", ")) : new ArrayList<>();
            result.add(new ServiceManagementDTO(roomType, services, rooms));
        }
        return result;
    }
}
