package com.metuncc.mlm.dto;

import com.metuncc.mlm.entity.RoomSlot;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=true)
public class RoomReservationDTO extends MLMBaseClassDTO{
    private LocalDate date;
    private RoomSlot roomSlot;
    private Long userId;
    private Boolean approved;
}
