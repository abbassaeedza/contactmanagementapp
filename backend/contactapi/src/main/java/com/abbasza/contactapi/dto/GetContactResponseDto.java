package com.abbasza.contactapi.dto;

import com.abbasza.contactapi.model.type.EmailType;
import com.abbasza.contactapi.model.type.PhoneType;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class GetContactResponseDto {
    private UUID id;
    private String title;
    private String firstName;
    private String lastName;
    private Map<EmailType, String> email;
    private Map<PhoneType, String> phone;
}
