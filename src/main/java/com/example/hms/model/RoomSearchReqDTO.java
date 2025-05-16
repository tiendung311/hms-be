package com.example.hms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomSearchReqDTO {
    private String type;
    private Integer star;
    private Double minPrice;
    private Double maxPrice;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
