package com.bayu.billingservice.model.enumerator;

public enum ApprovalStatus {

    PENDING("PENDING"),
    APPROVED("APPROVED");

    private final String approvalStatusName;

    ApprovalStatus(String approvalStatusName) {
        this.approvalStatusName = approvalStatusName;
    }

    public String getApprovalStatusName() {
        return approvalStatusName;
    }

}
