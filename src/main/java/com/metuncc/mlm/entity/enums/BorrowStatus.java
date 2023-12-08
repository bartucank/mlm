package com.metuncc.mlm.entity.enums;


public enum BorrowStatus {
    WAITING_TAKE("Waiting to take"),
    WAITING_RETURN("Waiting to return"),

    RETURNED("Book returned")

    ;

    private String desc;

    BorrowStatus(String s) {
        this.desc=s;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}