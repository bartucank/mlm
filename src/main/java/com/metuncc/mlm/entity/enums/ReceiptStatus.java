package com.metuncc.mlm.entity.enums;

public enum ReceiptStatus {
    APPROVED("Approved"),
    NOT_APPROVED("Not Approved Yet"),
    REJECTED("Rejected")

    ;

    private String desc;

    ReceiptStatus(String s) {
        this.desc=s;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}