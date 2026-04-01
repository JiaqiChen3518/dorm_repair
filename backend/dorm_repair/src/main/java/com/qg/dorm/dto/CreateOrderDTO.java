package com.qg.dorm.dto;

import lombok.Data;

@Data
public class CreateOrderDTO {
    private String building;
    private String room;
    private Integer deviceType;
    private Integer priority;
    private String description;
    private String images;
}