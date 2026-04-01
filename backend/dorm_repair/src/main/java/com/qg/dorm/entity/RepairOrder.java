package com.qg.dorm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RepairOrder {
    private Long id;
    private String orderNo;
    private Long userId;
    private String building;
    private String room;
    private Integer deviceType;
    private Integer priority;
    private String description;
    private String images;
    private Integer status;
    private Long handlerId;
    private String handleNote;
    private LocalDateTime createTime;
    private LocalDateTime handleTime;
    private LocalDateTime finishTime;
    private LocalDateTime updateTime;

    private String userName;
    private String handlerName;
    private Integer hasEvaluation;
}
