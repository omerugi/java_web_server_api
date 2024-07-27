package com.example.phonebook_java.dto;


import com.example.phonebook_java.config.Constant;
import com.example.phonebook_java.model.enums.CountryCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ContactUpdateDTO {
    private String firstName;
    private String lastName;
    @Pattern(regexp = "^\\+?(\\d{1,3})?[- ]?\\(?(\\d{1,4})\\)?[- ]?(\\d{1,4})[- ]?(\\d{1,4})[- ]?(\\d{1,9})$"
            , message = Constant.PHONE_NUMBER_ERROR)
    private String phone;
    private CountryCode countryCode;

}
