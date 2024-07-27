package com.example.phonebook_java.dto;


import com.example.phonebook_java.config.Constant;
import com.example.phonebook_java.model.enums.CountryCode;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class ContactDTO {
    private Long id;
    @NotBlank(message = Constant.FIRST_NAME_REQUIRED)
    private String firstName;
    @NotBlank(message = Constant.LAST_NAME_REQUIRED)
    private String lastName;
    @NotBlank(message = Constant.PHONE_REQUIRED)
    @Pattern(regexp = "^\\+?(\\d{1,3})?[- ]?\\(?(\\d{1,4})\\)?[- ]?(\\d{1,4})[- ]?(\\d{1,4})[- ]?(\\d{1,9})$"
            ,message = Constant.PHONE_NUMBER_ERROR)
    private String phone;
    private CountryCode countryCode = CountryCode.IL;
    private String address;
}
