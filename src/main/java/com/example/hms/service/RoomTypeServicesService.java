package com.example.hms.service;

import com.example.hms.model.RoomTypeServiceReqDTO;
import com.example.hms.model.RoomTypeServiceResDTO;
import com.example.hms.model.ServiceManagementDTO;

import java.util.List;

public interface RoomTypeServicesService {
    List<ServiceManagementDTO> getServiceManagementList();

    RoomTypeServiceResDTO getRoomTypeServicesById(Integer roomTypeId);

    void updateRoomTypeServices(Integer roomTypeId, RoomTypeServiceReqDTO roomTypeServiceReqDTO);
}
