package com.bayu.billingservice.model.enumerator;

import lombok.Getter;

@Getter
public enum BillingCategory {

    FUND("FUND"),
    CORE("CORE"),

    RETAIL("RETAIL"),
    ;

    private final String value;

    BillingCategory(String value) {
        this.value = value;
    }

    public boolean isFund() {
        return this == FUND;
    }

    public boolean isCore() {
        return this == CORE;
    }

    public boolean isRetail() {
        return this == RETAIL;
    }

}
