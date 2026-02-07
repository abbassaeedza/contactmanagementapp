package com.abbasza.contactapi.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ContactDetailResponseDto {
    private UUID id;
    private String title;
    private String firstname;
    private String lastname;
    private List<ContactEmailDto> emails;
    private List<ContactPhoneDto> phones;
}
