package com.metuncc.mlm.entity.enums;

public enum BookCategory {
    FICTION("Fiction"),
    NONFICTION("Non-Fiction"),
    FANTASY("Fantasy"),
    HORROR("Horror"),
    MYSTERY("Mystery"),
    POETRY("Poetry"),
    ROMANCE("Romance"),
    SCIFI("Science Fiction"),
    THRILLER("Thriller"),
    BIOLOGY("Biology"),
    CHEMISTRY("Chemistry"),
    MATH("Mathematics"),
    PHYSICS("Physics"),
    COMPUTERSCIENCE("Computer Science"),
    FINANCE("Finance"),
    BUSINESS("BUsiness"),
    ECONOMICS("Economics"),
    HISTORY("History"),
    BIOGRAPHY("Biography"),
    PSYCHOLOGY("Psychology"),
    OTHER("Other")
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