package com.bayu.billingservice.model.enumerator;

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

    // Getter for abbreviation
    public String getAbbreviation() {
        return abbreviation;
    }

}
