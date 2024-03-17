package com.bayu.billingservice.model.enumerator;

public enum BillingTemplate {

    FUND_TEMPLATE("FUND_TEMPLATE"),
    CORE_TEMPLATE_1("CORE_TEMPLATE_1"),
    CORE_TEMPLATE_2("CORE_TEMPLATE_2"),
    CORE_TEMPLATE_3("CORE_TEMPLATE_3"),
    CORE_TEMPLATE_4("CORE_TEMPLATE_4"),
    CORE_TEMPLATE_5("CORE_TEMPLATE_5"),
    CORE_TEMPLATE_6("CORE_TEMPLATE_6"),
    CORE_TEMPLATE_7("CORE_TEMPLATE_7"),
    CORE_TEMPLATE_8("CORE_TEMPLATE_8"),
    RETAIL_TEMPLATE_1_IDR("RETAIL_TEMPLATE_1_IDR"),
    RETAIL_TEMPLATE_1_USD("RETAIL_TEMPLATE_1_USD"),
    RETAIL_TEMPLATE_2_IDR("RETAIL_TEMPLATE_2_IDR"),
    RETAIL_TEMPLATE_2_USD("RETAIL_TEMPLATE_2_USD"),
    RETAIL_TEMPLATE_3_IDR("RETAIL_TEMPLATE_3_IDR"),
    RETAIL_TEMPLATE_3_USD("RETAIL_TEMPLATE_3_USD");

    private final String value;

    BillingTemplate(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
