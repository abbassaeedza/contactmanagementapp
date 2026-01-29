package com.abbasza.contactapi.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class GetSelfResponseDto {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
}
