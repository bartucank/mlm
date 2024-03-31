package com.metuncc.mlm.entity.enums;


public enum VerificationType {

    REGISTER("FOR REGISTER"),
    RESET_PASSWORD("FOR RESET PASSWORD")

    ;

    private String desc;

    VerificationType(String s) {
        this.desc=s;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}