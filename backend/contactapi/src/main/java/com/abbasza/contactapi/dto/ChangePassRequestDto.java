package com.abbasza.contactapi.dto;

import lombok.Data;

@Data
public class ChangePassRequestDto {
    private String oldpassword;
    private String newpassword;
}
