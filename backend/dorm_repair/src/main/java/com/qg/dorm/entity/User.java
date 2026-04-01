package com.qg.dorm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String account;
    private String password;
    private Integer role;
    private String name;
    private String phone;
    private String building;
    private String room;
    private Integer isBound;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
