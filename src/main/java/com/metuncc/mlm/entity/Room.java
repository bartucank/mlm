package com.metuncc.mlm.entity;

import com.metuncc.mlm.api.request.ShelfCreateRequest;
import com.metuncc.mlm.dto.RoomDTO;
import com.metuncc.mlm.dto.ShelfDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "room")
public class Room extends MLMBaseClass{

    private String name;
    private Long imageId;
    private Long quata;

    public RoomDTO toDTO(){
        RoomDTO dto = new RoomDTO();
        dto.setId(getId());
        dto.setName(getName());
        dto.setQuata(getQuata());
        dto.setImageId(getImageId());
        return dto;
    }
}
