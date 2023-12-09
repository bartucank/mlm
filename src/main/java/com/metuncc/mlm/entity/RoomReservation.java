package com.metuncc.mlm.entity;


import com.metuncc.mlm.dto.RoomReservationDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class RoomReservation extends MLMBaseClass{

    private LocalDate date;
    private RoomSlot roomSlot;
    private Long userId;

    public RoomReservationDTO toDTO(){
        RoomReservationDTO dto = new RoomReservationDTO();
        dto.setId(getId());
        dto.setDate(getDate());
        dto.setRoomSlot(getRoomSlot());
        dto.setUserId(getUserId());
        return dto;
    }
}
