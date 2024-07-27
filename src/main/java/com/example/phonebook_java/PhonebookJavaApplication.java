package com.example.phonebook_java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PhonebookJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhonebookJavaApplication.class, args);
    }

}
