package com.metuncc.mlm.dto;

import com.metuncc.mlm.entity.enums.RoomSlotDays;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@EqualsAndHashCode(callSuper=true)
public class RoomSlotWithResDTO extends MLMBaseClassDTO{
    private String startHour;
    private String endHour;
    private RoomSlotDays day;
    private Boolean available;


    private int dayInt;
    private RoomReservationDTO reservationDTO;
}
