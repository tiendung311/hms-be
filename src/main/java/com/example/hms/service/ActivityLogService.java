package com.example.hms.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

@Service
public class ActivityLogService {
    private final Deque<String> recentActivities = new LinkedList<>();
    private static final int MAX_SIZE = 10;

    public synchronized void log(String message) {
        if (recentActivities.size() >= MAX_SIZE) {
            recentActivities.removeLast();
        }
        recentActivities.addFirst(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")) + " - " + message);
    }

    public List<String> getRecentActivities() {
        return new ArrayList<>(recentActivities);
    }
}

