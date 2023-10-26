package com.metuncc.mlm.entity.enums;

public enum Role {
    USER("Student"),
    LIB("Librarian")

    ;

    private String desc;

    Role(String s) {
        this.desc=s;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}