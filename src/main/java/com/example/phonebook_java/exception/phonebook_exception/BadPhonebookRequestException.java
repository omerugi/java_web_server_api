package com.example.phonebook_java.exception.phonebook_exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadPhonebookRequestException extends RuntimeException {
    public BadPhonebookRequestException(String message) {
        super(message);
    }
}
