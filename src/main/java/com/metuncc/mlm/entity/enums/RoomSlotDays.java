package com.metuncc.mlm.entity.enums;

public enum RoomSlotDays {
    MON(1),
    TUE(2),
    WED(3),
    THU(4),
    FRI(5),
    SAT(6),
    SUN(7);

    private int value;

    public int getValue() {
        return value;
    }

    RoomSlotDays(int s) {
        this.value=s;
    }

    public static RoomSlotDays fromValue(int value){
        switch (value){
            case 1:
                return MON;
            case 2:
                return TUE;
            case 3:
                return WED;
            case 4:
                return THU;
            case 5:
                return FRI;
            case 6:
                return SAT;
            case 7:
                return SUN;
        }
        return null;
    }

}
