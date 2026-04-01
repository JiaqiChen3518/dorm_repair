package com.qg.dorm.dto;

import lombok.Data;

@Data
public class UpdateStatusDTO {
    private Long orderId;
    private Integer status;
}