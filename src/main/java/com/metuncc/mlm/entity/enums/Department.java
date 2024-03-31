package com.metuncc.mlm.entity.enums;


public enum Department {
    BUS("BUS"),
    ECO("ECO"),
    PSIR("PSIR"),
    GPC("GPC"),
    EFL("EFL"),
    PSYC("PSYC"),
    ASE("ASE"),
    CNG("CNG"),
    CHME("CHME"),
    CVE("CVE"),
    INE("INE"),
    SNG("SNG"),
    EEE("EEE"),
    MECH("MECH"),
    PNGE("PNGE"),

    ;

    private String desc;

    Department(String s) {
        this.desc=s;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}