package com.bayu.billingservice.model.enumerator;

import lombok.Getter;

@Getter
public enum MonthEnum {

    JANUARY("Jan"),
    FEBRUARY("Feb"),
    MARCH("Mar"),
    APRIL("Apr"),
    MAY("May"),
    JUNE("Jun"),
    JULY("Jul"),
    AUGUST("Aug"),
    SEPTEMBER("Sep"),
    OCTOBER("Oct"),
    NOVEMBER("Nov"),
    DECEMBER("Dec");

    private final String abbreviation;

    // Constructor
    MonthEnum(String abbreviation) {
        this.abbreviation = abbreviation;
    }

}
