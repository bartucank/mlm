package com.metuncc.mlm.entity.enums;

public enum BookCategory {
    FICTION("Fiction"),
    NONFICTION("Non-Fiction")

    ;

    private String desc;

    BookCategory(String s) {
        this.desc=s;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}