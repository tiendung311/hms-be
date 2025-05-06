package com.example.hms.service.impl;

import com.example.hms.entity.RoomTypeServices;
import com.example.hms.entity.RoomTypes;
import com.example.hms.entity.Services;
import com.example.hms.model.RoomTypeServiceReqDTO;
import com.example.hms.model.RoomTypeServiceResDTO;
import com.example.hms.model.ServiceManagementDTO;
import com.example.hms.repository.RoomTypeRepo;
import com.example.hms.repository.RoomTypeServicesRepo;
import com.example.hms.repository.ServiceRepo;
import com.example.hms.service.RoomTypeServicesService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class RoomTypeServicesServiceImpl implements RoomTypeServicesService {
    @Autowired
    private RoomTypeServicesRepo roomTypeServicesRepo;

    @Autowired
    private ServiceRepo servicesRepo;

    @Autowired
    private RoomTypeRepo roomTypesRepo;

    @Override
    public List<ServiceManagementDTO> getServiceManagementList() {
        List<Object[]> rawData = roomTypeServicesRepo.fetchServiceManagementData();

        List<ServiceManagementDTO> result = new ArrayList<>();
        for (Object[] row : rawData) {
            Integer serviceId = (Integer) row[0];
            String roomType = (String) row[1];
            List<String> services = row[2] != null ? Arrays.asList(((String) row[2]).split(", ")) : new ArrayList<>();
            List<String> rooms = row[3] != null ? Arrays.asList(((String) row[3]).split(", ")) : new ArrayList<>();
            result.add(new ServiceManagementDTO(serviceId, roomType, services, rooms));
        }
        return result;
    }

    @Override
    public RoomTypeServiceResDTO getRoomTypeServicesById(Integer roomTypeId) {
        Object result = roomTypeServicesRepo.fetchRoomTypeServicesById(roomTypeId);

        if (result == null) {
            throw new RuntimeException("Room type id not found: " + roomTypeId);
        }

        Object[] row = (Object[]) result;

        System.out.println("Row data: " + Arrays.toString(row));

        if (row.length < 2 || row[0] == null) {
            throw new RuntimeException("Room type id not found: " + roomTypeId);
        }

        String roomType = (String) row[0];
        List<String> services = row[1] != null
                ? Arrays.asList(((String) row[1]).split(", "))
                : new ArrayList<>();

        return new RoomTypeServiceResDTO(roomType, services);
    }

    @Override
    @Transactional
    public void updateRoomTypeServices(Integer roomTypeId, RoomTypeServiceReqDTO dto) {
        RoomTypes roomType = roomTypesRepo.findById(roomTypeId)
                .orElseThrow(() -> new RuntimeException("Room type not found"));

        roomTypeServicesRepo.deleteByRoomType(roomType);

        for (String serviceName : dto.getServices()) {
            Services service = servicesRepo.findByServiceName(serviceName);
            if (service != null) {
                RoomTypeServices rts = new RoomTypeServices();
                rts.setRoomType(roomType);
                rts.setService(service);
                roomTypeServicesRepo.save(rts);
            }
        }
    }
}
