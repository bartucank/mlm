package com.metuncc.mlm.entity.enums;

public enum BookStatus {
    AVAILABLE("Available"),
    NOT_AVAILABLE("Not Available")

    ;

    private String desc;

    BookStatus(String s) {
        this.desc=s;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}