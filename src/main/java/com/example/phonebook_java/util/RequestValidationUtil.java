package com.example.phonebook_java.util;

import com.example.phonebook_java.config.Constant;
import com.example.phonebook_java.exception.phonebook_exception.BadPhonebookRequestException;
import com.example.phonebook_java.model.enums.CountryCode;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RequestValidationUtil {

    private RequestValidationUtil(){}

    public static boolean isValidPageAndSize(int page, int size){
        List<String> errors = new ArrayList<>();
        if(page < 0)
            errors.add(Constant.PAGE_VALUE_ERROR+page);
        if(size < 0)
            errors.add(Constant.SIZE_VALUE_ERROR+page);
        if(size > 10)
            errors.add(Constant.SIZE_LIMIT_ERROR+size);
        if (!CollectionUtils.isEmpty(errors)) {
            log.error(String.join("\n", errors));
            throw new BadPhonebookRequestException(String.join("\n", errors));
        }
        return true;
    }

    public static boolean isValidPhoneNumberUsing(String phoneNumber, CountryCode countryIsoCode) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, countryIsoCode.getCountryName());
            if (!phoneUtil.isValidNumber(numberProto))
                throw new BadPhonebookRequestException(Constant.PHONE_NUMBER_ERROR);
        } catch (NumberParseException e) {
            log.error("NumberParseException was thrown: " + e.toString());
            throw new BadPhonebookRequestException(Constant.PHONE_NUMBER_ERROR);
        }
        return true;
    }

}
