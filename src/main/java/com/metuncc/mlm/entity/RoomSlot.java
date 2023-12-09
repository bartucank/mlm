package com.metuncc.mlm.entity;


import com.metuncc.mlm.dto.RoomSlotDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import com.metuncc.mlm.entity.enums.RoomSlotDays;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalTime;

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
    private Room roomId;

    public RoomSlotDTO toDto(){
        RoomSlotDTO dto = new RoomSlotDTO();
        dto.setId(getId());
        dto.setStartHour(getStartHour());
        dto.setEndHour(getEndHour());
        dto.setDay(getDay());
        dto.setAvailable(getAvailable());
        return dto;
    }

}
