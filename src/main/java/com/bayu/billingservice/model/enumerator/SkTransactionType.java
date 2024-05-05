package com.bayu.billingservice.model.enumerator;

import lombok.Getter;

@Getter
public enum SkTransactionType {

    TRANSACTION_BI_SSSS("BI-SSSS"),
    TRANSACTION_CBEST("CBEST");

    private final String value;

    SkTransactionType(String value) {
        this.value = value;
    }

}
