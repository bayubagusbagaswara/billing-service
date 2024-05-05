package com.bayu.billingservice.model.enumerator;

import lombok.Getter;

@Getter
public enum Currency {

    IDR("IDR"),
    USD("USD");

    private final String value;

    Currency(String value) {
        this.value = value;
    }

}
