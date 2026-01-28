package com.abbasza.contactapi.dto;

import lombok.Data;

@Data
public class SignupRequestDto {
    private String email;
    private String phone;
    private String firstname;
    private String lastname;
    private String password;
}
