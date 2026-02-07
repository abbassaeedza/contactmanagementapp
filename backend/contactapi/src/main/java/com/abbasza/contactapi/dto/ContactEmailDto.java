package com.abbasza.contactapi.dto;

import com.abbasza.contactapi.model.type.EmailType;
import lombok.Data;

@Data
public class ContactEmailDto {
    private EmailType emailtype;
    private String emailvalue;
}
