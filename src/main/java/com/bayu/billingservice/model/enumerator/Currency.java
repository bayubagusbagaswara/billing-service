package com.bayu.billingservice.model.enumerator;

public enum Currency {

    IDR("IDR"),
    USD("USD");

    private final String value;

    Currency(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
