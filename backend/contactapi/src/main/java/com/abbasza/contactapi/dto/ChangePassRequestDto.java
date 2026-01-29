package com.abbasza.contactapi.dto;

import lombok.Data;

@Data
public class ChangePassRequestDto {
    private String oldPassword;
    private String newPassword;
}
