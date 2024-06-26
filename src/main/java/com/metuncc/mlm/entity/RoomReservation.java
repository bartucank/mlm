package com.metuncc.mlm.entity;


import com.metuncc.mlm.dto.RoomReservationDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class RoomReservation extends MLMBaseClass{

    private LocalDate date;
    @OneToOne
    private RoomSlot roomSlot;
    private Long userId;
    private Boolean approved;

    public RoomReservationDTO toDTO(){
        RoomReservationDTO dto = new RoomReservationDTO();
        dto.setId(getId());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM");
        dto.setDate(getDate().format(dateTimeFormatter));
        dto.setUserId(getUserId());
        dto.setApproved(getApproved());
        return dto;
    }
}
