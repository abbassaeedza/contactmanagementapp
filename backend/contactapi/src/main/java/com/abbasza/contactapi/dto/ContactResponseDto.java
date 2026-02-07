package com.abbasza.contactapi.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ContactResponseDto {
    private UUID id;
    private String title;
    private String firstname;
    private String lastname;
}

