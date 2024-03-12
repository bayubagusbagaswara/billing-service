package com.bayu.billingservice.model.enumerator;

public enum SkTransactionType {

    TRANSACTION_BI_SSSS("BI-SSSS"),
    TRANSACTION_CBEST("CBEST");

    private final String value;

    SkTransactionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
