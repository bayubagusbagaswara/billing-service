package com.bayu.billingservice.model.enumerator;

public enum BillingTemplate {

    FUND_TEMPLATE("FUND_TEMPLATE"),
    CORE_1_TEMPLATE("CORE_1_TEMPLATE"),
    CORE_2_TEMPLATE("CORE_2_TEMPLATE"),
    CORE_3_TEMPLATE("CORE_3_TEMPLATE"),
    CORE_4_TEMPLATE("CORE_4_TEMPLATE"),
    CORE_5_TEMPLATE("CORE_5_TEMPLATE"),
    CORE_6_TEMPLATE("CORE_6_TEMPLATE"),
    CORE_7_TEMPLATE("CORE_7_TEMPLATE"),
    CORE_8_TEMPLATE("CORE_8_TEMPLATE"),
    RETAIL_1_IDR_TEMPLATE("RETAIL_1_IDR_TEMPLATE"),
    RETAIL_1_USD_TEMPLATE("RETAIL_1_USD_TEMPLATE"),
    RETAIL_2_IDR_TEMPLATE("RETAIL_2_IDR_TEMPLATE"),
    RETAIL_2_USD_TEMPLATE("RETAIL_2_USD_TEMPLATE"),
    RETAIL_3_IDR_TEMPLATE("RETAIL_3_IDR_TEMPLATE"),
    RETAIL_3_USD_TEMPLATE("RETAIL_3_USD_TEMPLATE");

    private final String value;

    BillingTemplate(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
