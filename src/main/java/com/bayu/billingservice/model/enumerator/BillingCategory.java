package com.bayu.billingservice.model.enumerator;

public enum BillingCategory {

    FUND("FUND"),
    CORE("CORE"),

    RETAIL("RETAIL"),
    ;

    private final String category;

    BillingCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

}
