package com.bayu.billingservice.model.enumerator;

public enum BillingCategory {

    FUND("FUND"),
    CORE("CORE"),

    RETAIL("RETAIL"),
    ;

    private final String value;

    BillingCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
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
