package com.bayu.billingservice.model.enumerator;

import lombok.Getter;

@Getter
public enum ApprovalStatus {

    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");
    private final String status;

    ApprovalStatus(String status) {
        this.status = status;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isApproved() {
        return this == APPROVED;
    }

    public boolean isRejected() {
        return this == REJECTED;
    }

}
