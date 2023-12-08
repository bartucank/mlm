package com.metuncc.mlm.entity.enums;


public enum QueueStatus {
    ACTIVE("Active"),
    END("End")

    ;

    private String desc;

    QueueStatus(String s) {
        this.desc=s;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}