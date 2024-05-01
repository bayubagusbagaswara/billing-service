package com.bayu.billingservice.model.enumerator;

import lombok.Getter;

@Getter
public enum ChangeAction {

    ADD("ADD"),
    EDIT("EDIT"),
    DELETE("DELETE");

    private final String action;

    ChangeAction(String action) {
        this.action = action;
    }
}
