package com.qg.dorm.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String account;
    private String password;
    private Integer role;
    private String captcha;
    private String sessionId;
}