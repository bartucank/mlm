package com.metuncc.mlm.entity;

import com.metuncc.mlm.dto.RoomDTO;
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

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room roomId;
}
