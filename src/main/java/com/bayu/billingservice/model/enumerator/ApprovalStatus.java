package com.bayu.billingservice.model.enumerator;

public enum ApprovalStatus {

    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");
    private final String status;

    ApprovalStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
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
