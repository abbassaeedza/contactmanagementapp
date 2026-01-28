package com.abbasza.contactapi.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String username;
    private String password;
}
