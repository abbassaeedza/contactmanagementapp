package com.abbasza.contactapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContactRequestDto {
    private String title;
    private String firstname;
    private String lastname;
    private List<ContactEmailDto> emails;
    private List<ContactPhoneDto> phones;
}
