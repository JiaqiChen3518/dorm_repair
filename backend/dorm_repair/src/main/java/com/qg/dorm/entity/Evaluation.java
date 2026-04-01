package com.qg.dorm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Evaluation {
    private Long id;
    private Long orderId;
    private Long userId;
    private Integer rating;
    private String content;
    private LocalDateTime createTime;

    private String userName;
    private String orderNo;
}
