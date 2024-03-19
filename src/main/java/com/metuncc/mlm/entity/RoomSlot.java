package com.metuncc.mlm.entity;


import com.metuncc.mlm.dto.RoomSlotDTO;
import com.metuncc.mlm.dto.RoomSlotWithResDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import com.metuncc.mlm.entity.enums.RoomSlotDays;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class RoomSlot extends MLMBaseClass{

    private LocalTime startHour;
    private LocalTime endHour;
    @Enumerated(value = EnumType.STRING)
    private RoomSlotDays day;
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    public RoomSlotDTO toDto(){
        RoomSlotDTO dto = new RoomSlotDTO();
        dto.setId(getId());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        dto.setStartHour(getStartHour().format(dateTimeFormatter));
        dto.setEndHour(getEndHour().format(dateTimeFormatter));
        dto.setDay(getDay());
        dto.setAvailable(getAvailable());
        return dto;
    }
    public RoomSlotWithResDTO toResDto(){
        RoomSlotWithResDTO dto = new RoomSlotWithResDTO();
        dto.setId(getId());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        dto.setStartHour(getStartHour().format(dateTimeFormatter));
        dto.setEndHour(getEndHour().format(dateTimeFormatter));
        dto.setDay(getDay());
        dto.setAvailable(getAvailable());
        return dto;
    }

}
