package com.example.phonebook_java.util;

import com.example.phonebook_java.config.Constant;
import com.example.phonebook_java.dto.ContactDTO;
import com.example.phonebook_java.exception.phonebook_exception.BadPhonebookRequestException;
import com.example.phonebook_java.model.Contact;
import com.example.phonebook_java.model.enums.CountryCode;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


@Slf4j
public class RequestUtil {

    private RequestUtil(){}

    public static boolean isValidPhoneNumberUsing(String phoneNumber, CountryCode countryIsoCode) {
        log.info("Validating phone number with country code {} {}",phoneNumber,countryIsoCode);
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

    public static Contact updateFiled(ContactDTO contactFromReq, Contact contactFromDB) {
        log.info("updating filed");
        Contact updatedContact = new Contact(contactFromDB);
        if(StringUtils.isNotEmpty(contactFromReq.getFirstName()) && !contactFromReq.getFirstName().equals(updatedContact.getFirstName())) {
            log.debug("updating first name from {} to {}", updatedContact.getFirstName(), contactFromReq.getFirstName());
            updatedContact.setFirstName(contactFromReq.getFirstName());
        }
        if(StringUtils.isNotEmpty(contactFromReq.getLastName()) && !contactFromReq.getLastName().equals(updatedContact.getLastName())) {
            log.debug("updating last name from {} to {}", updatedContact.getLastName(),  contactFromReq.getLastName());
            updatedContact.setLastName(contactFromReq.getLastName());
        }
        if(contactFromReq.getCountryCode() != null && !contactFromReq.getCountryCode().equals(updatedContact.getCountryCode())) {
            log.debug("updating country code from {} to {}", updatedContact.getCountryCode(), contactFromReq.getCountryCode());
            updatedContact.setCountryCode(contactFromReq.getCountryCode());
        }
        if(StringUtils.isNotEmpty(contactFromReq.getPhone()) && !contactFromReq.getPhone().equals(updatedContact.getPhone())) {
            log.debug("updating phone from {} to {}", updatedContact.getPhone(), contactFromReq.getPhone());
            updatedContact.setPhone(contactFromReq.getPhone());
        }
        if(contactFromReq.getAddress() != null && !contactFromReq.getAddress().equals(updatedContact.getAddress())) {
            log.debug("updating address from {} to {}", updatedContact.getAddress(), contactFromReq.getAddress());
            updatedContact.setAddress(contactFromReq.getAddress());
        }
        log.info("Updated contact : {}", updatedContact);
        return updatedContact;
    }

}
