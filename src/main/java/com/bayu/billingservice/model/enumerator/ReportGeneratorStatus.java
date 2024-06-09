package com.bayu.billingservice.model.enumerator;

import lombok.Getter;

@Getter
public enum ReportGeneratorStatus {

    SUCCESS("SUCCESS"),
    FAILED("FAILED");

    private final String status;

    ReportGeneratorStatus(String status) {
        this.status = status;
    }
}
