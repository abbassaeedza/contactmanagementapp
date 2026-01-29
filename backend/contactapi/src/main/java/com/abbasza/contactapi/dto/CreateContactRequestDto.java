package com.abbasza.contactapi.dto;

import com.abbasza.contactapi.model.type.EmailType;
import com.abbasza.contactapi.model.type.PhoneType;
import lombok.Data;

import java.util.Map;

@Data
public class CreateContactRequestDto {
    private String title;
    private String firstName;
    private String lastName;
    private Map<EmailType, String> email;
    private Map<PhoneType, String> phone;
}
