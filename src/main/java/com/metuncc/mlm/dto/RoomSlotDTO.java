package com.metuncc.mlm.dto;

import com.metuncc.mlm.entity.RoomSlot;
import com.metuncc.mlm.entity.enums.RoomSlotDays;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=true)
public class RoomSlotDTO extends MLMBaseClassDTO{
    private LocalTime startHour;
    private LocalTime endHour;
    @Enumerated(value = EnumType.STRING)
    private RoomSlotDays day;
    private Boolean available;
}
