package com.abbasza.contactapi.dto;

import com.abbasza.contactapi.model.type.PhoneType;
import lombok.Data;

@Data
public class ContactPhoneDto {
    private PhoneType phonetype;
    private String phonevalue;
}
