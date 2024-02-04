package com.bayu.billingservice.model.enumerator;

public enum BillingType {

    TYPE_1("TYPE 1"),
    TYPE_2("TYPE 2"),
    TYPE_3("TYPE 3"),
    TYPE_4("TYPE 4"),
    TYPE_5("TYPE 5"),
    TYPE_6("TYPE 6"),
    TYPE_7("TYPE 7"),
    TYPE_8("TYPE 8"),
    TYPE_9("TYPE 9"),
    TYPE_10("TYPE 10"),
    TYPE_11("TYPE 11"),
    ;

    private final String type;

    BillingType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
