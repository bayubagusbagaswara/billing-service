package com.bayu.billingservice.model.enumerator;

import lombok.Getter;

@Getter
public enum BillingType {

    TYPE_1("TYPE_1"),
    TYPE_2("TYPE_2"),
    TYPE_3("TYPE_3"),
    TYPE_4("TYPE_4"),
    TYPE_5("TYPE_5"),
    TYPE_6("TYPE_6"),
    TYPE_7("TYPE_7"),
    TYPE_8("TYPE_8"),
    TYPE_9("TYPE_9"),
    TYPE_10("TYPE_10"),
    TYPE_11("TYPE_11"),
    ;
    private final String value;

    BillingType(String value) {
        this.value = value;
    }

}
