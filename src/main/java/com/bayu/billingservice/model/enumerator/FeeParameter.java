package com.bayu.billingservice.model.enumerator;

import lombok.Getter;

@Getter
public enum FeeParameter {

    VAT("VAT"),
    TRANSACTION_HANDLING_IDR("TRANSACTION_HANDLING_IDR"),
    TRANSACTION_HANDLING_USD("TRANSACTION_HANDLING_USD"),
    BI_SSSS("BI-SSSS"),
    KSEI("KSEI"),
    ADMINISTRATION_SET_UP("ADMINISTRATION_SET_UP"),
    SIGNING_REPRESENTATION("SIGNING_REPRESENTATION"),
    SECURITY_AGENT("SECURITY_AGENT"),
    OTHER("OTHER");

    private final String value;

    FeeParameter(String value) {
        this.value = value;
    }

}
