package com.example.phonebook_java.model.enums;

public enum CountryCode {
    US("US"),
    GB("GB"),
    DE("DE"),
    FR("FR"),
    IN("IN"),
    IL("IL"),
    CN("CN"),
    JP("JP"),
    BR("BR"),
    CA("CA");

    private final String countryName;

    CountryCode(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryName() {
        return this.countryName;
    }
}
