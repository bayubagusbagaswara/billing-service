package com.bayu.billingservice.model.enumerator;

import lombok.Getter;

@Getter
public enum BillingStatus {

    GENERATED("GENERATED"),
    REVIEWED("REVIEWED"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private final String status;

    BillingStatus(String status) {
        this.status = status;
    }
}
