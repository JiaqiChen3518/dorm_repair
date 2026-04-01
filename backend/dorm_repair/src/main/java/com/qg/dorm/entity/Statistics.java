package com.qg.dorm.entity;

import lombok.Data;

@Data
public class Statistics {
    private Integer total;
    private Integer pending;
    private Integer processing;
    private Integer completed;

}
