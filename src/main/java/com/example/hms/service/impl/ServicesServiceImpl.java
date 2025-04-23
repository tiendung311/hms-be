package com.example.hms.service.impl;

import com.example.hms.repository.ServiceRepo;
import com.example.hms.service.ServicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicesServiceImpl implements ServicesService {
    @Autowired
    private ServiceRepo serviceRepo;

    @Override
    public List<String> getAllServiceNames() {
        return serviceRepo.findAllServiceNames();
    }
}
