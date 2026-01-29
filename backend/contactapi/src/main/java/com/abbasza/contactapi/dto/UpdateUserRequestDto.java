package com.abbasza.contactapi.dto;

import lombok.Data;

@Data
public class UpdateUserRequestDto {
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
}
