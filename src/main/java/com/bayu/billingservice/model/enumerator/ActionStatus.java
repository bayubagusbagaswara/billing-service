package com.bayu.billingservice.model.enumerator;

public enum ActionStatus {

    ADD("ADD"),
    EDIT("EDIT"),
    DELETE("DELETE");

    private final String status;

    ActionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
