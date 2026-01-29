package com.abbasza.contactapi.dto;

import lombok.Data;

@Data
public class SignupRequestDto {
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String password;
}
